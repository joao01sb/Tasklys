package com.joao01sb.tasklys.core.service

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BootReceiverTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testReceiver_isRegisteredForBootCompleted() {
        val intent = Intent(Intent.ACTION_BOOT_COMPLETED)
        val receivers = context.packageManager.queryBroadcastReceivers(
            intent,
            PackageManager.MATCH_ALL
        )

        val hasBootReceiver = receivers.any {
            it.activityInfo.name.contains("BootReceiver")
        }

        assertTrue("BootReceiver must be registered", hasBootReceiver)
    }

    @Test
    fun testReceiver_respondsToQuickBoot() {
        val intent = Intent("android.intent.action.QUICKBOOT_POWERON")
        val receivers = context.packageManager.queryBroadcastReceivers(
            intent,
            PackageManager.MATCH_ALL
        )

        val hasBootReceiver = receivers.any {
            it.activityInfo.name.contains("BootReceiver")
        }

        assertTrue("BootReceiver must respond to QUICKBOOT", hasBootReceiver)
    }

    @Test
    fun testBootReceiver_hasCorrectPermission() {
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS
        )

        val hasPermission = packageInfo.requestedPermissions?.contains(
            "android.permission.RECEIVE_BOOT_COMPLETED"
        ) ?: false

        assertTrue("Requires the RECEIVE_BOOT_COMPLETED permission", hasPermission)
    }
}