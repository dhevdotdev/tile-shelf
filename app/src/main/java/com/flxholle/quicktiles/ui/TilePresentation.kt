package com.flxholle.quicktiles.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.flxholle.quicktiles.R
import com.flxholle.quicktiles.catalog.ShelfSection
import com.flxholle.quicktiles.catalog.Tile

@StringRes
fun tileTitleRes(tile: Tile): Int = titleRes.getValue(tile)

@DrawableRes
fun tileIconRes(tile: Tile): Int = iconRes.getValue(tile)

@StringRes
fun sectionTitleRes(section: ShelfSection): Int = when (section) {
    ShelfSection.Media -> R.string.media_control
    ShelfSection.OpenApps -> R.string.open_apps
    ShelfSection.Settings -> R.string.settings
    ShelfSection.Utilities -> R.string.system_utilities
    ShelfSection.Miscellaneous -> R.string.miscellaneous
    ShelfSection.Development -> R.string.development
}

private val titleRes: Map<Tile, Int> = mapOf(
    Tile.PlayPause to R.string.play_pause,
    Tile.NextSong to R.string.next_song,
    Tile.PreviousSong to R.string.previous_song,
    Tile.MediaVolume to R.string.media_volume,
    Tile.OpenVolumePanel to R.string.open_volume_panel,
    Tile.NewAlarm to R.string.new_alarm,
    Tile.NewTimer to R.string.new_timer,
    Tile.NewEvent to R.string.new_event,
    Tile.MakeCall to R.string.make_call,
    Tile.TakePhoto to R.string.take_photo,
    Tile.RecordVideo to R.string.record_video,
    Tile.OpenCalculator to R.string.open_calculator,
    Tile.OpenFiles to R.string.open_files,
    Tile.CustomOne to R.string.custom_app,
    Tile.CustomTwo to R.string.custom_app,
    Tile.CustomThree to R.string.custom_app,
    Tile.CustomFour to R.string.custom_app,
    Tile.CustomFive to R.string.custom_app,
    Tile.SearchSettings to R.string.search_settings,
    Tile.OpenLanguage to R.string.languages,
    Tile.OpenUserDictionary to R.string.personal_dictionary,
    Tile.OpenPrivacy to R.string.privacy,
    Tile.OpenDisplay to R.string.display,
    Tile.OpenHome to R.string.home_app,
    Tile.NotificationHistory to R.string.notification_history,
    Tile.Vpn to R.string.vpn,
    Tile.DataUsage to R.string.data_usage,
    Tile.ScreenCast to R.string.screen_cast,
    Tile.ConnectedDevices to R.string.connected_devices,
    Tile.AboutPhone to R.string.about_phone,
    Tile.ClearClipboard to R.string.clear_clipboard,
    Tile.ScreenTimeout to R.string.screen_timeout,
    Tile.KeepScreenOn to R.string.keep_screen_on,
    Tile.SwitchStates to R.string.switch_volume_modes,
    Tile.SwitchSilenceLoud to R.string.switch_silence_loud,
    Tile.AdjustBrightness to R.string.adjust_brightness,
    Tile.AdaptiveBrightness to R.string.adaptive_brightness,
    Tile.Grayscale to R.string.grayscale,
    Tile.TakeScreenshot to R.string.screenshot,
    Tile.ForceRotation to R.string.force_rotation,
    Tile.FixRotation to R.string.fix_rotation,
    Tile.LockScreen to R.string.lock_screen,
    Tile.ToggleSplitScreen to R.string.split_screen,
    Tile.VibrateCalls to R.string.vibrate_on_calls,
    Tile.PowerDialog to R.string.power_menu,
    Tile.Battery to R.string.battery,
    Tile.HeadsUp to R.string.heads_up,
    Tile.Counter to R.string.counter,
    Tile.UsbDebugging to R.string.usb_debugging,
    Tile.DemoMode to R.string.demo_mode,
    Tile.DeveloperOptions to R.string.developer_options,
    Tile.ShowTaps to R.string.show_taps,
    Tile.AnimatorDuration to R.string.animator_duration,
    Tile.DisableAllAnimations to R.string.disable_all_animations,
    Tile.FinishActivities to R.string.finish_activities,
)

