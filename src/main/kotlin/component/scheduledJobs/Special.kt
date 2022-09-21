/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.component.scheduledJobs

import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.buildMessageChain
import org.operacon.component.Scheduler.friendMessage
import org.operacon.component.Scheduler.quartzScheduler
import org.operacon.component.Settings
import org.operacon.service.friendJob.SpecialReg.dk
import org.quartz.*
import java.io.File
import kotlin.random.Random

class Dk : Job {
    override fun execute(context: JobExecutionContext?) {
        // 在此处添加应当执行的任务体。使用 org.operacon.component.Scheduler.friendMessage 和 groupMessage 发送消息
        if (!Settings.enableSpecialService) return
        val ls = File(Settings.pathSpecialService).readText().split(";;;")
        for (i in ls) {
            if (i == "")
                continue
            val ii = i.split("\t")
            try {
                val m = dk(ii[1], ii[2], ii[3], ii[4])
                if (m == "done")
                    friendMessage(ii[0].toLong(), buildMessageChain { +"今天帮你打了卡哦";+Face(Random.nextInt(0, 324)) })
                else
                    friendMessage(ii[0].toLong(), buildMessageChain { +"没打上";+Face(9);+"错误原因：\n$m" })
            } catch (e: Exception) {
                e.printStackTrace()
                friendMessage(ii[0].toLong(), buildMessageChain { +"没打上";+Face(9);+"自己试试吧" })
            }
        }
        quartzScheduler.deleteJob(DkHandler.jobDetail.key)
        DkHandler.trigger = TriggerBuilder.newTrigger()
            .withSchedule(
                CronScheduleBuilder.cronSchedule(
                    Random.nextInt(0, 55).toString() + " "
                            + Random.nextInt(0, 55).toString() + " 16 * * ?"
                )
            ).build()
        quartzScheduler.scheduleJob(DkHandler.jobDetail, DkHandler.trigger)
    }
}

object DkHandler {
    // 描述定时任务
    val jobDetail: JobDetail = JobBuilder.newJob(Dk::class.java)
        .withDescription("每天下午四到五点随机时间打卡").build()

    // 使用 Cron 表达式确定该任务应该如何执行
    var trigger: CronTrigger = TriggerBuilder.newTrigger()
        .withSchedule(
            CronScheduleBuilder.cronSchedule(
                Random.nextInt(0, 55).toString() + " "
                        + Random.nextInt(0, 55).toString() + " 16 * * ?"
            )
        ).build()

    fun register() {
        quartzScheduler.scheduleJob(jobDetail, trigger)
    }
}