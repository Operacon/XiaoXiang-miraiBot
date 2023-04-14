/*
 * Copyright (C) 2023 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.operacon.component.ChatGLM
import org.operacon.component.GlobalVars
import org.operacon.component.GlobalVars.mediaTypeJson
import java.time.LocalDateTime
import kotlin.coroutines.coroutineContext

object ChatGLMService {
    private val reqMap: HashMap<Long, GLMUserRequest> = HashMap()

    suspend fun groupScan(event: GroupMessageEvent, split: List<String>): Boolean {
        if (split[0] == "glm" && event.group.id in ChatGLM.enabledGroups) {
            if (split.size == 1)
                return true
            var prompt = ""
            split.drop(1).forEach { prompt += it }
            if (!reqMap.containsKey(event.sender.id)) {
                event.group.sendMessage(
                    event.message.quote()
                            + "生成中。ChatGLM响应较慢，每个人都有自己专用的 history list。请适度使用。"
                            + "发送 glm clear 以清除 history。"
                )
                reqMap[event.sender.id] = GLMUserRequest()
            }
            if (!reqMap[event.sender.id]!!.replied) {
                event.group.sendMessage(event.message.quote() + "生成中。等待回复。")
                return true
            }
            if (reqMap[event.sender.id]!!.lastCallTime.plusSeconds(ChatGLM.coolDownDelay)
                    .isAfter(LocalDateTime.now())
            ) {
                event.group.sendMessage(event.message.quote() + "Cooling. Wait.")
                return true
            }
            if (prompt == "clear") {
                reqMap[event.sender.id]!!.history = ArrayList()
                event.group.sendMessage(event.message.quote() + "Done.")
                return true
            }
            reqMap[event.sender.id]!!.lastCallTime = LocalDateTime.now()
            reqMap[event.sender.id]!!.replied = false
            CoroutineScope(coroutineContext).launch {
                val rsp = try {
                    chat(event.sender.id, prompt)
                } catch (_: Exception) {
                    "可能是服务器被撑爆了\n（；´д｀）ゞ"
                }
                event.group.sendMessage(event.message.quote() + rsp + "\n\nChatGLM 冷却 ${ChatGLM.coolDownDelay} 秒。")
                reqMap[event.sender.id]!!.lastCallTime = LocalDateTime.now()
                reqMap[event.sender.id]!!.replied = true
            }
            return true
        }
        return false
    }

    private fun chat(person: Long, prompt: String): String {
        val body = Json.encodeToString(
            GLMReq(
                prompt,
                reqMap[person]!!.history,
                ChatGLM.maxLength,
                ChatGLM.topP,
                ChatGLM.temperature
            )
        ).toRequestBody(mediaTypeJson)
        val request = Request.Builder().url(ChatGLM.url).method("POST", body).build()
        val response = GlobalVars.okHttpClient.newCall(request).execute()
        val rsp = Json.decodeFromString(GLMRsp.serializer(), response.body!!.string())
        if (rsp.status != 200)
            return "但是生成出现故障了\n（；´д｀）ゞ"
        reqMap[person]!!.history = rsp.history
        if (reqMap[person]!!.history.size > ChatGLM.maxHistoryLength)
            reqMap[person]!!.history = reqMap[person]!!.history.drop(1)
        return rsp.response
    }
}

class GLMUserRequest {
    var lastCallTime: LocalDateTime = LocalDateTime.MIN
    var history: List<List<String>> = ArrayList()
    var replied: Boolean = true
}

@Serializable
data class GLMReq(
    val prompt: String,
    val history: List<List<String>>,
    val max_length: Long,
    val top_p: Double,
    val temperature: Double
)

@Serializable
data class GLMRsp(
    val response: String,
    val history: List<List<String>>,
    val status: Int,
    val time: String
)