private val iconRes: Map<Tile, Int> = mapOf(
    Tile.PlayPause to R.drawable.ic_play_pause,
    Tile.NextSong to R.drawable.ic_skip_next,
    Tile.PreviousSong to R.drawable.ic_skip_previous,
    Tile.MediaVolume to R.drawable.ic_music_video,
    Tile.OpenVolumePanel to R.drawable.ic_volume_up,
    Tile.NewAlarm to R.drawable.ic_alarm_add,
    Tile.NewTimer to R.drawable.ic_timer,
    Tile.NewEvent to R.drawable.ic_event,
    Tile.MakeCall to R.drawable.ic_call,
    Tile.TakePhoto to R.drawable.ic_photo_camera,
    Tile.RecordVideo to R.drawable.ic_videocam,
    Tile.OpenCalculator to R.drawable.ic_grid_on,
    Tile.OpenFiles to R.drawable.ic_folder,
    Tile.CustomOne to R.drawable.ic_open_in_new,
    Tile.CustomTwo to R.drawable.ic_open_in_new,
    Tile.CustomThree to R.drawable.ic_open_in_new,
    Tile.CustomFour to R.drawable.ic_open_in_new,
    Tile.CustomFive to R.drawable.ic_open_in_new,
    Tile.SearchSettings to R.drawable.ic_search,
    Tile.OpenLanguage to R.drawable.ic_language,
    Tile.OpenUserDictionary to R.drawable.ic_dictionary,
    Tile.OpenPrivacy to R.drawable.ic_privacy,
    Tile.OpenDisplay to R.drawable.ic_phonelink_setup,
    Tile.OpenHome to R.drawable.ic_home,
    Tile.NotificationHistory to R.drawable.ic_history,
    Tile.Vpn to R.drawable.ic_vpn_key,
    Tile.DataUsage to R.drawable.ic_data_usage,
    Tile.ScreenCast to R.drawable.ic_cast,
    Tile.ConnectedDevices to R.drawable.ic_devices_other,
    Tile.AboutPhone to R.drawable.ic_perm_device_information,
    Tile.ClearClipboard to R.drawable.ic_clipboard_clear,
    Tile.ScreenTimeout to R.drawable.ic_screen_infinity,
    Tile.KeepScreenOn to R.drawable.ic_keep_screen_on_enabled,
    Tile.SwitchStates to R.drawable.ic_ring_volume,
    Tile.SwitchSilenceLoud to R.drawable.ic_ring_volume,
    Tile.AdjustBrightness to R.drawable.ic_brightness_high,
    Tile.AdaptiveBrightness to R.drawable.ic_brightness_auto,
    Tile.Grayscale to R.drawable.ic_grayscale,
    Tile.TakeScreenshot to R.drawable.ic_screenshot,
    Tile.ForceRotation to R.drawable.ic_screen_lock_rotation,
    Tile.FixRotation to R.drawable.ic_screen_lock_rotation,
    Tile.LockScreen to R.drawable.ic_lock_screen,
    Tile.ToggleSplitScreen to R.drawable.ic_split_screen,
    Tile.VibrateCalls to R.drawable.ic_vibration,
    Tile.PowerDialog to R.drawable.ic_power_dialog,
    Tile.Battery to R.drawable.ic_battery_full,
    Tile.HeadsUp to R.drawable.ic_heads_up,
    Tile.Counter to R.drawable.ic_exposure_plus_1,
    Tile.UsbDebugging to R.drawable.ic_usb_debugging_enabled,
    Tile.DemoMode to R.drawable.ic_demo_mode_enabled,
    Tile.DeveloperOptions to R.drawable.ic_developer_mode,
    Tile.ShowTaps to R.drawable.ic_show_taps_enabled,
    Tile.AnimatorDuration to R.drawable.ic_animator_duration_enabled,
    Tile.DisableAllAnimations to R.drawable.ic_animation_on,
    Tile.FinishActivities to R.drawable.ic_finish_activities_enabled,
)
