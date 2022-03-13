package com.dauma.grokimkartu.general.utils.time

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class TimeUtilsImpl : TimeUtils {

    companion object {
        private const val dateFormatPattern = "yyyy-MM-dd"
    }

    override fun format(customDate: CustomDate): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val localDate = LocalDate.of(customDate.year, customDate.month, customDate.dayOfMonth)
            val dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormatPattern)
            return localDate.format(dateTimeFormatter)
        } else {
            val date = convertCustomDateToDate(customDate)
            val simpleDateFormatter = SimpleDateFormat(dateFormatPattern)
            return simpleDateFormatter.format(date)
        }
    }

    override fun format(date: Date): String {
        val customDate = convertDateToCustomDate(date)
        return format(customDate)
    }

    override fun convertToTimeInMillis(customDate: CustomDate): Long {
        val date = convertCustomDateToDate(customDate)
        val dateInMillis = date.time
        return dateInMillis
    }

    override fun getCurrentDate(): CustomDate {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentLocalDate = LocalDate.now()
            return CustomDate(currentLocalDate.year, currentLocalDate.monthValue, currentLocalDate.dayOfMonth)
        } else {
            return convertDateToCustomDate(Date())
        }
    }

    private fun convertDateToCustomDate(date: Date) : CustomDate {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return CustomDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun convertCustomDateToDate(customDate: CustomDate) : Date {
        val calendar = Calendar.getInstance()
        calendar.set(customDate.year, customDate.month - 1, customDate.dayOfMonth)
        return calendar.time
    }
}