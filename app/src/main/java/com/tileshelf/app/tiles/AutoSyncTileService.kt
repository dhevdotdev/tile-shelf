package com.tileshelf.app.tiles

import android.content.ContentResolver
import android.graphics.drawable.Icon
import com.tileshelf.app.R
import com.tileshelf.app.abstract_tiles.SelectionTileService

class AutoSyncTileService : SelectionTileService<Int>() {
    override fun isActive(value: Int): Boolean = value != 0

    override fun queryValue(): Int = if (ContentResolver.getMasterSyncAutomatically()) 1 else 0

    override fun reset() = Unit

    override fun saveValue(value: Int): Boolean = try {
        ContentResolver.setMasterSyncAutomatically(value != 0)
        true
    } catch (_: SecurityException) {
        false
    }

    override fun getValueList(): List<Int> = listOf(0, 1)

    override fun getIcon(value: Int): Icon = Icon.createWithResource(
        applicationContext,
        if (value != 0) R.drawable.ic_sync else R.drawable.ic_sync_disabled,
    )

    override fun updateLabel() = false

    override fun getLabel(value: Int): CharSequence = getString(R.string.auto_sync)
}
