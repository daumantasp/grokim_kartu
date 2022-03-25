package com.dauma.grokimkartu.general.utils.time

import java.util.*

interface TimeUtils {
    fun format(customDate: CustomDate) : String
    fun format(date: Date) : String
    fun convertToTimeInMillis(customDate: CustomDate) : Long
    fun getCurrentDate() : CustomDate
    fun addDays(customDate: CustomDate, days: Int) : CustomDate
    fun addMonths(customDate: CustomDate, months: Int) : CustomDate
    fun addYears(customDate: CustomDate, years: Int) : CustomDate
    fun parseToDate(date: String) : CustomDate?
}