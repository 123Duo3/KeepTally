package com.konyaco.keeptally.viewmodel.model

import java.time.LocalDate
import java.time.LocalDateTime

sealed class DateRange(val start: LocalDate, val end: LocalDate) {
    data class Month(val year: Int, val month: Int) :
        DateRange(
            LocalDate.of(year, month, 1),
            LocalDate.of(year, month, 1).plusMonths(1)
        ) {
        companion object {
            fun now(): Month {
                val dateTime = LocalDateTime.now()
                return Month(dateTime.year, dateTime.monthValue)
            }
        }
    }

    data class Day(val year: Int, val month: Int, val day: Int) : DateRange(
        LocalDate.of(year, month, day),
        LocalDate.of(year, month, day).plusDays(1)
    ) {
        companion object {
            fun now(): Day {
                val dateTime = LocalDateTime.now()
                return Day(dateTime.year, dateTime.monthValue, dateTime.dayOfMonth)
            }
        }
    }

    class Custom(start: LocalDate, end: LocalDate) : DateRange(start, end)

    override fun equals(other: Any?): Boolean {
        return other is DateRange && other.start == start && other.end == end
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + end.hashCode()
        return result
    }
}