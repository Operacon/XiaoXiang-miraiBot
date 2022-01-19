package org.operacon.controller

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import org.operacon.service.GroupCountService

class GroupMessageListener(private val event: GroupMessageEvent) {
    suspend fun monitor() {
        GroupCountService.count(event.group.id, event.sender.id)
        if (event.message.content == "小湘") {
            event.group.sendMessage("潇小湘在线上~")
            return
        }

        GroupCountService.repeat(event)
    }
}