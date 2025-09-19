package com.joao01sb.tasklys.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    fun formatDate(timestamp: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    fun getTimeUntilExpiry(expiryTime: Long): String {
        val now = System.currentTimeMillis()
        val diff = expiryTime - now

        return when {
            diff < 0 -> "Venceu há ${formatTimeDifference(-diff)}"
            diff < 24 * 60 * 60 * 1000 -> "Vence em ${formatTimeDifference(diff)}"
            else -> "Vence em ${formatTimeDifference(diff)}"
        }
    }

    fun formatTimeDifference(milliseconds: Long): String {
        val days = milliseconds / (24 * 60 * 60 * 1000)
        val hours = (milliseconds % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
        val minutes = (milliseconds % (60 * 60 * 1000)) / (60 * 1000)

        return when {
            days > 0 -> "${days}d ${hours}h"
            hours > 0 -> "${hours}h ${minutes}min"
            else -> "${minutes}min"
        }
    }

}