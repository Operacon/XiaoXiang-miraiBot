/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.service

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.isBotMuted
import net.mamoe.mirai.event.events.FriendMessageEvent
import org.operacon.XiaoXiang.reload
import org.operacon.bean.Bai
import org.operacon.bean.Chat
import org.operacon.bean.Settings

object MasterService {
    private val groupMap = HashMap<String, Long>()
    private val modeMap = HashMap<Long, Boolean>()
    private val broadcastGpMap = HashMap<Long, Boolean>()
    private val broadcastFrMap = HashMap<Long, Boolean>()
    private val transMap = HashMap<Long, Long>()

    suspend fun scan(event: FriendMessageEvent, split: List<String>): Boolean {
        if (event.sender.id !in Settings.masterId)
            return false
        transMap[event.sender.id] = transMap[event.sender.id] ?: 0
        modeMap[event.sender.id] = modeMap[event.sender.id] ?: false
        broadcastGpMap[event.sender.id] = broadcastGpMap[event.sender.id] ?: false
        broadcastFrMap[event.sender.id] = broadcastFrMap[event.sender.id] ?: false
        if (checkTrans(event)) return true
        if (checkGpBc(event)) return true
        if (checkFrBc(event)) return true
        if (checkTrans(event)) return true
        if (split[0] == Settings.tokenEnable) {
            modeMap[event.sender.id] = true
            event.sender.sendMessage("[sudo mode on]")
            return true
        }
        if (split[0] == Settings.tokenDisable) {
            modeMap[event.sender.id] = false
            event.sender.sendMessage("[sudo mode off]")
            return true
        }
        if (!modeMap[event.sender.id]!!)
            return false

        if (split[0] == "reload" || split[0] == "重载") {
            try {
                reloadConfig()
                event.sender.sendMessage("[sudo] 重新加载配置文件 - 完毕")
            } catch (e: Exception) {
                event.sender.sendMessage("[sudo] 重新加载配置文件 - 出错")
            }
            return true
        }

        if (split[0] in groupMap.keys) {
            transMap[event.sender.id] = groupMap[split[0]]!!
            event.sender.sendMessage("[sudo] 下一条请发送要向群聊 ${split[0]} 转发的消息")
            return true
        }

        if (split[0] == "group broadcast" || split[0] == "群聊广播") {
            broadcastGpMap[event.sender.id] = true
            event.sender.sendMessage("[sudo] 下一条请发送要向所有群聊广播的消息")
            return true
        }

        if (split[0] == "friend broadcast" || split[0] == "私聊广播") {
            broadcastFrMap[event.sender.id] = true
            event.sender.sendMessage("[sudo] 下一条请发送要向所有好友广播的消息")
            return true
        }

        return false
    }

    fun reloadConfig() {
        Settings.reload()
        Bai.reload()
        Chat.reload()
        for (i in Settings.groups) {
            val j = i.split(":")
            groupMap[j[0]] = j[1].toLong()
        }
    }

    private suspend fun checkTrans(event: FriendMessageEvent): Boolean {
        if (transMap[event.sender.id] == 0.toLong()) return false
        try {
            Bot.getInstance(Settings.selfId)
                .getGroup(transMap[event.sender.id]!!)!!.sendMessage(event.message)
            event.sender.sendMessage("[sudo] 转发完成")
            transMap[event.sender.id] = 0
        } catch (e: Exception) {
            event.sender.sendMessage("[sudo] 转发失败，检查配置文件中的 bot QQ号以及群号，或者 bot 被目标群禁言")
            transMap[event.sender.id] = 0
        }
        return true
    }

    private suspend fun checkGpBc(event: FriendMessageEvent): Boolean {
        if (!broadcastGpMap[event.sender.id]!!) return false
        for (i in Bot.getInstance(Settings.selfId).groups) {
            try {
                i.sendMessage(event.message)
            } catch (e: Exception) {
                event.sender.sendMessage("[sudo] 向群聊 ${i.name}:${i.id} 广播失败。在该群的禁言状态：${i.isBotMuted}")
            }
        }
        broadcastGpMap[event.sender.id] = false
        event.sender.sendMessage("[sudo] 群聊广播完成")
        broadcastGpMap[event.sender.id] = false
        return true
    }

    private suspend fun checkFrBc(event: FriendMessageEvent): Boolean {
        if (!broadcastGpMap[event.sender.id]!!) return false
        for (i in Bot.getInstance(Settings.selfId).friends) {
            try {
                if (i.id != Settings.selfId)
                    i.sendMessage(event.message)
            } catch (e: Exception) {
                event.sender.sendMessage("[sudo] 向好友 ${i.nick}:${i.id} 广播失败")
            }
        }
        event.sender.sendMessage("[sudo] 群聊广播完成")
        broadcastGpMap[event.sender.id] = false
        return true
    }
}