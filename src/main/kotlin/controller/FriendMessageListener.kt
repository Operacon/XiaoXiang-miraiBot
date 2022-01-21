package org.operacon.controller

import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.message.data.content
import org.operacon.bean.GlobalVars
import org.operacon.service.MasterService

class FriendMessageListener(private val event: FriendMessageEvent) {
    suspend fun monitor() {
        val content = event.message.content.trim()
        val split = content.split(GlobalVars.splitter)

        if(MasterService.scan(event, split)) return
    }
}