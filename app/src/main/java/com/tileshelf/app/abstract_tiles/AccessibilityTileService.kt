package com.tileshelf.app.abstract_tiles

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.tileshelf.app.R
import com.tileshelf.app.catalog.GateCheck
import com.tileshelf.app.utils.CustomAccessibilityService

abstract class AccessibilityTileService : BaseTileService() {

    override fun onClick() {
        if (!GateCheck.proceed(this)) return
        try {
            startService(
                Intent(this, CustomAccessibilityService::class.java).setAction(
                    getActionString()
                )
            )
        } catch (e: Exception) {
            Log.e("AccessibilityTileService", "Error starting service", e)
            Toast.makeText(this, R.string.an_error_occurred, Toast.LENGTH_LONG).show()
        }
    }

    override fun reset() {}

    abstract fun getActionString(): String
}