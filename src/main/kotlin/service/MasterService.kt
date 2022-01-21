package org.operacon.service

import org.operacon.XiaoXiang.reload
import org.operacon.bean.Bai
import org.operacon.bean.Settings

object MasterService {
    private val groupMap = HashMap<String, Long>()

    fun reloadConfig() {
        Settings.reload()
        Bai.reload()
        for (i in Settings.groups) {
            val j = i.split(":")
            groupMap[j[0]] = j[1].toLong()
        }
    }
}