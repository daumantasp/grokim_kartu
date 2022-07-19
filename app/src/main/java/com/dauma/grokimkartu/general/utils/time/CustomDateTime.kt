package com.dauma.grokimkartu.general.utils.time

class CustomDateTime {
    var year: Int? = null
    var month: Int? = null
    var dayOfMonth: Int? = null
    var hour: Int? = null
    var minute: Int? = null
    var second: Int? = null

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        this.year = year
        this.month = month
        this.dayOfMonth = dayOfMonth
    }

    fun setTime(hour: Int, minute: Int, second: Int) {
        this.hour = hour
        this.minute = minute
        this.second = second
    }

    fun setDateTime(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        hour: Int,
        minute: Int,
        second: Int
    ) {
        setDate(year, month, dayOfMonth)
        setTime(hour, minute, second)
    }

    fun isSameDay(other: CustomDateTime): Boolean {
        return year == other.year &&
                month == other.month &&
                dayOfMonth == other.dayOfMonth
    }
}