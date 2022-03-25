package com.dauma.grokimkartu.general.utils.time

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class TimeUtilsImpl : TimeUtils {

    companion object {
        private const val separator = "-"
        private const val dateFormatPattern = "yyyy${separator}MM${separator}dd"
    }

    override fun format(customDate: CustomDate): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val localDate = convertCustomDateToLocalDate(customDate)
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

    override fun addYears(customDate: CustomDate, years: Int): CustomDate {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val localDate = convertCustomDateToLocalDate(customDate).plusYears(years.toLong())
            return convertLocalDateToCustomDate(localDate)
        } else {
            val date = convertCustomDateToDate(customDate)
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.YEAR, years)
            return convertDateToCustomDate(calendar.time)
        }
    }

    override fun addMonths(customDate: CustomDate, months: Int): CustomDate {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val localDate = convertCustomDateToLocalDate(customDate).plusMonths(months.toLong())
            return convertLocalDateToCustomDate(localDate)
        } else {
            val date = convertCustomDateToDate(customDate)
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.MONTH, months)
            return convertDateToCustomDate(calendar.time)
        }
    }

    override fun addDays(customDate: CustomDate, days: Int): CustomDate {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val localDate = convertCustomDateToLocalDate(customDate).plusDays(days.toLong())
            return convertLocalDateToCustomDate(localDate)
        } else {
            val date = convertCustomDateToDate(customDate)
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.DAY_OF_MONTH, days)
            return convertDateToCustomDate(calendar.time)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertLocalDateToCustomDate(localDate: LocalDate) : CustomDate {
        return CustomDate(localDate.year, localDate.monthValue, localDate.dayOfMonth)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertCustomDateToLocalDate(customDate: CustomDate) : LocalDate {
        return LocalDate.of(customDate.year, customDate.month, customDate.dayOfMonth)
    }

    private fun convertCustomDateToDate(customDate: CustomDate) : Date {
        val calendar = Calendar.getInstance()
        calendar.set(customDate.year, customDate.month - 1, customDate.dayOfMonth)
        return calendar.time
    }

    override fun parseToDate(date: String): CustomDate? {
        val yearMonthDay = date.split(TimeUtilsImpl.separator)
        if (yearMonthDay.count() == 3) {
            val year = yearMonthDay[0].toIntOrNull()
            val month = yearMonthDay[1].toIntOrNull()
            val day = yearMonthDay[2].toIntOrNull()
            if (year != null && month != null && day != null) {
                return CustomDate(year, month, day)
            }
        }
        return null
    }
}