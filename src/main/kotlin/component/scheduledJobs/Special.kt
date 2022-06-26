/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.component.scheduledJobs

import org.operacon.component.Scheduler.friendMessage
import org.operacon.component.Scheduler.quartzScheduler
import org.operacon.component.Settings
import org.operacon.service.friendJob.SpecialReg.dk
import org.quartz.*
import java.io.File

class Dk : Job {
    override fun execute(context: JobExecutionContext?) {
        // 在此处添加应当执行的任务体。使用 org.operacon.bean.Scheduler.friendMessage 和 groupMessage 发送消息
        if (!Settings.enableSpecialService) return
        val ls = File(Settings.pathSpecialService).readText().split(";;;")
        for (i in ls) {
            if (i == "")
                continue
            val ii = i.split("\t")
            try {
                if (dk(ii[1], ii[2]))
                    friendMessage(ii[0].toLong(), "今天帮你打了卡哦")
            } catch (e: Exception) {
                e.printStackTrace()
                friendMessage(ii[0].toLong(), "没打上，自己试试？")
            }
        }
    }
}

object DkHandler {
    // 描述定时任务
    private val jobDetail: JobDetail = JobBuilder.newJob(Dk::class.java)
        .withDescription("每天下午五点打卡").build()

    // 使用 Cron 表达式确定该任务应该如何执行
    private val trigger: CronTrigger = TriggerBuilder.newTrigger()
        .withSchedule(CronScheduleBuilder.cronSchedule("30 0 17 * * ?")).build()

    fun register() {
        quartzScheduler.scheduleJob(jobDetail, trigger)
    }
}