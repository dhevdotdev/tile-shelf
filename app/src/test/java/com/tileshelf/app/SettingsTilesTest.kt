package com.tileshelf.app

import android.app.Application
import android.provider.Settings
import com.tileshelf.app.abstract_tiles.SelectionTileService
import com.tileshelf.app.tiles.AnimatorDurationTileService
import com.tileshelf.app.tiles.DemoModeTileService
import com.tileshelf.app.tiles.FinishActivitiesTileService
import com.tileshelf.app.tiles.FixRotationTileService
import com.tileshelf.app.tiles.HeadsUpTileService
import com.tileshelf.app.tiles.KeepScreenOnTileService
import com.tileshelf.app.tiles.RotationSwitchTileService
import com.tileshelf.app.tiles.ToggleAnimationTileService
import com.tileshelf.app.tiles.UsbDebuggingTileService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SettingsTilesTest {
    private val resolver get() = RuntimeEnvironment.getApplication().contentResolver

    private fun <T : SelectionTileService<*>> tile(type: Class<T>): T =
        Robolectric.buildService(type).create().get()

    private fun <V : Any> roundTrips(service: SelectionTileService<V>) {
        service.getValueList().forEach { value ->
            assertTrue("save $value", service.saveValue(value))
            assertEquals(value, service.queryValue())
        }
    }

    private fun global(key: String) = Settings.Global.getInt(resolver, key)
    private fun globalFloat(key: String) = Settings.Global.getFloat(resolver, key)
    private fun system(key: String) = Settings.System.getInt(resolver, key)

    @Test
    fun heads_up_writes_the_global_switch() {
        val service = tile(HeadsUpTileService::class.java)
        roundTrips(service)
        service.saveValue(0)
        assertEquals(0, global("heads_up_notifications_enabled"))
        service.saveValue(1)
        assertEquals(1, global("heads_up_notifications_enabled"))
    }

    @Test
    fun usb_debugging_writes_adb_enabled() {
        val service = tile(UsbDebuggingTileService::class.java)
        roundTrips(service)
        service.saveValue(1)
        assertEquals(1, global(Settings.Global.ADB_ENABLED))
    }

    @Test
    fun always_finish_activities_writes_its_global() {
        val service = tile(FinishActivitiesTileService::class.java)
        roundTrips(service)
        service.saveValue(1)
        assertEquals(1, global(Settings.Global.ALWAYS_FINISH_ACTIVITIES))
    }

    @Test
    fun animator_duration_cycles_through_every_scale() {
        val service = tile(AnimatorDurationTileService::class.java)
        roundTrips(service)
        service.saveValue(0.5f)
        assertEquals(0.5f, globalFloat(Settings.Global.ANIMATOR_DURATION_SCALE), 0f)
    }

    @Test
    fun disable_all_animations_sets_all_three_scales() {
        val service = tile(ToggleAnimationTileService::class.java)
        roundTrips(service)
        service.saveValue(0f)
        listOf(
            Settings.Global.ANIMATOR_DURATION_SCALE,
            Settings.Global.TRANSITION_ANIMATION_SCALE,
            Settings.Global.WINDOW_ANIMATION_SCALE,
        ).forEach { assertEquals(it, 0f, globalFloat(it), 0f) }
    }

    @Test
    fun demo_mode_sets_both_globals_and_tells_system_ui() {
        val service = tile(DemoModeTileService::class.java)
        roundTrips(service)
        val app = shadowOf(RuntimeEnvironment.getApplication() as Application)
        app.clearBroadcastIntents()
        service.saveValue(1)
        assertEquals(1, global("sysui_demo_allowed"))
        assertEquals(1, global("sysui_tuner_demo_on"))
        val commands = app.broadcastIntents
            .filter { it.action == "com.android.systemui.demo" }
            .map { it.getStringExtra("command") }
        assertEquals("enter", commands.first())
        app.clearBroadcastIntents()
        service.saveValue(0)
        assertEquals(
            listOf("exit"),
            app.broadcastIntents.filter { it.action == "com.android.systemui.demo" }.map { it.getStringExtra("command") },
        )
    }

    @Test
    fun keep_screen_on_any_source_writes_every_plug_bit_and_keeps_the_timeout() {
        Settings.System.putInt(resolver, Settings.System.SCREEN_OFF_TIMEOUT, 120000)
        val service = tile(KeepScreenOnTileService::class.java)
        roundTrips(service)
        service.saveValue(KeepScreenOnTileService.ANY)
        assertEquals(7, global(Settings.Global.STAY_ON_WHILE_PLUGGED_IN))
        assertEquals(120000, system(Settings.System.SCREEN_OFF_TIMEOUT))
    }

    @Test
    fun keep_screen_on_off_restores_a_timeout_an_older_build_left_at_never() {
        Settings.System.putInt(resolver, Settings.System.SCREEN_OFF_TIMEOUT, 90000)
        val service = tile(KeepScreenOnTileService::class.java)
        service.queryValue()
        Settings.System.putInt(resolver, Settings.System.SCREEN_OFF_TIMEOUT, Int.MAX_VALUE)
        assertEquals(KeepScreenOnTileService.ANY, service.queryValue())
        service.saveValue(KeepScreenOnTileService.OFF)
        assertEquals(90000, system(Settings.System.SCREEN_OFF_TIMEOUT))
        assertEquals(0, global(Settings.Global.STAY_ON_WHILE_PLUGGED_IN))
    }

    @Test
    fun force_rotation_locks_each_orientation_and_returns_to_auto() {
        val service = tile(RotationSwitchTileService::class.java)
        roundTrips(service)
        service.saveValue(RotationSwitchTileService.LANDSCAPE)
        assertEquals(0, system(Settings.System.ACCELEROMETER_ROTATION))
        assertEquals(RotationSwitchTileService.LANDSCAPE, system(Settings.System.USER_ROTATION))
        service.saveValue(RotationSwitchTileService.AUTO_ROTATION)
        assertEquals(1, system(Settings.System.ACCELEROMETER_ROTATION))
    }

    @Test
    fun fix_rotation_round_trips_its_values() {
        roundTrips(tile(FixRotationTileService::class.java))
    }
}
