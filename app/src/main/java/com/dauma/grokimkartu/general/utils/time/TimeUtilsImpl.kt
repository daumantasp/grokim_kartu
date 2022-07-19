package com.dauma.grokimkartu.general.utils.time

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TimeUtilsImpl : TimeUtils {
    companion object {
        private const val dateSeparator = "-"
        private const val timeSeparator = ":"
        private const val dateTimeSeparator = " "
        private const val yyyyMMdd = "yyyy${dateSeparator}MM${dateSeparator}dd"
        private const val HHmmss = "HH${timeSeparator}mm${timeSeparator}ss"
        private const val yyyyMMddHHmmss = "${yyyyMMdd}${dateTimeSeparator}${HHmmss}"
    }

    override fun format(customDateTime: CustomDateTime, pattern: CustomDateTimeFormatPattern): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dateTimeFormatter = when (pattern) {
                CustomDateTimeFormatPattern.yyyyMMdd -> DateTimeFormatter.ofPattern(yyyyMMdd)
                CustomDateTimeFormatPattern.HHmmss -> DateTimeFormatter.ofPattern(HHmmss)
                CustomDateTimeFormatPattern.yyyyMMddHHmmss -> DateTimeFormatter.ofPattern(yyyyMMddHHmmss)
            }
            val localDateTime = convertToLocalDateTime(customDateTime)
            return localDateTime.format(dateTimeFormatter)
        } else {
            val dateTime = convertToDate(customDateTime)
            val simpleDateFormatter = when (pattern) {
                CustomDateTimeFormatPattern.yyyyMMdd -> SimpleDateFormat(yyyyMMdd)
                CustomDateTimeFormatPattern.HHmmss -> SimpleDateFormat(HHmmss)
                CustomDateTimeFormatPattern.yyyyMMddHHmmss -> SimpleDateFormat(yyyyMMddHHmmss)
            }
            return simpleDateFormatter.format(dateTime)
        }
    }

    override fun format(date: Date, pattern: CustomDateTimeFormatPattern): String {
        val customDateTime = convertToCustomDateTime(date)
        return format(customDateTime, pattern)
    }

    override fun convertToTimeInMillis(customDateTime: CustomDateTime): Long {
        val date = convertToDate(customDateTime)
        val dateInMillis = date.time
        return dateInMillis
    }

    override fun getCurrentDateTime(): CustomDateTime {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentLocalDateTime = LocalDateTime.now()
            return CustomDateTime().apply {
                setDateTime(
                    currentLocalDateTime.year,
                    currentLocalDateTime.monthValue,
                    currentLocalDateTime.dayOfMonth,
                    currentLocalDateTime.hour,
                    currentLocalDateTime.minute,
                    currentLocalDateTime.second
                )
            }
        } else {
            return convertToCustomDateTime(Date())
        }
    }

    override fun addYears(customDateTime: CustomDateTime, years: Int): CustomDateTime {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val localDateTime = convertToLocalDateTime(customDateTime).plusYears(years.toLong())
            return convertToCustomDate(localDateTime)
        } else {
            val date = convertToDate(customDateTime)
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.YEAR, years)
            return convertToCustomDateTime(calendar.time)
        }
    }

    override fun addMonths(customDateTime: CustomDateTime, months: Int): CustomDateTime {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val localDateTime = convertToLocalDateTime(customDateTime).plusMonths(months.toLong())
            return convertToCustomDate(localDateTime)
        } else {
            val date = convertToDate(customDateTime)
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.MONTH, months)
            return convertToCustomDateTime(calendar.time)
        }
    }

    override fun addDays(customDateTime: CustomDateTime, days: Int): CustomDateTime {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val localDateTime = convertToLocalDateTime(customDateTime).plusDays(days.toLong())
            return convertToCustomDate(localDateTime)
        } else {
            val date = convertToDate(customDateTime)
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.DAY_OF_MONTH, days)
            return convertToCustomDateTime(calendar.time)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertToCustomDate(localDateTime: LocalDateTime) : CustomDateTime {
        return CustomDateTime().apply {
            setDateTime(
                localDateTime.year,
                localDateTime.monthValue,
                localDateTime.dayOfMonth,
                localDateTime.hour,
                localDateTime.minute,
                localDateTime.second
            )
        }
    }

    override fun convertToCustomDateTime(date: Date) : CustomDateTime {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return CustomDateTime().apply {
            setDateTime(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertToLocalDateTime(customDateTime: CustomDateTime) : LocalDateTime {
        return LocalDateTime.of(
            customDateTime.year ?: 0,
            customDateTime.month ?: 0,
            customDateTime.dayOfMonth ?: 0,
            customDateTime.hour ?: 0,
            customDateTime.minute ?: 0,
            customDateTime.second ?: 0
        )
    }

    private fun convertToDate(customDateTime: CustomDateTime) : Date {
        val calendar = Calendar.getInstance()
        calendar.set(
            customDateTime.year ?: 0,
            (customDateTime.month ?: 1) - 1,
            customDateTime.dayOfMonth ?: 0,
            customDateTime.hour ?: 0,
            customDateTime.minute ?: 0,
            customDateTime.second ?: 0
        )
        return calendar.time
    }

    override fun parseToCustomDateTime(date: String): CustomDateTime? {
        val dateAndTime = date.split(dateTimeSeparator)

        val yearMonthDay = if (dateAndTime.isEmpty() == false) {
            dateAndTime[0].split(dateSeparator)
        } else {
            date.split(dateSeparator)
        }

        val time = if (dateAndTime.count() == 2) {
            dateAndTime[1].split(timeSeparator)
        } else {
            date.split(timeSeparator)
        }

        val customDateTime = CustomDateTime()

        if (yearMonthDay.count() == 3) {
            val year = yearMonthDay[0].toIntOrNull()
            val month = yearMonthDay[1].toIntOrNull()
            val day = yearMonthDay[2].toIntOrNull()
            if (year != null && month != null && day != null) {
                customDateTime.setDate(year, month, day)
            }
        }

        if (time.count() == 3) {
            val hour = time[0].toIntOrNull()
            val minute = time[1].toIntOrNull()
            val second = time[2].toIntOrNull()
            if (hour != null && minute != null && second != null) {
                customDateTime.setTime(hour, minute, second)
            }
        }

        return customDateTime
    }
}