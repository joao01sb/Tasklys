package com.joao01sb.tasklys.core.domain.model

import java.util.Calendar

enum class DayOfWeek {
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY;

    fun toCalendarDay(): Int = when (this) {
        SUNDAY -> Calendar.SUNDAY
        MONDAY -> Calendar.MONDAY
        TUESDAY -> Calendar.TUESDAY
        WEDNESDAY -> Calendar.WEDNESDAY
        THURSDAY -> Calendar.THURSDAY
        FRIDAY -> Calendar.FRIDAY
        SATURDAY -> Calendar.SATURDAY
    }
}