package com.tileshelf.app.tiles

import android.graphics.drawable.Icon
import android.media.AudioManager
import com.tileshelf.app.R
import com.tileshelf.app.abstract_tiles.SelectionTileService

class MuteMediaTileService : SelectionTileService<Int>() {
    private val audio: AudioManager? get() = getSystemService(AudioManager::class.java)

    override fun isActive(value: Int): Boolean = value != 0

    override fun queryValue(): Int = if (audio?.isStreamMute(AudioManager.STREAM_MUSIC) == true) 1 else 0

    override fun reset() = Unit

    override fun saveValue(value: Int): Boolean {
        val manager = audio ?: return false
        val direction = if (value != 0) AudioManager.ADJUST_MUTE else AudioManager.ADJUST_UNMUTE
        return try {
            manager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, 0)
            true
        } catch (_: SecurityException) {
            false
        }
    }

    override fun getValueList(): List<Int> = listOf(0, 1)

    override fun getIcon(value: Int): Icon = Icon.createWithResource(
        applicationContext,
        if (value != 0) R.drawable.ic_volume_off else R.drawable.ic_volume_up,
    )

    override fun updateLabel() = false

    override fun getLabel(value: Int): CharSequence = getString(R.string.mute_media)
}
