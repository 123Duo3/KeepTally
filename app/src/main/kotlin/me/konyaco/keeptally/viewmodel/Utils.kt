package me.konyaco.keeptally.viewmodel

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

fun LocalDate.zonedEpoch(localZoneId: ZoneId = ZoneId.systemDefault()): Long {
    return ZonedDateTime.of(
        year,
        monthValue,
        dayOfMonth,
        0,
        0,
        0,
        0,
        localZoneId
    ).toEpochSecond()
}