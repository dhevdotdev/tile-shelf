package com.tileshelf.app.tiles

import android.graphics.drawable.Icon
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.tileshelf.app.R
import com.tileshelf.app.abstract_tiles.WriteSecureSettingsTileService
import com.tileshelf.app.utils.WriteSystemSettingsUtils

class WirelessDebuggingTileService : WriteSecureSettingsTileService<Int>() {
    companion object {
        const val SETTING = "adb_wifi_enabled"
    }

    override fun isActive(value: Int): Boolean = value != 0

    override fun queryValue(): Int =
        if (WriteSystemSettingsUtils.getIntFromGlobalSettings(contentResolver, SETTING) != 0) 1 else 0

    override fun reset() {
        saveValue(0)
    }

    override fun saveValue(value: Int): Boolean {
        if (value != 0 && !onWifi()) {
            Toast.makeText(applicationContext, R.string.wireless_debugging_needs_wifi, Toast.LENGTH_LONG).show()
            return false
        }
        return WriteSystemSettingsUtils.setIntToGlobalSettings(contentResolver, SETTING, value)
    }

    @Suppress("DEPRECATION")
    private fun onWifi(): Boolean {
        val connectivity = getSystemService(ConnectivityManager::class.java) ?: return false
        return connectivity.allNetworks.any {
            connectivity.getNetworkCapabilities(it)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        }
    }

    override fun getValueList(): List<Int> = listOf(0, 1)

    override fun getIcon(value: Int): Icon =
        Icon.createWithResource(applicationContext, R.drawable.ic_wireless_debugging)

    override fun updateLabel() = false

    override fun getLabel(value: Int): CharSequence = getString(R.string.wireless_debugging)
}
