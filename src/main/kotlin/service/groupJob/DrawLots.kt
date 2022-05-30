/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.service.groupJob

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import org.operacon.component.Settings
import java.time.LocalDateTime
import java.util.function.Predicate
import kotlin.random.Random

object DrawLots {
    val sentMap: HashMap<Long, MutableList<Lot>> = HashMap()
    val limitMap: HashMap<Long, Int> = HashMap()

    suspend fun scan(event: GroupMessageEvent, split: List<String>): Boolean {
        var limited: Boolean = false
        if (event.group.id in Settings.limitedGroups) {
            // 此处用于调用限额
            limitMap[event.sender.id] = (limitMap[event.sender.id] ?: 0) + 1
            if (limitMap[event.sender.id]!! >= 11)
                limited = true
        }

        if (split[0] == "求签") {
            if (limit(event, limited)) return true
            if (split.size == 1) {
                event.group.sendMessage(event.message.quote() + "所以想求什么呢")
                return true
            }
            var txt = ""
            for (i in (1 until split.size))
                txt += split[i]
            Lot.draw(txt, event)
            return true
        }
        if (split[0] == "概率") {
            if (limit(event, limited)) return true
            if (split.size == 1) {
                event.group.sendMessage(event.message.quote() + "所以想算什么呢")
                return true
            }
            var txt = ""
            for (i in (1 until split.size))
                txt += split[i]
            Lot.prob(txt, event)
            return true
        }
        if (split[0] == "决定") {
            if (limit(event, limited)) return true
            val s = split.drop(1).distinct()
            if (split.size == 1) {
                event.group.sendMessage(event.message.quote() + "所以想决定什么呢")
                return true
            }
            if (s.size == 1) {
                event.group.sendMessage(event.message.quote() + "只发一项那就是已经决定好了？")
                return true
            }
            var txt = "我觉得 "
            txt += s.random().replaceFirst("我", "你")
            event.group.sendMessage(event.message.quote() + txt.trimMargin())
            return true
        }
        if (split[0] == "评价一下") {
            if (limit(event, limited)) return true
            when (if (split.size == 1)
                Random.nextInt(0, 8)
            else
                split[1].hashCode() % 8) {
                0 -> event.group.sendMessage("鉴定为烂")
                1 -> event.group.sendMessage("我觉得好")
                2 -> event.group.sendMessage("小湘觉得不怎么样")
                3 -> event.group.sendMessage("？我拒绝")
                4 -> event.group.sendMessage("鉴定为好")
                5 -> event.group.sendMessage("就不评")
                6 -> event.group.sendMessage("小湘觉得坏")
                7 -> event.group.sendMessage("我觉得不好")
            }
            return true
        }

        if (event.group.id in Settings.limitedGroups)
        // 此处用于恢复调用限额
            limitMap[event.sender.id] = limitMap[event.sender.id]!! - 1

        return false
    }

    suspend fun limit(event: GroupMessageEvent, limited: Boolean): Boolean {
        if (limited)
            event.group.sendMessage(
                At(event.sender) + (when (Random.nextInt(0, 3)) {
                    0 -> "汝今日天机已尽，明日再来"
                    1 -> "今日毕矣"
                    2 -> "一天玩这么多次对群不好~"
                    else -> ""
                }).toString()
            )
        return limited
    }
}

class Lot(private val sent: String, private val result: Int) {
    private val num1: Int = ((result + 3.0) * 14.285714).toInt() + Random.nextInt(0, 15)
    private val num2: Int = Random.nextInt(0, 100)
    private val time: LocalDateTime = LocalDateTime.now()
    fun sentRet(v: Int): String {
        return when (v * result) {
            -3 -> "大凶"
            -2 -> "凶"
            -1 -> "小凶"
            0 -> "平"
            1 -> "小吉"
            2 -> "吉"
            3 -> "大吉"
            else -> ""
        }
    }

    fun sentProbRet(v: Int): String {
        var ret = ""
        val bool: Int = Random.nextInt(0, 2)
        if (bool == 0) {
            ret += when (v) {
                1 -> num1.toString()
                else -> (99 - num1).toString()
            }
            ret += "."
            ret += when (v) {
                1 -> num2.toString()
                else -> (100 - num1).toString()
            }
            ret += "% 的概率发生"
        } else {
            ret += when (v) {
                -1 -> num1.toString()
                else -> (99 - num1).toString()
            }
            ret += "."
            ret += when (v) {
                -1 -> num2.toString()
                else -> (100 - num1).toString()
            }
            ret += "% 的概率不发生"
        }
        return ret
    }

