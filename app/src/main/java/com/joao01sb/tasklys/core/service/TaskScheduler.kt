package com.joao01sb.tasklys.core.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.joao01sb.tasklys.core.domain.model.Note

class TaskScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val EXTRA_TASK_ID = "EXTRA_TASK_ID"
        const val EXTRA_TASK_TITLE = "EXTRA_TASK_TITLE"
        const val EXTRA_TASK_CONTENT = "EXTRA_TASK_CONTENT"
        private const val TAG = "TaskScheduler"
    }

    fun scheduleTaskNotification(task: Note) {
        if (task.expiresAt == null) {
            Log.d(TAG, "Task ${task.id} will not be scheduled")
            return
        }

        if (task.expiresAt <= System.currentTimeMillis()) {
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

            Log.d(TAG, " Scheduled alarm: ${task.title}")
            Log.d(TAG, "   ID: ${task.id}")
            Log.d(TAG, "   From: ${java.util.Date(task.expiresAt)}")
            Log.d(TAG, "   in: ${(task.expiresAt - System.currentTimeMillis()) / 1000}s")
        } catch (e: SecurityException) {
            Log.e(TAG, "Error scheduling alarm", e)
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