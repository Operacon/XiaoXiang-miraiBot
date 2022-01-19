package org.operacon.service

object GroupCountService {
    val messageCounter = HashMap<Long, Int>()
    val baiCounter = HashMap<Long, Int>()

    suspend fun count(gid: Long, mid: Long) {
        messageCounter[gid] = (messageCounter[gid] ?: 0) + 1
        baiCounter[mid] = (baiCounter[mid] ?: 23) + 1
    }
}