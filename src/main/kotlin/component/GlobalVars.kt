/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE
 */
package org.operacon.component

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object GlobalVars {
    val splitter = Regex(""" +""")
    val atReplace = Regex("""@\d{5,10} ?""")
    val pagReplace = Regex("""\[.+]""")
    val atMe = Regex("@" + Settings.selfId.toString())
    val pos = Regex("""\d{2,3}\.\d{6}""")

    val okHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS).build()
    val mediaTypePlain = "text/plain".toMediaTypeOrNull()
    val mediaTypeJson = "application/json".toMediaTypeOrNull()
}