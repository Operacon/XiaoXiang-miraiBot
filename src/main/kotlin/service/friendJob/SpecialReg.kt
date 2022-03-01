/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.service.friendJob

import net.mamoe.mirai.event.events.FriendMessageEvent
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.operacon.bean.ChatBot
import org.operacon.bean.Settings.enableSpecialService
import org.operacon.bean.Settings.pathSpecialService
import java.io.File

object SpecialReg {
    private val susSet = HashSet<Long>()
    private val pat = Regex(""".+;;;.+""")

    suspend fun scan(event: FriendMessageEvent, split: List<String>): Boolean {
        if (!enableSpecialService) return false
        if (split[0] == "帮小忙干大事") {
            susSet.add(event.sender.id)
            event.sender.sendMessage("收到，但是我需要学号和密码~")
            event.sender.sendMessage("决定好了的话，你下条消息的格式应该是：\\n“\\\\s;;;\\\\s”\\n即用连续三个英文分号连接的两个字符串，第一个学号，第二个密码")
            event.sender.sendMessage("请注意：格式错了可以重发，但是如果学号密码打错了，会造成不小麻烦。。")
            event.sender.sendMessage("另外我目前只能定位到 北京市海淀区")
            return true
        }
        if (split[0].matches(pat) and susSet.contains(event.sender.id)) {
            try {
                val ll = split[0].split(";;;")
                val id = ll[0]
                val pwd = ll[1]
                if (ll.size > 2 || id.length < 8) {
                    event.sender.sendMessage("不要乱发谢谢")
                    return true
                }
                File(pathSpecialService).appendText("${event.sender.id}\t${id}\t${pwd};;;")
                susSet.remove(event.sender.id)
                event.sender.sendMessage("学号 ${id}\n密码 $pwd")
                event.sender.sendMessage("记住了，每天五点多会尽量帮你的~")
                return true
            } catch (e: Exception) {
                event.sender.sendMessage("检查格式，没问题的话去找 bot 主人检查配置文件")
                return true
            }
        }
        if (split[0] == "试试打卡") {
            val ls = File(pathSpecialService).readText().split(";;;")
            for (i in ls) {
                if (i == "")
                    continue
                val ii = i.split("\t")
                if (ii[0] == event.sender.id.toString()) {
                    try {
                        if (dk(ii[1], ii[2]))
                            event.sender.sendMessage("打上了打上了打上了！！好耶")
                        else
                            event.sender.sendMessage("坏 又没打上")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        event.sender.sendMessage("好像代码出了问题，自己打卡吧")
                    }
                    return true
                }
            }
            event.sender.sendMessage("没找到你的信息~")
            return true
        }
        return false
    }

    fun dk(id: String, pwd: String): Boolean {
        val mediaType: MediaType = "application/json".toMediaType()
        val body: RequestBody = "{\"id\":\"${id}\",\"pwd\":\"${pwd}\"}".toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url("http://localhost:20012/dk")
            .method("POST", body)
            .addHeader("Content-Type", "application/json")
            .build()
        val response = ChatBot.client.newCall(request).execute().body?.string()
        if (response == null || response == "0")
            return false
        return true
    }
}