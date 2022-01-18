package org.operacon

import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.utils.info
import org.operacon.bean.Config
import org.operacon.bean.GlobalVars
import org.operacon.controller.GroupMessageListener

object XiaoXiang : KotlinPlugin(
    JvmPluginDescription(
        id = "org.operacon.xiaoXiang",
        name = "XiaoXiang",
        version = "1.0-SNAPSHOT",
    )
) {
    override fun onEnable() {
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> { event -> GroupMessageListener(event).monitor() }
        Config.reload()
        GlobalVars.selfId = Bot.instances.last().id
        logger.info { "潇小湘 - 主插件加载完毕" }
    }
}