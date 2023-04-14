/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.component

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import org.operacon.component.Chat.provideDelegate

object Settings : ReadOnlyPluginConfig("Settings") {
    @ValueDescription("设置此 bot 的 qq 号")
    val selfId by value<Long>(439980487)

    @ValueDescription("设置此 bot 主人的 qq 号")
    val masterId by value(mutableListOf<Long>(114514))

    @ValueDescription("设置 bot 主人开启管理员模式的口令")
    val tokenEnable by value("sudo")

    @ValueDescription("设置 bot 主人关闭管理员模式的口令")
    val tokenDisable by value("exit")

    @ValueDescription("设置可以控制 bot 发消息的群称呼和群号。每条格式为 '群称呼:群号'")
    val groups by value(mutableListOf("工作群:114514"))

    @ValueDescription("设置是否打开特殊服务")
    val enableSpecialService by value(false)

    @ValueDescription("设置特殊服务信息保存文件的绝对路径")
    val pathSpecialService by value("C:/Users/Public/specialData.txt")

    @ValueDescription("在下列群中应用每人每天的调用限额")
    val limitedGroups by value(mutableListOf<Long>(114514))

    @ValueDescription("每人每天最多调用 bot 的次数")
    val callLimit by value(10)

    @ValueDescription("对戳一戳，bot 跟戳的概率")
    val nudgeProb by value(0.1)
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

object Chat : ReadOnlyPluginConfig("ChatBot") {
    @ValueDescription("设置 ChatBot 是否在所有群聊中开启。开启后，通过下面设置的关键词触发。除积极群外所有的群聊共享开启地址池中第一个 ChatBot")
    val enableForAllGroups by value(false)

    @ValueDescription("设置 ChatBot 在群聊中的关键词，以此开头的句子将被发给 ChatBot")
    val groupKeyword by value("。")

    @ValueDescription("设置 ChatBot 是否在所有私聊中开启。开启后，如果用户没有触发其他服务则直接触发。所有好友共享开启地址池中第一个 ChatBot")
    val enableForAllFriends by value(false)

    @ValueDescription("设置请求 ChatBot 地址的方法")
    val method by value("POST")

    @ValueDescription("设置在哪些群聊中开启服务。每个群聊调用 ChatBot 的消息会被发给各自的地址，列表顺序与地址池一一对应。")
    val enabledGroups by value(mutableListOf<Long>(114514))

    @ValueDescription("设置开启服务群请求 ChatBot 的地址池。Params: text 传参，例如 http://localhost:8520/chat?text=发送的句子")
    val enabledUrl by value(mutableListOf("http://localhost:8520/chat"))

    @ValueDescription("设置是否开启积极群。即使积极群不在开启服务群列表中，也会响应")
    val enableActiveGroups by value(false)

    @ValueDescription(
        "设置积极群。积极群中的群聊的所有消息都会被发给各自的 ChatBot （按照上面 ChatBot " +
                "地址池的顺序一一对应，积极群数量小于等于地址池大小），但机器人只会按下面设置的概率回复。这可以获得更加符合语境和生草的体验，但非常浪费计算资源"
    )
    val activeGroups by value(mutableListOf<Long>(114514))

    @ValueDescription("设置积极群请求 ChatBot 的地址池。Params: text 传参，例如 http://localhost:8520/chat?text=发送的句子")
    val activeUrl by value(mutableListOf("http://localhost:8520/chat"))

    @ValueDescription("设置积极群回复的概率，请保证为 0 - 1 之间的合法浮点数")
    val replyProb by value(0.08)

    @ValueDescription("设置 ChatBot 单次发送多条消息的分隔符")
    val multiSplit by value("[SEP]")
}

object WordCloud : ReadOnlyPluginConfig("WordCloud") {
    @ValueDescription("设置是否打开每日词云，和每日统计一起发送，生成可能非常消耗算力")
    val enableWordCloud by value(false)

    @ValueDescription("设置请求词云的地址，参考 README")
    val url by value("http://localhost:6785/wc")
}

object ChatGLM : ReadOnlyPluginConfig("ChatGLM") {
    @ValueDescription("设置在哪些群聊中开启服务。")
    val enabledGroups by value(mutableListOf<Long>(114514))

    @ValueDescription("设置为哪些私聊开启服务。")
    val enabledFriends by value(mutableListOf<Long>(114514))

    @ValueDescription("设置 ChatGLM API 地址。使用原仓库 api.py 的请求定义。")
    val url by value("http://127.0.0.1:8000")

    @ValueDescription("设置 history 最大大小。")
    val maxHistoryLength by value(20)

    @ValueDescription("设置 max_length。")
    val maxLength by value<Long>(512)

    @ValueDescription("设置 top_p。")
    val topP by value<Double>(0.7)

    @ValueDescription("设置 temperature。")
    val temperature by value<Double>(0.95)

    @ValueDescription("设置冷却时间。单位为秒。")
    val coolDownDelay by value<Long>(120)
}