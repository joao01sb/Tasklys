package com.joao01sb.tasklys.core.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.joao01sb.tasklys.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_COMPLETE = "ACTION_COMPLETE_TASK"
        const val ACTION_SNOOZE = "ACTION_SNOOZE_TASK"
        const val ACTION_DISMISS = "DISMISS_NOTIFICATION"
        private const val TAG = "NotificationAction"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("TASK_ID", -1L)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (intent.action) {
            ACTION_COMPLETE -> {
                Log.d(TAG, "Task completed: $taskId")

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val database = (context.applicationContext as App).database
                        val taskDao = database.noteDao()
                        taskDao.markAsCompleted(taskId)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error completing task", e)
                    }
                }

                notificationManager.cancel(taskId.hashCode())
            }

            ACTION_SNOOZE -> {
                Log.d(TAG, "Postponed task: $taskId")

                val title = intent.getStringExtra("TASK_TITLE") ?: "Reminder"
                val content = intent.getStringExtra("TASK_CONTENT") ?: ""

                val snoozeTime = System.currentTimeMillis() + (10 * 60 * 1000)

                val snoozeIntent = Intent(context, TaskAlarmReceiver::class.java).apply {
                    putExtra(TaskScheduler.EXTRA_TASK_ID, taskId)
                    putExtra(TaskScheduler.EXTRA_TASK_TITLE, title)
                    putExtra(TaskScheduler.EXTRA_TASK_CONTENT, content)
                }

                val pendingIntent = android.app.PendingIntent.getBroadcast(
                    context,
                    taskId.hashCode(),
                    snoozeIntent,
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                )

                val alarmManager =
                    context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager

                alarmManager.setExactAndAllowWhileIdle(
                    android.app.AlarmManager.RTC_WAKEUP,
                    snoozeTime,
                    pendingIntent
                )

                notificationManager.cancel(taskId.hashCode())
            }

            ACTION_DISMISS -> {
                Log.d(TAG, "Notification dismissed: $taskId")
                val notificationId = intent.getIntExtra("notificationId", -1)
                if (notificationId != -1) {
                    Log.d(TAG, "Notification dismissed: $notificationId")
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(notificationId)
                }
            }
        }
    }
}