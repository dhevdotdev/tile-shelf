package com.tileshelf.app.catalog

import android.service.quicksettings.TileService
import com.tileshelf.app.accessibility_tiles.LockScreenTileService
import com.tileshelf.app.accessibility_tiles.PowerDialogTileService
import com.tileshelf.app.accessibility_tiles.ScreenshotTileService
import com.tileshelf.app.accessibility_tiles.ToggleSplitScreenTileService
import com.tileshelf.app.intent_tiles.BatteryTileService
import com.tileshelf.app.intent_tiles.MakeCallTileService
import com.tileshelf.app.intent_tiles.NewAlarmTileService
import com.tileshelf.app.intent_tiles.NewCalendarEventTileService
import com.tileshelf.app.intent_tiles.NewTimerTileService
import com.tileshelf.app.intent_tiles.NextSongTileService
import com.tileshelf.app.intent_tiles.OpenAboutPhoneTileService
import com.tileshelf.app.intent_tiles.OpenAppFiveTileService
import com.tileshelf.app.intent_tiles.OpenAppFourTileService
import com.tileshelf.app.intent_tiles.OpenAppOneTileService
import com.tileshelf.app.intent_tiles.OpenAppThreeTileService
import com.tileshelf.app.intent_tiles.OpenAppTwoTileService
import com.tileshelf.app.intent_tiles.OpenCalculatorTileService
import com.tileshelf.app.intent_tiles.OpenCastTileService
import com.tileshelf.app.intent_tiles.OpenConnectedDevicesTileService
import com.tileshelf.app.intent_tiles.OpenDataUsageTileService
import com.tileshelf.app.intent_tiles.OpenDeveloperOptionsTileService
import com.tileshelf.app.intent_tiles.OpenDisplayTileService
import com.tileshelf.app.intent_tiles.OpenFilesTileService
import com.tileshelf.app.intent_tiles.OpenHomeTileService
import com.tileshelf.app.intent_tiles.OpenLocaleTileService
import com.tileshelf.app.intent_tiles.OpenNotificationLogTileService
import com.tileshelf.app.intent_tiles.OpenPrivacyTileService
import com.tileshelf.app.intent_tiles.OpenSettingsSearchTileService
import com.tileshelf.app.intent_tiles.OpenUserDictionaryTileService
import com.tileshelf.app.intent_tiles.OpenVolumePanelTileService
import com.tileshelf.app.intent_tiles.OpenVpnTileService
import com.tileshelf.app.intent_tiles.PlayPauseTileService
import com.tileshelf.app.intent_tiles.PreviousSongTileService
import com.tileshelf.app.intent_tiles.RecordVideoTileService
import com.tileshelf.app.intent_tiles.TakePhotoTileService
import com.tileshelf.app.tiles.AdaptiveBrightnessTileService
import com.tileshelf.app.tiles.AnimatorDurationTileService
import com.tileshelf.app.tiles.AutoSyncTileService
import com.tileshelf.app.tiles.BrightnessTileService
import com.tileshelf.app.tiles.ClearClipboardTileService
import com.tileshelf.app.tiles.CounterTileService
import com.tileshelf.app.tiles.DemoModeTileService
import com.tileshelf.app.tiles.DoNotDisturbSwitchTileService
import com.tileshelf.app.tiles.FinishActivitiesTileService
import com.tileshelf.app.tiles.FixRotationTileService
import com.tileshelf.app.tiles.GrayscaleTileService
import com.tileshelf.app.tiles.HeadsUpTileService
import com.tileshelf.app.tiles.KeepScreenOnTileService
import com.tileshelf.app.tiles.MuteMediaTileService
import com.tileshelf.app.tiles.MediaVolumeTileService
import com.tileshelf.app.tiles.RotationSwitchTileService
import com.tileshelf.app.tiles.ScreenTimeoutTileService
import com.tileshelf.app.tiles.ShowTapsTileService
import com.tileshelf.app.tiles.SilenceLoudSwitchTileService
import com.tileshelf.app.tiles.ToggleAnimationTileService
import com.tileshelf.app.tiles.UsbDebuggingTileService
import com.tileshelf.app.tiles.VibrateCallsTileService
import kotlin.reflect.KClass

enum class ShelfSection {
    Media,
    OpenApps,
    Settings,
    Utilities,
    Miscellaneous,
    Development,
}

