package org.operacon.bean

object GlobalVars {
    val splitter = Regex(""" +""")
    val atReplace = Regex("""@[0-9]{5,10} ?""")
    val pagReplace = Regex("\\[.+?\\]")
}