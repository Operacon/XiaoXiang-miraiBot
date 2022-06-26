/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.service

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.error
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.operacon.XiaoXiang
import org.operacon.component.Chat
import org.operacon.component.GlobalVars
import org.operacon.component.GlobalVars.atMe
import org.operacon.service.GroupCountService.botCount
import org.operacon.service.GroupCountService.repeatCache
import java.io.IOException
import kotlin.random.Random

object ChatBotService {

    suspend fun groupScan(event: GroupMessageEvent): Boolean {
        if (!Chat.enableForGroups and !Chat.enableActiveGroups)
            return false
        if (repeatCache.containsKey(event.group.id) && repeatCache[event.group.id]!!.contentEquals(
                event.message,
                ignoreCase = false, strict = true
            )
        )
            return false
        var text = event.message.content.trim()
        var called = text.contains(atMe)
        text = text.replace(GlobalVars.pagReplace, "").replace(GlobalVars.atReplace, "")
        if (text == "")
            return false
        try {
            if (text.startsWith(Chat.groupKeyword)) {
                text = text.removeRange(0, Chat.groupKeyword.length)
                called = true
            }
            if (called) {
                if (Chat.enableForGroups) {
                    if (Chat.enableActiveGroups and (event.group.id in Chat.activeGroups))
                        groupSendMessage(chat(text, Chat.url[Chat.activeGroups.indexOf(event.group.id)]), event)
                    else
                        groupSendMessage(chat(text, Chat.url[0]), event)
                    return true
                }
                if (Chat.enableActiveGroups and (event.group.id in Chat.activeGroups)) {
                    groupSendMessage(chat(text, Chat.url[Chat.activeGroups.indexOf(event.group.id)]), event)
                    return true
                }
            } else if (Chat.enableActiveGroups && (event.group.id in Chat.activeGroups)) {
                text = chat(text, Chat.url[Chat.activeGroups.indexOf(event.group.id)], true)
                if (Random.nextFloat() <= Chat.replyProb)
                    groupSendMessage(text, event)
            }
        } catch (e: Exception) {
            XiaoXiang.logger.error { "潇小湘 - 聊天机器人出错，检查聊天机器人服务器或者配置文件" }
        }

        return false
    }

    private fun chat(text: String, path: String, active: Boolean = false): String {
        if ((text.length >= 140) and !active)
            return "一句话说那么长不会喘不过气吗"
        val body = "".toRequestBody(GlobalVars.mediaTypePlain)
        val request = Request.Builder().url(path.plus("?text=").plus(text)).method(Chat.method, body).build()
        val response = GlobalVars.okHttpClient.newCall(request).execute()
        if (response.code != 200 || response.body == null) throw IOException()
        val res = response.body!!.string()
        response.body!!.close()
        return res
    }

    private suspend fun groupSendMessage(text: String, event: GroupMessageEvent) {
        val split = text.split(Chat.multiSplit)
        for (i in split) {
            botCount(event)
            event.group.sendMessage(i)
        }
    }
}