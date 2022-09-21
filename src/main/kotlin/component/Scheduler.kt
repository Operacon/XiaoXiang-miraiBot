/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.component

import org.operacon.component.scheduledJobs.StatisticsHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.message.data.Message
import org.operacon.component.scheduledJobs.DkHandler
import org.quartz.impl.StdSchedulerFactory
import org.quartz.Scheduler

object Scheduler {
    val quartzScheduler: Scheduler by lazy {
        StdSchedulerFactory.getDefaultScheduler().also { it.start() }
    }

    fun registerJobs() {
        StatisticsHandler.register()
        DkHandler.register()
    }

    fun groupMessage(id: Long, message: Message) = runBlocking {
        // 其他需要 suspend 的函数调用可以用类似的协程避免
        launch(Dispatchers.Unconfined) {
            try {
                Bot.getInstance(Settings.selfId).getGroup(id)!!.sendMessage(message)
            } catch (_: Exception) {
            }
        }
    }

    fun friendMessage(id: Long, message: Message) = runBlocking {
        launch(Dispatchers.Unconfined) {
            try {
                Bot.getInstance(Settings.selfId).getFriend(id)!!.sendMessage(message)
            } catch (_: Exception) {
            }
        }
    }
}