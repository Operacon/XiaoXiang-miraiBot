package org.operacon

import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.utils.error
import net.mamoe.mirai.utils.info
import org.operacon.bean.Config
import org.operacon.bean.GlobalVars
import org.operacon.controller.GroupMessageListener
import org.operacon.service.MasterService
import java.lang.Exception

object XiaoXiang : KotlinPlugin(
    JvmPluginDescription(
        id = "org.operacon.xiaoXiang",
        name = "XiaoXiang",
        version = "0.1-DEBUG",
    )
) {
    override fun onEnable() {
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> { event -> GroupMessageListener(event).monitor() }

        try {
            Config.reload()
            MasterService.initGroupMap()
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error { "潇小湘 - 配置文件解析出错" }
        }
        logger.info { "潇小湘 - 主插件加载完毕" }
    }
}