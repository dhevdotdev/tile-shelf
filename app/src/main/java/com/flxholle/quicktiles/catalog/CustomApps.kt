package com.flxholle.quicktiles.catalog

import android.content.Context
import androidx.preference.PreferenceManager

data class StoredApp(val packageName: String, val label: String)

object CustomApps {
    fun read(context: Context, slot: CustomSlot): StoredApp? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val packageName = prefs.getString(slot.packageKey, null)?.trim().orEmpty()
        if (packageName.isEmpty()) return null
        val label = prefs.getString(slot.labelKey, null)?.trim().orEmpty()
        return StoredApp(packageName, label.ifEmpty { packageName })
    }

    fun write(context: Context, slot: CustomSlot, app: StoredApp) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putString(slot.packageKey, app.packageName)
            .putString(slot.labelKey, app.label)
            .apply()
    }
}
