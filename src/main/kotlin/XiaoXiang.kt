/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.utils.error
import net.mamoe.mirai.utils.info
import org.operacon.controller.FriendMessageListener
import org.operacon.controller.GroupMessageListener
import org.operacon.service.MasterService
import java.lang.Exception

object XiaoXiang : KotlinPlugin(
    JvmPluginDescription(
        id = "org.operacon.xiaoXiang",
        name = "XiaoXiang",
        version = "0.2-DEBUG",
    )
) {
    override fun onEnable() {
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> { event -> GroupMessageListener(event).monitor() }
        GlobalEventChannel.subscribeAlways<FriendMessageEvent> { event -> FriendMessageListener(event).monitor() }

        try {
            MasterService.reloadConfig()
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error { "潇小湘 - 配置文件解析出错" }
        }
        logger.info { "潇小湘 - 主插件加载完毕" }
    }
}