package com.dauma.grokimkartu.general.utils.time

import java.util.*

interface TimeUtils {
    fun format(customDateTime: CustomDateTime, pattern: CustomDateTimeFormatPattern) : String
    fun format(date: Date, pattern: CustomDateTimeFormatPattern) : String
    fun convertToTimeInMillis(customDateTime: CustomDateTime) : Long
    fun convertToCustomDateTime(date: Date) : CustomDateTime
    fun getCurrentDateTime() : CustomDateTime
    fun addDays(customDateTime: CustomDateTime, days: Int) : CustomDateTime
    fun addMonths(customDateTime: CustomDateTime, months: Int) : CustomDateTime
    fun addYears(customDateTime: CustomDateTime, years: Int) : CustomDateTime
    fun parseToCustomDateTime(date: String) : CustomDateTime?
}