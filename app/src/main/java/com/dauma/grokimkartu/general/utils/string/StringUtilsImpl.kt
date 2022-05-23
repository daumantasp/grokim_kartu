package com.dauma.grokimkartu.general.utils.string

class StringUtilsImpl : StringUtils {
    override fun getInitials(fullName: String): String {
        val names = fullName.split("\\s".toRegex())
        var initials = ""
        for (name in names) {
            initials += name.firstOrNull()?.uppercase()
        }
        return initials
    }
}