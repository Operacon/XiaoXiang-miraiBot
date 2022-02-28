/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.bean.scheduledJobs

import org.operacon.bean.Scheduler.groupMessage
import org.operacon.bean.Scheduler.quartzScheduler
import org.operacon.service.GroupCountService
import org.quartz.*

class Statistics : Job {
    override fun execute(context: JobExecutionContext?) {
        // 在此处添加应当执行的任务体。使用 org.operacon.bean.Scheduler.friendMessage 和 groupMessage 发送消息
        for (i: Long in GroupCountService.messageCounter.keys) {
            if (GroupCountService.messageCounter[i]!! == 0)
                continue
            groupMessage(i, "你群昨日共发言 ${GroupCountService.messageCounter[i]!!} 条，" +
                    "其中图片 ${GroupCountService.imageCounter[i]!!} 张" +
                    if (GroupCountService.noneBotCounter[i]!! == GroupCountService.messageCounter[i]!!) "~"
                    else "~\n小湘发言 ${GroupCountService.messageCounter[i]!! - GroupCountService.noneBotCounter[i]!!} 条~"
            )
            GroupCountService.messageCounter[i] = 0
            GroupCountService.imageCounter[i] = 0
            GroupCountService.noneBotCounter[i] = 0
        }
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