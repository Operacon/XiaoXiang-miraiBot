package org.operacon.controller

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import org.operacon.bean.GlobalVars
import org.operacon.service.GroupCountService
import org.operacon.service.groupJob.*

class GroupMessageListener(private val event: GroupMessageEvent) {
    suspend fun monitor() {
        val content = event.message.content.trim()
        val split = content.split(GlobalVars.splitter)
        GroupCountService.count(event)

        if (GroupCountService.hello(event, split)) return
        if (DrawLots.scan(event, split)) return
        if (Bai.scan(event, split)) return

        if (GroupCountService.repeat(event)) return
        GroupCountService.noneBotCount(event)
    }
}