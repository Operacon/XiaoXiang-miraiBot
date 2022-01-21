package org.operacon.bean

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object Settings : ReadOnlyPluginConfig("Settings") {
    @ValueDescription("设置此 bot 的 qq 号")
    val selfId by value(439980487)

    @ValueDescription("设置此 bot 主人的 qq 号")
    val masterId by value(mutableListOf<Long>(114514))

    @ValueDescription("设置可以控制 bot 发消息的群称呼和群号。每条格式为 '群称呼:群号'")
    val groups by value(mutableListOf("工作群:114514"))

    @ValueDescription("设置是否打开特殊服务")
    val enableSpecialService by value(false)

    @ValueDescription("设置特殊服务信息保存文件的绝对路径")
    val pathSpecialService by value("C:\\Users\\Public\\specialData.txt")
}

object Bai : ReadOnlyPluginConfig("BaiLan") {
    @ValueDescription("设置摆烂功能 bot 说的骚话，利用 \$name 代表群名片，利用 \$sleep 代表生成的小时数")
    val sentences by value(
        mutableListOf(
            "\$name 写了 \$sleep 小时毫无意义的代码还觉得自己进步了",
            "\$name 发了 \$sleep 个小时的呆好可爱",
            "\$name 被游戏玩了 \$sleep 小时",
            "\$name 去爽吃了 \$sleep 小时被店家赶了出来",
            "手机拿着 \$name 刷了 \$sleep 小时视频",
            "\$name 去爽吃了 \$sleep 小时还没吃够",
            "\$name 一个人单排 \$sleep 小时有点头晕",
            "\$name 和人开黑 \$sleep 小时被骂铸币",
            "\$name 和人开黑 \$sleep 小时大鲨特鲨",
            "\$name 刷了 \$sleep 个小时知乎开始忧国忧民",
            "\$name 刷了 \$sleep 个小时微博血压爆炸",
            "\$name 刷了 \$sleep 个小时不知道什么东西，然后走进了厕所",
            "\$name 打了 \$sleep 个小时怪还没升级",
            "\$name 本来要去xc但被鸽了，一个人玩了 \$sleep 小时游戏",
            "\$name 和npy出去xc了 \$sleep 小时开开心心"
        )
    )
}