    fun checkSim(v: String): Int {
        if (v == sent)
            return 0
        var ret = 0
        var rst = false
        val posSelf1 = sent.replace("不", "")
        var posPara1 = v.replace("不", "")
        val bool: Boolean = (posPara1 == posSelf1)
        if (!bool && posSelf1 == posPara1.replace("大", "小")) {
            rst = true; ret += when (ret < 0) {
                true -> -1; else -> 1
            }; posPara1 = posPara1.replace("大", "小")
        } else if (!bool && posSelf1 == posPara1.replace("小", "大")) {
            rst = true; ret += when (ret < 0) {
                true -> -1; else -> 1
            }; posPara1 = posPara1.replace("小", "大")
        }
        if (bool || posPara1 == posSelf1) {
            rst = true
            for (i in v) {
                if (i == '不')
                    ret++
            }
            for (i in sent) {
                if (i == '不')
                    ret--
            }
        }
        if (!rst) {
            return -1
        }
        return when (ret < 0) {
            true -> -ret; else -> ret
        }
    }

    fun deltaSec(minutes: Long): Boolean {
        if (time.plusMinutes(minutes) < LocalDateTime.now())
            return true
        return false
    }

    companion object {
        suspend fun draw(txt: String, event: GroupMessageEvent) {
            val bufM = DrawLots.sentMap
            if (!bufM.containsKey(event.sender.id)) {
                val list = mutableListOf(Lot(txt, (0..6).random() - 3))
                bufM[event.sender.id] = list
                event.group.sendMessage(
                    event.message.quote() + """
                    所求事项：${txt.replaceFirst("我", "你")}
                    求签结果：${list.first().sentRet(1)}
                """.trimIndent()
                )
                return
            }
            val list = bufM[event.sender.id]
            if (list != null) {
                var flag = false
                var ret: Int
                list.removeIf(object : Predicate<Lot?> {
                    override fun test(i: Lot?): Boolean {
                        if (i != null)
                            return i.deltaSec(30)
                        return false
                    }
                })
                for (i in list) {
                    if (!flag) {
                        ret = i.checkSim(txt)
                        if (ret >= 0) {
                            event.group.sendMessage(
                                event.message.quote() + """
                            所求事项：${txt.replaceFirst("我", "你")}
                            求签结果：${
                                    i.sentRet(
                                        when (ret % 2) {
                                            1 -> -1
                                            else -> 1
                                        }
                                    )
                                }
                        """.trimIndent()
                            )
                            flag = true
                            continue
                        }
                    }
                }
                if (!flag) {
                    list.add(Lot(txt, (0..6).random() - 3))
                    event.group.sendMessage(
                        event.message.quote() + """
                    所求事项：${txt.replaceFirst("我", "你")}
                    求签结果：${list.last().sentRet(1)}
                """.trimIndent()
                    )
                    return
                }
            }
        }

        suspend fun prob(txt: String, event: GroupMessageEvent) {
            val bufM = DrawLots.sentMap
            if (!bufM.containsKey(event.sender.id)) {
                val list = mutableListOf(Lot(txt, (0..6).random() - 3))
                bufM[event.sender.id] = list
                event.group.sendMessage(
                    event.message.quote() + """
                    此事有 ${list.first().sentProbRet(1)}
                """.trimIndent()
                )
                return
            }
            val list = bufM[event.sender.id]
            if (list != null) {
                var flag = false
                var ret: Int
                list.removeIf(object : Predicate<Lot?> {
                    override fun test(i: Lot?): Boolean {
                        if (i != null)
                            return i.deltaSec(30)
                        return false
                    }
                })
                for (i in list) {
                    if (!flag) {
                        ret = i.checkSim(txt)
                        if (ret >= 0) {
                            event.group.sendMessage(
                                event.message.quote() + """
                            此事有 ${
                                    i.sentProbRet(
                                        when (ret % 2) {
                                            1 -> -1
                                            else -> 1
                                        }
                                    )
                                }
                        """.trimIndent()
                            )
                            flag = true
                            continue
                        }
                    }
                }
                if (!flag) {
                    list.add(Lot(txt, (0..6).random() - 3))
                    event.group.sendMessage(
                        event.message.quote() + """
                    此事有 ${list.last().sentProbRet(1)}
                """.trimIndent()
                    )
                    return
                }
            }
        }
    }
}