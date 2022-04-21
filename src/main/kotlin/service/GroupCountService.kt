/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.operacon.bean.ChatBot
import org.operacon.bean.GlobalVars
import org.operacon.bean.Settings
import org.operacon.bean.WordCloud
import java.io.IOException

object GroupCountService {
    val baiCounter = HashMap<Long, Int>()
    val messageCounter = HashMap<Long, Int>()
    val imageCounter = HashMap<Long, Int>()
    val noneBotCounter = HashMap<Long, Int>()
    val repeatCache = HashMap<Long, Message>()
    private val repeatState = HashMap<Long, Boolean>()
    private val chatLog = HashMap<Long, StringBuilder>()

    suspend fun hello(event: GroupMessageEvent, split: List<String>): Boolean {
        if (split[0] == "小湘") {
            event.group.sendMessage("潇小湘在线上~")
            return true
        }
        if (split[0] == "功能" || split[0] == "介绍") {
            event.group.sendMessage(
                "潇小湘是开源项目，功能请参照 https://github.com/Operacon/XiaoXiang-miraiBot#已经开发的功能 。bug " +
                        "反馈或功能需求请开 issue"
            )
            return true
        }
        return false
    }

    fun count(event: GroupMessageEvent, text: String) {
        messageCounter[event.group.id] = (messageCounter[event.group.id] ?: 0) + 1
        baiCounter[event.sender.id] = (baiCounter[event.sender.id] ?: 23) + 1
        if (event.message[Image] != null)
            imageCounter[event.group.id] = (imageCounter[event.group.id] ?: 0) + 1
        if (WordCloud.enableWordCloud) {
            if (chatLog[event.group.id] == null)
                chatLog[event.group.id] = StringBuilder()
            chatLog[event.group.id]!!.append(' ')
                .append(text.replace(GlobalVars.pagReplace, "").replace(GlobalVars.atReplace, ""))
        }
    }

    fun botCount(event: GroupMessageEvent) {
        messageCounter[event.group.id] = (messageCounter[event.group.id] ?: 0) + 1
    }

    fun noneBotCount(event: GroupMessageEvent) {
        noneBotCounter[event.group.id] = (noneBotCounter[event.group.id] ?: 0) + 1
    }

    suspend fun repeat(event: GroupMessageEvent): Boolean {
        if (!repeatCache.containsKey(event.group.id)) {
            repeatCache[event.group.id] = event.message
            repeatState[event.group.id] = false
            return false
        }
        if (event.message.contentEquals(repeatCache[event.group.id]!!, ignoreCase = false, strict = true)) {
            if (repeatState[event.group.id]!!)
                return true
            event.group.sendMessage(event.message)
            botCount(event)
            repeatState[event.group.id] = true
            return true
        }
        repeatCache[event.group.id] = event.message
        repeatState[event.group.id] = false
        return false
    }

    fun sendWordCloud(id: Long) = runBlocking {
        launch(Dispatchers.Unconfined) {
            try {
                if (WordCloud.enableWordCloud) {
                    val body = chatLog[id]!!.toString().toRequestBody(ChatBot.mediaType)
                    val request = Request.Builder().url(WordCloud.url).method("POST", body).build()
                    val response = ChatBot.client.newCall(request).execute()
                    if (response.code != 200 || response.body == null) throw IOException()
                    Bot.getInstance(Settings.selfId).getGroup(id)!!.sendImage(response.body!!.byteStream())
                    chatLog[id]!!.clear()
                }
            } catch (e: Exception) {
            }
        }
    }
}