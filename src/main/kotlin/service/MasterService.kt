package org.operacon.service

import net.mamoe.mirai.event.events.FriendMessageEvent
import org.operacon.XiaoXiang.reload
import org.operacon.bean.Bai
import org.operacon.bean.Chat
import org.operacon.bean.Settings

object MasterService {
    private val groupMap = HashMap<String, Long>()
    private val modeMap = HashMap<Long, Boolean>()

    suspend fun scan(event: FriendMessageEvent, split: List<String>): Boolean {
        if (event.sender.id !in Settings.masterId)
            return false
        modeMap[event.sender.id] = modeMap[event.sender.id] ?: false
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

        if (split[0] == "reload") {
            try {
                reloadConfig()
                event.sender.sendMessage("[sudo] 重新加载配置文件 - 完毕")
            } catch (e: Exception) {
                event.sender.sendMessage("[sudo] 重新加载配置文件 - 出错")
            }
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
}