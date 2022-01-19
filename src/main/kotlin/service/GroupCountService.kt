package org.operacon.service

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Message

object GroupCountService {
    private val messageCounter = HashMap<Long, Int>()
    private val baiCounter = HashMap<Long, Int>()
    private val repeatCache = HashMap<Long, Message>()
    private val repeatState = HashMap<Long, Boolean>()

    fun count(gid: Long, mid: Long) {
        messageCounter[gid] = (messageCounter[gid] ?: 0) + 1
        baiCounter[mid] = (baiCounter[mid] ?: 23) + 1
    }

    suspend fun repeat(event: GroupMessageEvent) {
        if (!repeatCache.containsKey(event.group.id)) {
            repeatCache[event.group.id] = event.message
            repeatState[event.group.id] = false
            return
        }
        if (event.message.contentEquals(repeatCache[event.group.id]!!, ignoreCase = false, strict = true)) {
            if (repeatState[event.group.id]!!)
                return
            event.group.sendMessage(event.message)
            repeatState[event.group.id] = true
            return
        }
        repeatCache[event.group.id] = event.message
        repeatState[event.group.id] = false
    }
}