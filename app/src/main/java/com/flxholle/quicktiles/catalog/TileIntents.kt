package com.flxholle.quicktiles.catalog

import android.app.PendingIntent
import android.content.Intent
import android.service.quicksettings.TileService
import android.widget.Toast
import androidx.core.service.quicksettings.PendingIntentActivityWrapper
import androidx.core.service.quicksettings.TileServiceCompat
import com.flxholle.quicktiles.R
import com.flxholle.quicktiles.ShelfActivity

object TileIntents {
    @JvmStatic
    fun collapse(service: TileService, intent: Intent?) {
        if (intent == null || intent.resolveActivity(service.packageManager) == null) {
            Toast.makeText(service, R.string.no_app_found, Toast.LENGTH_LONG).show()
            return
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val wrapper = PendingIntentActivityWrapper(
            service,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            false,
        )
        TileServiceCompat.startActivityAndCollapse(service, wrapper)
    }

    @JvmStatic
    fun collapseToGrants(service: TileService, tile: Tile) {
        val intent = Intent(service, ShelfActivity::class.java)
            .putExtra(ShelfActivity.EXTRA_ROUTE, "grants:${tile.name}")
        collapse(service, intent)
    }
}
