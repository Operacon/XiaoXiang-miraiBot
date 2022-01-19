package org.operacon.service

import org.operacon.bean.Config

object MasterService {
    private val groupMap = HashMap<String, Long>()

    fun initGroupMap() {
        for (i in Config.groups) {
            val j = i.split(":")
            groupMap[j[0]] = j[1].toLong()
        }
    }
}