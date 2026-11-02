package com.tileshelf.app.tiles

import android.content.ClipboardManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import com.tileshelf.app.R
import com.tileshelf.app.abstract_tiles.BaseTileService
import com.tileshelf.app.catalog.AndroidClipboard
import com.tileshelf.app.catalog.ClearOutcome
import com.tileshelf.app.catalog.clearClip

class ClearClipboardTileService : BaseTileService() {
    // Bumped when listening repaints, so a delayed restore cannot overwrite that paint.
    private var flashGen = 0

    override fun onStartListening() {
        flashGen++
        super.onStartListening()
    }

    override fun onClick() {
        val manager = getSystemService(ClipboardManager::class.java)
        if (manager == null) {
            show(getString(R.string.clipboard_clear_failed))
            return
        }
        val message = when (clearClip(AndroidClipboard(manager), Build.VERSION.SDK_INT)) {
            ClearOutcome.Cleared, ClearOutcome.ReplacedWithEmpty -> getString(R.string.clipboard_cleared)
            ClearOutcome.Failed -> getString(R.string.clipboard_clear_failed)
        }
        show(message)
    }

    override fun reset() = Unit

    private fun show(message: String) {
        val tile = qsTile ?: return
        val priorLabel = tile.label
        val priorState = tile.state
        val priorSubtitle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) tile.subtitle else null
        val generation = ++flashGen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = message
        } else {
            tile.label = message
        }
        tile.state = Tile.STATE_ACTIVE
        tile.updateTile()
        Handler(Looper.getMainLooper()).postDelayed({
            if (generation != flashGen) return@postDelayed
            val current = qsTile ?: return@postDelayed
            current.label = priorLabel
            current.state = priorState
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                current.subtitle = priorSubtitle
            }
            current.updateTile()
        }, 1500L)
    }
}
