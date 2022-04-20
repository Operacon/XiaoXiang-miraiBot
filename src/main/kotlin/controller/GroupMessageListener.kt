/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.controller

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import org.operacon.bean.ChatBot
import org.operacon.bean.GlobalVars
import org.operacon.service.GroupCountService
import org.operacon.service.groupJob.*

class GroupMessageListener(private val event: GroupMessageEvent) {
    suspend fun monitor() {
        val content = event.message.content.trim()
        val split = content.split(GlobalVars.splitter)
        GroupCountService.count(event, content)
        if (GroupCountService.hello(event, split)) return
        if (DrawLots.scan(event, split)) return
        if (Bai.scan(event, split)) return

        if (ChatBot.groupScan(event)) return
        if (GroupCountService.repeat(event)) return
        GroupCountService.noneBotCount(event)
    }
}