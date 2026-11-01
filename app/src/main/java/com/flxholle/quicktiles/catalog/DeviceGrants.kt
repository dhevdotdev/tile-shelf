package com.flxholle.quicktiles.catalog

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import com.flxholle.quicktiles.utils.CustomAccessibilityService

object DeviceGrants {
    fun held(context: Context): HeldGrants {
        return HeldGrants(
            writeSecure = granted(context, Manifest.permission.WRITE_SECURE_SETTINGS),
            modifySystem = Settings.System.canWrite(context),
            dump = granted(context, Manifest.permission.DUMP),
            notificationPolicy = notificationPolicy(context),
            accessibility = accessibilityEnabled(context),
        )
    }

    private fun granted(context: Context, permission: String): Boolean =
        context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    private fun notificationPolicy(context: Context): Boolean {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return false
        return manager.isNotificationPolicyAccessGranted
    }

    private fun accessibilityEnabled(context: Context): Boolean {
        val manager = context.getSystemService(AccessibilityManager::class.java) ?: return false
        val enabled = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        val serviceName = CustomAccessibilityService::class.java.name
        return enabled.any { info ->
            val serviceInfo = info.resolveInfo?.serviceInfo ?: return@any false
            serviceInfo.packageName == context.packageName && serviceInfo.name == serviceName
        }
    }
}
