/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.bean

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.operacon.bean.GlobalVars.atReplace
import org.operacon.bean.GlobalVars.pagReplace
import java.io.IOException
import kotlin.random.Random

object ChatBot {
    private val client = OkHttpClient().newBuilder().build()
    private val mediaType = "text/plain".toMediaTypeOrNull()

    suspend fun groupScan(event: GroupMessageEvent): Boolean {
        if (!Chat.enableForGroups and !Chat.enableActiveGroups)
            return false
        var text = event.message.content.trim().replace(pagReplace, "").replace(atReplace, "")
        if (text == "")
            return false
        if (text.startsWith(Chat.groupKeyword)) {
            text = text.removeRange(0, Chat.groupKeyword.length)
            if (Chat.enableForGroups) {
                if (Chat.enableActiveGroups and (event.group.id in Chat.activeGroups))
                    groupSendMessage(chat(text, Chat.url[Chat.activeGroups.indexOf(event.group.id)]), event)
                else
                    groupSendMessage(chat(text, Chat.url[0]), event)
                return true
            }
            if (Chat.enableActiveGroups) {
                groupSendMessage(chat(text, Chat.url[Chat.activeGroups.indexOf(event.group.id)]), event)
                return true
            }
        } else if (Chat.enableActiveGroups && (event.group.id in Chat.activeGroups)) {
            text = chat(text, Chat.url[Chat.activeGroups.indexOf(event.group.id)])
            if (Random.nextFloat() <= Chat.replyProb)
                groupSendMessage(text, event)
        }
        return false
    }

    private fun chat(text: String, path: String): String {
        val body = "".toRequestBody(mediaType)
        val request = Request.Builder().url(path.plus("?text=").plus(text)).method(Chat.method, body).build()
        val response = client.newCall(request).execute()
        if (response.code != 200 || response.body == null) throw IOException()
        return response.body!!.string()
    }

    private suspend fun groupSendMessage(text: String, event: GroupMessageEvent) {
        val split = text.split(Chat.multiSplit)
        for (i in split)
            event.group.sendMessage(i)
    }
}