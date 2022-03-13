package com.dauma.grokimkartu.general.utils.time

import java.util.*

interface TimeUtils {
    fun format(customDate: CustomDate) : String
    fun format(date: Date) : String
    fun convertToTimeInMillis(customDate: CustomDate) : Long
    fun getCurrentDate() : CustomDate
}