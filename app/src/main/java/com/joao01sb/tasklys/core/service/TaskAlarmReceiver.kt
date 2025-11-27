package com.joao01sb.tasklys.core.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.joao01sb.tasklys.App
import com.joao01sb.tasklys.R
import com.joao01sb.tasklys.core.data.mapper.toDomain
import com.joao01sb.tasklys.features.notes.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskAlarmReceiver : BroadcastReceiver() {
    
    companion object {
        private const val CHANNEL_ID = "task_reminders"
        private const val CHANNEL_NAME = "Task Reminders"
        private const val TAG = "TaskAlarmReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "========== ALARM TRIGGERED ==========")
        Log.d(TAG, "Intent action: ${intent.action}")
        Log.d(TAG, "Extras: ${intent.extras?.keySet()}")

        val taskId = intent.getLongExtra(TaskScheduler.EXTRA_TASK_ID, -1L)
        val title = intent.getStringExtra(TaskScheduler.EXTRA_TASK_TITLE)
        val content = intent.getStringExtra(TaskScheduler.EXTRA_TASK_CONTENT)

        Log.d(TAG, "taskId: $taskId")
        Log.d(TAG, "title: $title")
        Log.d(TAG, "content: $content")

        if (taskId == null) {
            Log.e(TAG, "TASK_ID é NULL! Aborting.")
            return
        }

        Log.d(TAG, "call showNotification...")

        markTaskAsNotified(context, taskId)

        val isRecurring = intent.getBooleanExtra(TaskScheduler.EXTRA_IS_RECURRING, false)

        showNotification(context, taskId, title ?: "", content ?: "")

        if (isRecurring) {
            rescheduleRecurringTask(context, taskId)
        } else {
            markTaskAsNotified(context, taskId)
        }

        Log.d(TAG, "========== END ==========")
    }

    private fun rescheduleRecurringTask(context: Context, taskId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val databaseDao = (context.applicationContext as App).database.noteDao()
                val task = databaseDao.getNoteById(taskId)
                if (task != null) {
                    val scheduler = TaskScheduler(context)
                    scheduler.scheduleTaskNotification(task.toDomain())
                    Log.d(TAG, "Rescheduled recurring task: $taskId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error rescheduling recurring task", e)
            }
        }
    }

    private fun markTaskAsNotified(context: Context, taskId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dabaseDao = (context.applicationContext as App).database.noteDao()
                dabaseDao.markAsCompleted(taskId)
            } catch (e: Exception) {
                Log.e(TAG, "Error marking as notified", e)
            }
        }
    }
    
    private fun showNotification(
        context: Context,
        taskId: Long,
        title: String,
        content: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        createNotificationChannel(notificationManager)
        
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("TASK_ID", taskId)
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val completeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_COMPLETE
            putExtra("TASK_ID", taskId)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode() + 1,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_SNOOZE
            putExtra("TASK_ID", taskId)
            putExtra("TASK_TITLE", title)
            putExtra("TASK_CONTENT", content)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode() + 2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_DISMISS
            putExtra("notificationId", taskId.hashCode())
        }

        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode() + 3,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_alarm)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setLights(Color.RED, 1000, 1000)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "OK",
                dismissPendingIntent
            )
            .addAction(
                R.drawable.ic_check,
                "Completed",
                completePendingIntent
            )
            .addAction(
                R.drawable.ic_snooze,
                "To postpone 10min",
                snoozePendingIntent
            )
            .build()
        Log.d(TAG, "Notification created")
        notificationManager.notify(taskId.hashCode(), notification)
    }
    
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Scheduled task notifications"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                enableLights(true)
                lightColor = Color.RED
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}