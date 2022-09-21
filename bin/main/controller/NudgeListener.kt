/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.controller

import net.mamoe.mirai.event.events.NudgeEvent
import org.operacon.component.Settings
import kotlin.random.Random

class NudgeListener(private val event: NudgeEvent) {
    suspend fun monitor() {
        try {
            if (event.target.id == Settings.selfId) {
                when (Random.nextInt(0, 4)) {
                    0 -> event.from.nudge().sendTo(event.subject)
                    1 -> event.from.nudge().sendTo(event.subject)
                    2 -> event.subject.sendMessage("拍人不拍脸")
                    3 -> event.subject.sendMessage("不准拍 (｡･∀･)ﾉﾞ")
                }
            } else {
                if (Random.nextFloat() <= Settings.nudgeProb)
                    event.target.nudge().sendTo(event.subject)
            }
        } catch (_: Exception) {
        }
    }
}