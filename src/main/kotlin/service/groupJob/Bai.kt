/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.service.groupJob

import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.buildMessageChain
import org.operacon.bean.Bai
import org.operacon.service.GroupCountService
import kotlin.random.Random

object Bai {
    suspend fun scan(event: GroupMessageEvent, split: List<String>): Boolean {
        if (split[0] == "摆烂额度") {
            val ye = GroupCountService.baiCounter[event.sender.id]!! - 1
            GroupCountService.baiCounter[event.sender.id] = ye
            event.group.sendMessage(At(event.sender) + "现在有 $ye 小时的摆烂额度~")
            return true
        }
        if (split[0] == "睡大觉") {
            val sleep: Int = Random.nextInt(-5, 25)
            val ye = GroupCountService.baiCounter[event.sender.id]!!
            val name = event.sender.nameCardOrNick.replaceFirst("我", "你")
            if (sleep <= -2) {
                event.group.sendMessage("不许睡！")
                return true
            }
            if (sleep <= 0) {
                event.group.sendMessage(buildMessageChain { +"$name 觉得自己没脸睡觉偷偷爬了起来干活";+Face(0) })
                return true
            }
            if (ye < sleep) {
                event.group.sendMessage(buildMessageChain { +"$name 想睡 $sleep 小时但是她摆烂额度不够";+Face(9) })
                return true
            }
            GroupCountService.baiCounter[event.sender.id] = ye - sleep - 1
            event.group.sendMessage("$name 现在去睡 $sleep 小时")
            return true
        }
        if (split[0] == "摆烂") {
            val sleep: Int = Random.nextInt(-5, 25)
            val ye = GroupCountService.baiCounter[event.sender.id]!!
            val name = event.sender.nameCardOrNick.replaceFirst("我", "你")
            if (sleep <= -2) {
                event.group.sendMessage("不准摆！")
                return true
            }
            if (sleep <= 0) {
                event.group.sendMessage(buildMessageChain { +"$name 其实是偷偷去卷了哦";+Face(0) })
                return true
            }
            if (ye < sleep) {
                event.group.sendMessage(buildMessageChain { +"$name 想摆 $sleep 小时但是她摆烂额度不够";+Face(9); })
                return true
            }
            event.group.sendMessage(
                Bai.sentences.random()
                    .replace("\$name", name).replace("\$sleep", sleep.toString())
            )
            GroupCountService.baiCounter[event.sender.id] = ye - sleep - 1
            return true
        }
        return false
    }
}