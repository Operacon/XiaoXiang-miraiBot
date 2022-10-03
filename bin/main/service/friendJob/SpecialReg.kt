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
import org.operacon.component.GlobalVars
import org.operacon.component.GlobalVars.pos
import org.operacon.component.Settings.enableSpecialService
import org.operacon.component.Settings.pathSpecialService
import java.io.File

object SpecialReg {
    private val susSet = HashSet<Long>()
    private val pat = Regex(""".+;;;.+""")

    suspend fun scan(event: FriendMessageEvent, split: List<String>): Boolean {
        if (!enableSpecialService) return false
        if (split[0] == "教我打卡") {
            event.sender.sendMessage("注册请回复 天天打卡好累啊")
            event.sender.sendMessage("注销请回复 金盆洗手改过自新")
            return true
        }
        if (split[0] == "天天打卡好累啊") {
            susSet.add(event.sender.id)
            event.sender.sendMessage("收到，但是我需要学号，密码以及你的坐标~")
            event.sender.sendMessage("你的坐标应当是你所在小区的**中心**坐标，可以使用 https://jingweidu.bmcx.com/ 获取，经纬度保留小数点后 6 位")
            event.sender.sendMessage(
                "决定好了的话，你下条消息的格式应该是：\n\\s;;;\\s;;;\\s;;;" +
                        "\\s\n即用连续三个英文分号连接的四个字符串，第一个学号，第二个密码，第三个经度，第四个纬度"
            )
            event.sender.sendMessage("例如你学号是 12345678 ，密码是 aa114514 ，人在某学校，那么你应该回我这个：")
            event.sender.sendMessage("12345678;;;aa114514;;;117.347668;;;29.982260")
            event.sender.sendMessage("我会使用你提供的经纬度附近的某个位置，在 16 点到 17 点之间随机某个时候打卡")
            event.sender.sendMessage("如果你的信息有变化，请注销后重新注册")
            event.sender.sendMessage("只是测试使用，一切后果由你本人承担！")
            return true
        }
        if (split[0].matches(pat) and susSet.contains(event.sender.id)) {
            try {
                val ll = split[0].split(";;;")
                if (ll.size > 4 || ll[0].length < 8) {
                    event.sender.sendMessage("不要乱发谢谢")
                    return true
                }
                if (!pos.matches(ll[2]) || !pos.matches(ll[3])) {
                    event.sender.sendMessage("经纬度格式错误，请保证精确到小数点后 6 位")
                    return true
                }
                File(pathSpecialService).appendText("${event.sender.id}\t${ll[0]}\t${ll[1]}\t${ll[2]}\t${ll[3]};;;")
                susSet.remove(event.sender.id)
                event.sender.sendMessage("学号 ${ll[0]}\n经度 ${ll[2]}\n纬度 ${ll[3]}")
                event.sender.sendMessage("记住了，会尽量帮你的~")
                return true
            } catch (e: Exception) {
                event.sender.sendMessage("检查格式，没问题的话去找 bot 主人检查配置文件")
                return true
            }
        }
        if (split[0] == "金盆洗手改过自新") {
            val ls = File(pathSpecialService).readText().split(";;;")
            val nf = StringBuilder()
            var rm = false
            for (i in ls) {
                if (i == "")
                    continue
                val ii = i.split("\t")
                if (ii[0] == event.sender.id.toString()) {
                    rm = true
                    continue
                }
                nf.append(i)
            }
            File(pathSpecialService).writeText(nf.toString())
            if (rm)
                event.sender.sendMessage("已经将你的信息移除 以后好好学习 重新做人")
            else
                event.sender.sendMessage("没找到你的信息\nu r already clear")
        }
        if (split[0] == "试试打卡") {
            val ls = File(pathSpecialService).readText().split(";;;")
            for (i in ls) {
                if (i == "")
                    continue
                val ii = i.split("\t")
                if (ii[0] == event.sender.id.toString()) {
                    try {
                        val m = dk(ii[1], ii[2], ii[3], ii[4])
                        if (m == "done")
                            event.sender.sendMessage("打上了打上了打上了！！好耶")
                        else
                            event.sender.sendMessage("没打上，错误原因：\n$m")
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

    fun dk(id: String, pwd: String, lng: String, lat: String): String {
        val mediaType: MediaType = "application/json".toMediaType()
        val body: RequestBody = "{\"id\":\"${id}\",\"pwd\":\"${pwd}\",\"lng\":\"${lng}\",\"lat\":\"${lat}\"}"
            .toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url("http://localhost:6785/dk")
            .method("POST", body)
            .addHeader("Content-Type", "application/json")
            .build()
        val response = GlobalVars.okHttpClient.newCall(request).execute()
        val res = response.body?.string()
        response.body?.close()
        return res ?: "让 bot 主人去翻日志"
    }
}