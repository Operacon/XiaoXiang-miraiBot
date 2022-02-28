/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.service

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message

object GroupCountService {
    val baiCounter = HashMap<Long, Int>()
    val messageCounter = HashMap<Long, Int>()
    val imageCounter = HashMap<Long, Int>()
    val noneBotCounter = HashMap<Long, Int>()
    private val repeatCache = HashMap<Long, Message>()
    private val repeatState = HashMap<Long, Boolean>()

    suspend fun hello(event: GroupMessageEvent, split: List<String>): Boolean {
        if (split[0] == "小湘") {
            event.group.sendMessage("潇小湘在线上~")
            return true
        }
        return false
    }

    fun count(event: GroupMessageEvent) {
        messageCounter[event.group.id] = (messageCounter[event.group.id] ?: 0) + 1
        baiCounter[event.sender.id] = (baiCounter[event.sender.id] ?: 23) + 1
        if (event.message[Image] != null)
            imageCounter[event.group.id] = (imageCounter[event.group.id] ?: 0) + 1
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
            repeatState[event.group.id] = true
            return true
        }
        repeatCache[event.group.id] = event.message
        repeatState[event.group.id] = false
        return false
    }
}