package com.tileshelf.app

import android.content.ContentResolver
import android.media.AudioManager
import com.tileshelf.app.tiles.AutoSyncTileService
import com.tileshelf.app.tiles.MuteMediaTileService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class NewTilesTest {
    @Test
    fun auto_sync_turns_account_sync_off_and_on() {
        val service = Robolectric.buildService(AutoSyncTileService::class.java).create().get()
        ContentResolver.setMasterSyncAutomatically(true)
        assertEquals(1, service.queryValue())
        assertTrue(service.saveValue(0))
        assertFalse(ContentResolver.getMasterSyncAutomatically())
        assertEquals(0, service.queryValue())
        assertTrue(service.saveValue(1))
        assertTrue(ContentResolver.getMasterSyncAutomatically())
    }

    @Test
    fun mute_media_mutes_and_unmutes_the_music_stream() {
        val service = Robolectric.buildService(MuteMediaTileService::class.java).create().get()
        val audio = RuntimeEnvironment.getApplication().getSystemService(AudioManager::class.java)
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, 7, 0)
        assertEquals(0, service.queryValue())
        assertTrue(service.saveValue(1))
        assertTrue(audio.isStreamMute(AudioManager.STREAM_MUSIC))
        assertEquals(1, service.queryValue())
        assertTrue(service.saveValue(0))
        assertFalse(audio.isStreamMute(AudioManager.STREAM_MUSIC))
        assertEquals(7, audio.getStreamVolume(AudioManager.STREAM_MUSIC))
    }
}
