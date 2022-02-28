/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.bean.scheduledJobs

import net.mamoe.mirai.Bot
import org.operacon.bean.Scheduler.quartzScheduler
import org.quartz.*

class Statistics : Job {
    override fun execute(context: JobExecutionContext?) {
        // 在此处添加应当执行的任务体。使用 org.operacon.bean.Scheduler.friendMessage 和 groupMessage 发送消息

    }
}

object StatisticsHandler {
    // 描述定时任务
    private val jobDetail: JobDetail = JobBuilder.newJob(Statistics::class.java)
        .withDescription("每天零点发送统计数据").build()
    // 使用 Cron 表达式确定该任务应该如何执行
    private val trigger: CronTrigger = TriggerBuilder.newTrigger()
        .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?")).build()

    fun register() {
        quartzScheduler.scheduleJob(jobDetail, trigger)
    }
}