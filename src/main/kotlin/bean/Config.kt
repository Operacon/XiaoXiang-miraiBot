package org.operacon.bean

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object Config : ReadOnlyPluginConfig("Settings") {
    @ValueDescription("设置此 bot 的 qq 号")
    val selfId by value(439980487)
    @ValueDescription("设置此 bot 主人的 qq 号")
    val masterId by value(mutableListOf<Long>(114514))
    @ValueDescription("设置可以控制 bot 发消息的群称呼和群号。每条格式为 '群称呼:群号'")
    val groups by value(mutableListOf("工作群:114514"))
}