enum class Tile(
    val key: String,
    val service: KClass<out TileService>,
    val section: ShelfSection,
    val gate: Gate = Gate.None,
    val offer: Offer = Offer.Always,
    val detail: Detail = Detail.None,
    val activeTile: Boolean,
) {
    PlayPause("play_pause", PlayPauseTileService::class, ShelfSection.Media, activeTile = true),
    NextSong("next_song", NextSongTileService::class, ShelfSection.Media, activeTile = true),
    PreviousSong("previous_song", PreviousSongTileService::class, ShelfSection.Media, activeTile = true),
    MediaVolume("media_volume", MediaVolumeTileService::class, ShelfSection.Media, activeTile = true),
    MuteMedia("mute_media", MuteMediaTileService::class, ShelfSection.Media, activeTile = false),
    OpenVolumePanel("open_volume_panel", OpenVolumePanelTileService::class, ShelfSection.Media, activeTile = true),

    NewAlarm("new_alarm", NewAlarmTileService::class, ShelfSection.OpenApps, activeTile = true),
    NewTimer("new_timer", NewTimerTileService::class, ShelfSection.OpenApps, activeTile = true),
    NewEvent("new_event", NewCalendarEventTileService::class, ShelfSection.OpenApps, activeTile = true),
    MakeCall("make_call", MakeCallTileService::class, ShelfSection.OpenApps, activeTile = true),
    TakePhoto("take_photo", TakePhotoTileService::class, ShelfSection.OpenApps, activeTile = true),
    RecordVideo("record_video", RecordVideoTileService::class, ShelfSection.OpenApps, activeTile = true),
    OpenCalculator("open_calculator", OpenCalculatorTileService::class, ShelfSection.OpenApps, activeTile = true),
    OpenFiles("open_files", OpenFilesTileService::class, ShelfSection.OpenApps, activeTile = false),
    CustomOne("custom_app_one", OpenAppOneTileService::class, ShelfSection.OpenApps, detail = Detail.CustomApp(CustomSlot.One), activeTile = true),
    CustomTwo("custom_app_two", OpenAppTwoTileService::class, ShelfSection.OpenApps, detail = Detail.CustomApp(CustomSlot.Two), activeTile = true),
    CustomThree("custom_app_three", OpenAppThreeTileService::class, ShelfSection.OpenApps, detail = Detail.CustomApp(CustomSlot.Three), activeTile = true),
    CustomFour("custom_app_four", OpenAppFourTileService::class, ShelfSection.OpenApps, detail = Detail.CustomApp(CustomSlot.Four), activeTile = true),
    CustomFive("custom_app_five", OpenAppFiveTileService::class, ShelfSection.OpenApps, detail = Detail.CustomApp(CustomSlot.Five), activeTile = true),

    SearchSettings("search_settings", OpenSettingsSearchTileService::class, ShelfSection.Settings, activeTile = true),
    OpenLanguage("open_language", OpenLocaleTileService::class, ShelfSection.Settings, activeTile = true),
    OpenUserDictionary("open_user_dictionary", OpenUserDictionaryTileService::class, ShelfSection.Settings, activeTile = true),
    OpenPrivacy("open_privacy", OpenPrivacyTileService::class, ShelfSection.Settings, activeTile = true),
    OpenDisplay("open_display", OpenDisplayTileService::class, ShelfSection.Settings, activeTile = true),
    OpenHome("open_home", OpenHomeTileService::class, ShelfSection.Settings, activeTile = true),
    NotificationHistory("notification_history", OpenNotificationLogTileService::class, ShelfSection.Settings, activeTile = true),
    Vpn("vpn", OpenVpnTileService::class, ShelfSection.Settings, activeTile = true),
    DataUsage("data_usage", OpenDataUsageTileService::class, ShelfSection.Settings, activeTile = true),
    ScreenCast("screen_cast", OpenCastTileService::class, ShelfSection.Settings, activeTile = true),
    ConnectedDevices("connected_devices", OpenConnectedDevicesTileService::class, ShelfSection.Settings, activeTile = true),
    AboutPhone("about_phone", OpenAboutPhoneTileService::class, ShelfSection.Settings, activeTile = true),

    ClearClipboard("clear_clipboard", ClearClipboardTileService::class, ShelfSection.Utilities, activeTile = true),
    ScreenTimeout("screen_timeout", ScreenTimeoutTileService::class, ShelfSection.Utilities, Gate.ModifySystem, activeTile = false),
    KeepScreenOn("keep_screen_on", KeepScreenOnTileService::class, ShelfSection.Utilities, Gate.SecureAndModifySystem, detail = Detail.KeepScreen, activeTile = false),
    SwitchStates("switch_states", DoNotDisturbSwitchTileService::class, ShelfSection.Utilities, Gate.NotificationPolicy, activeTile = false),
    SwitchSilenceLoud("switch_silence_loud", SilenceLoudSwitchTileService::class, ShelfSection.Utilities, Gate.NotificationPolicy, activeTile = false),
    AdjustBrightness("adjust_brightness", BrightnessTileService::class, ShelfSection.Utilities, Gate.ModifySystem, activeTile = false),
    AdaptiveBrightness("adaptive_brightness", AdaptiveBrightnessTileService::class, ShelfSection.Utilities, Gate.ModifySystem, activeTile = false),
    Grayscale("grayscale", GrayscaleTileService::class, ShelfSection.Utilities, Gate.WriteSecure, Offer.Until(30), activeTile = true),
    TakeScreenshot("take_screenshot", ScreenshotTileService::class, ShelfSection.Utilities, Gate.Accessibility, Offer.Since(28), activeTile = true),
    ForceRotation("force_rotation", RotationSwitchTileService::class, ShelfSection.Utilities, Gate.ModifySystem, activeTile = false),
    FixRotation("fix_rotation", FixRotationTileService::class, ShelfSection.Utilities, Gate.ModifySystem, activeTile = false),
    LockScreen("lock_screen", LockScreenTileService::class, ShelfSection.Utilities, Gate.Accessibility, Offer.Since(28), activeTile = true),
    ToggleSplitScreen("toggle_split_screen", ToggleSplitScreenTileService::class, ShelfSection.Utilities, Gate.Accessibility, activeTile = true),
    VibrateCalls("vibrate_calls", VibrateCallsTileService::class, ShelfSection.Utilities, Gate.ModifySystem, activeTile = false),
    PowerDialog("power_dialog", PowerDialogTileService::class, ShelfSection.Utilities, Gate.Accessibility, activeTile = true),
    AutoSync("auto_sync", AutoSyncTileService::class, ShelfSection.Utilities, activeTile = false),
    Battery("battery", BatteryTileService::class, ShelfSection.Utilities, activeTile = false),
    HeadsUp("heads_up", HeadsUpTileService::class, ShelfSection.Utilities, Gate.WriteSecure, activeTile = false),

    Counter("counter", CounterTileService::class, ShelfSection.Miscellaneous, detail = Detail.Counter, activeTile = true),

    UsbDebugging("usb_debugging", UsbDebuggingTileService::class, ShelfSection.Development, Gate.WriteSecure, activeTile = false),
    DemoMode("demo_mode", DemoModeTileService::class, ShelfSection.Development, Gate.SecureAndDump, activeTile = false),
    DeveloperOptions("developer_options", OpenDeveloperOptionsTileService::class, ShelfSection.Development, activeTile = true),
    ShowTaps("show_taps", ShowTapsTileService::class, ShelfSection.Development, Gate.WriteSecure, Offer.Never, activeTile = false),
    AnimatorDuration("animator_duration", AnimatorDurationTileService::class, ShelfSection.Development, Gate.WriteSecure, activeTile = false),
    DisableAllAnimations("disable_all_animations", ToggleAnimationTileService::class, ShelfSection.Development, Gate.WriteSecure, activeTile = false),
    FinishActivities("finish_activities", FinishActivitiesTileService::class, ShelfSection.Development, Gate.WriteSecure, activeTile = false),
    ;

    companion object {
        private val byService: Map<Class<out TileService>, Tile> =
            entries.associateBy { it.service.java }
        private val byKey: Map<String, Tile> =
            entries.associateBy { it.key }

        init {
            require(entries.size == 57)
            require(byService.size == entries.size)
            require(byKey.size == entries.size)
            val slots = entries.mapNotNull { (it.detail as? Detail.CustomApp)?.slot }
            require(slots.toSet() == CustomSlot.entries.toSet())
            require(entries.none { it.detail is Detail.CustomApp && it.gate != Gate.None })
        }

        @JvmStatic
        fun fromService(service: Class<out TileService>): Tile = byService.getValue(service)

        fun fromKey(key: String): Tile? = byKey[key]

        fun fromRoute(name: String): Tile? = entries.firstOrNull { it.name == name }
    }
}
