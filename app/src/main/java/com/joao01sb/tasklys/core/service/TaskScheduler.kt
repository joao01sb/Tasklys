package com.joao01sb.tasklys.core.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.joao01sb.tasklys.core.domain.model.DayOfWeek
import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.model.RecurrenceType
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class TaskScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val EXTRA_TASK_ID = "EXTRA_TASK_ID"
        const val EXTRA_TASK_TITLE = "EXTRA_TASK_TITLE"
        const val EXTRA_TASK_CONTENT = "EXTRA_TASK_CONTENT"
        const val EXTRA_IS_RECURRING = "EXTRA_IS_RECURRING"
        private const val TAG = "TaskScheduler"
    }

    fun scheduleTaskNotification(task: Note) {
        if (task.expiresAt == null) {
            Log.d(TAG, "Task ${task.id} will not be scheduled")
            return
        }

        when (task.recurrenceType) {
            RecurrenceType.ONCE -> scheduleOneTimeNotification(task)
            else -> scheduleRecurringNotification(task)
        }
    }

    private fun scheduleOneTimeNotification(task: Note) {
        if (task.expiresAt!! <= System.currentTimeMillis()) {
            Log.d(TAG, "Task ${task.id} - date has passed")
            return
        }

        val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
            putExtra(EXTRA_TASK_ID, task.id)
            putExtra(EXTRA_TASK_TITLE, task.title)
            putExtra(EXTRA_TASK_CONTENT, task.content)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    task.expiresAt,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    task.expiresAt,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled one-time alarm: ${task.title} at ${Date(task.expiresAt)}")
        } catch (e: SecurityException) {
            Log.e(TAG, "Error scheduling alarm", e)
        }
    }

    private fun scheduleRecurringNotification(task: Note) {
        val nextOccurrence = calculateNextOccurrence(task)

        if (nextOccurrence == null || nextOccurrence <= System.currentTimeMillis()) {
            Log.d(TAG, "No valid next occurrence for task ${task.id}")
            return
        }

        val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
            putExtra(EXTRA_TASK_ID, task.id)
            putExtra(EXTRA_TASK_TITLE, task.title)
            putExtra(EXTRA_TASK_CONTENT, task.content)
            putExtra(EXTRA_IS_RECURRING, true)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextOccurrence,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    nextOccurrence,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled recurring alarm: ${task.title} at ${Date(nextOccurrence)}")
        } catch (e: SecurityException) {
            Log.e(TAG, "Error scheduling alarm", e)
        }
    }

    private fun calculateNextOccurrence(task: Note): Long? {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"))
        val originalTime = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo")).apply {
            timeInMillis = task.expiresAt ?: return null
        }

        calendar.set(Calendar.HOUR_OF_DAY, originalTime.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, originalTime.get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val maxDaysToCheck = 7
        repeat(maxDaysToCheck) {
            if (isDayValid(calendar, task)) {
                return calendar.timeInMillis
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return null
    }

    private fun isDayValid(calendar: Calendar, task: Note): Boolean {
        val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> DayOfWeek.SUNDAY
            Calendar.MONDAY -> DayOfWeek.MONDAY
            Calendar.TUESDAY -> DayOfWeek.TUESDAY
            Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
            Calendar.THURSDAY -> DayOfWeek.THURSDAY
            Calendar.FRIDAY -> DayOfWeek.FRIDAY
            Calendar.SATURDAY -> DayOfWeek.SATURDAY
            else -> return false
        }

        return when (task.recurrenceType) {
            RecurrenceType.DAILY -> true
            RecurrenceType.WEEKDAYS, RecurrenceType.WEEKEND, RecurrenceType.CUSTOM ->
                task.recurrenceDays.contains(dayOfWeek)
            RecurrenceType.ONCE -> false
        }
    }

    fun cancelTaskNotification(taskId: Long) {
        val intent = Intent(context, TaskAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()

        Log.d(TAG, "alarm canceled: $taskId")
    }

    fun rescheduleTaskNotification(task: Note) {
        cancelTaskNotification(task.id)
        scheduleTaskNotification(task)
    }

    fun cancelAllTaskNotifications(taskIds: List<Long>) {
        taskIds.forEach { taskId ->
            cancelTaskNotification(taskId)
        }
        Log.d(TAG, "${taskIds.size} alarms canceled")
    }

}