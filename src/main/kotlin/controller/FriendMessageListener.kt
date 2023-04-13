/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.controller

import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.message.data.content
import org.operacon.component.GlobalVars
import org.operacon.service.MasterService

class FriendMessageListener(private val event: FriendMessageEvent) {
    suspend fun monitor() {
        val content = event.message.content.trim()
        val split = content.split(GlobalVars.splitter)
        if (MasterService.scan(event, split)) return
    }
}