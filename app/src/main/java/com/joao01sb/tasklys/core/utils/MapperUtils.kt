package com.joao01sb.tasklys.core.utils

import com.joao01sb.tasklys.core.domain.model.DayOfWeek

object MapperUtils {

    fun parseRecurrenceDays(daysString: String): Set<DayOfWeek> {
        if (daysString.isBlank()) return emptySet()

        return try {
            daysString.split(",")
                .filter { it.isNotBlank() }
                .map { DayOfWeek.valueOf(it.trim()) }
                .toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    fun serializeRecurrenceDays(days: Set<DayOfWeek>): String {
        return days.joinToString(",") { it.name }
    }

}