package org.operacon.controller

import net.mamoe.mirai.event.events.GroupMessageEvent
import org.operacon.service.GroupCountService

class GroupMessageListener(event: GroupMessageEvent) {
    private val event = event

    suspend fun monitor() {
        GroupCountService.count(event.group.id, event.sender.id)
    }
}