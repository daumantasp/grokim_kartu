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
            val calendar = Calendar.getInstance()
            calendar.set(customDate.year, customDate.month - 1, customDate.dayOfMonth)
            val simpleDateFormatter = SimpleDateFormat(dateFormatPattern)
            return simpleDateFormatter.format(calendar.time)
        }
    }

    override fun format(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val customDate = CustomDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        return format(customDate)
    }

    override fun convertToTimeInMillis(customDate: CustomDate): Long {
        val calendar = Calendar.getInstance()
        calendar.set(customDate.year, customDate.month - 1, customDate.dayOfMonth)
        return calendar.timeInMillis
    }
}