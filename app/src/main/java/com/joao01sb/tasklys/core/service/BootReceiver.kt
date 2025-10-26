package com.joao01sb.tasklys.core.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.joao01sb.tasklys.App
import com.joao01sb.tasklys.core.data.mapper.toDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "BootReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            Log.d(TAG, "Device restarted - rescheduling tasks")
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val databaseDao = (context.applicationContext as App).database.noteDao()
                    val tasks = databaseDao.getTasksToReschedule()
                    val scheduler = TaskScheduler(context)
                    tasks.forEach { task ->
                        scheduler.scheduleTaskNotification(task.toDomain())
                    }
                    Log.d(TAG, "${tasks.size} rescheduled tasks")
                } catch (e: Exception) {
                    Log.e(TAG, "Error rescheduling tasks", e)
                }
            }
        }
    }
}