package com.flxholle.quicktiles

import androidx.compose.runtime.Composable
import com.flxholle.quicktiles.catalog.GrantNeed
import com.flxholle.quicktiles.catalog.KeepScreenMode
import com.flxholle.quicktiles.catalog.RowSwitch
import com.flxholle.quicktiles.catalog.Tile
import com.flxholle.quicktiles.ui.AboutModel
import com.flxholle.quicktiles.ui.DetailModel
import com.flxholle.quicktiles.ui.TileShelfApp
import com.flxholle.quicktiles.ui.GrantStepModel
import com.flxholle.quicktiles.ui.GrantsModel
import com.flxholle.quicktiles.ui.LauncherApp
import com.flxholle.quicktiles.ui.ModeChoice
import com.flxholle.quicktiles.ui.PickerModel
import com.flxholle.quicktiles.ui.RootTab
import com.flxholle.quicktiles.ui.Screen
import com.flxholle.quicktiles.ui.ScreenActions
import com.flxholle.quicktiles.ui.ShelfGroupModel
import com.flxholle.quicktiles.ui.ShelfModel
import com.flxholle.quicktiles.ui.ShelfRowModel
import com.flxholle.quicktiles.ui.theme.AppTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xhdpi")
class ScreenMatrixTest {
    private val screens: Map<String, @Composable () -> Unit> = mapOf(
        "shelf" to { page(Screen.Shelf(shelfReady()), RootTab.Shelf) },
        "shelf_filter_empty" to { page(Screen.Shelf(shelfReady(query = "zzzz", groups = emptyList())), RootTab.Shelf) },
        "shelf_loading" to { page(Screen.Shelf(ShelfModel.Loading), RootTab.Shelf) },
        "shelf_failed" to { page(Screen.Shelf(ShelfModel.Failed), RootTab.Shelf) },
        "detail_counter" to { page(Screen.Detail(DetailModel.Counter("Counter", 12)), RootTab.Shelf) },
        "detail_keep" to { page(Screen.Detail(keepScreen()), RootTab.Shelf) },
        "detail_custom_empty" to { page(Screen.Detail(DetailModel.CustomEmpty("Custom App")), RootTab.Shelf) },
        "detail_custom_ready" to {
            page(
                Screen.Detail(DetailModel.CustomReady("Custom App", "Maps", "com.google.android.apps.maps", false)),
                RootTab.Shelf,
            )
        },
        "grants_missing" to { page(Screen.Grants(grantsMissing()), RootTab.Grants) },
        "grants_clear" to { page(Screen.Grants(GrantsModel.Clear("USB Debugging")), RootTab.Grants) },
        "grants_unknown" to { page(Screen.Grants(GrantsModel.Unknown), RootTab.Grants) },
        "picker_ready" to { page(Screen.Picker(pickerReady()), RootTab.Shelf) },
        "picker_empty" to { page(Screen.Picker(PickerModel.Empty), RootTab.Shelf) },
        "picker_loading" to { page(Screen.Picker(PickerModel.Loading), RootTab.Shelf) },
        "picker_failed" to { page(Screen.Picker(PickerModel.Failed), RootTab.Shelf) },
        "about" to { page(Screen.About(AboutModel.Ready("1.8")), RootTab.About) },
        "about_loading" to { page(Screen.About(AboutModel.Loading), RootTab.About) },
        "about_failed" to { page(Screen.About(AboutModel.Failed), RootTab.About) },
    )

    @Composable
    private fun page(screen: Screen, tab: RootTab) {
        TileShelfApp(screen = screen, tab = tab, onTab = {}, onUp = {}, actions = ScreenActions())
    }

    private fun renderAll(variant: String, dark: Boolean) {
        screens.forEach { (name, content) ->
            captureRoboImage("screenshots/matrix/$variant/$name.png") {
                AppTheme(darkTheme = dark) { content() }
            }
        }
    }

    @Test fun light() = renderAll("light", dark = false)

    @Test fun dark() = renderAll("dark", dark = true)

    @Test
    @Config(sdk = [35], qualifiers = "w411dp-h891dp-xhdpi", fontScale = 2f)
    fun font200() = renderAll("font200", dark = false)

    @Test
    @Config(sdk = [35], qualifiers = "w700dp-h1000dp-xhdpi")
    fun medium() = renderAll("medium", dark = false)

    private fun row(
        tile: Tile,
        title: String,
        supporting: String?,
        icon: Int,
        switch: RowSwitch,
    ) = ShelfRowModel(tile, title, supporting, icon, switch)

    private fun shelfReady(
        query: String = "",
        groups: List<ShelfGroupModel>? = null,
    ): ShelfModel.Ready {
        val built = groups ?: listOf(
            ShelfGroupModel(
                "Media",
                listOf(
                    row(Tile.PlayPause, "Play/Pause", null, R.drawable.ic_play_pause, RowSwitch.Offered(true)),
                    row(Tile.NextSong, "Next", null, R.drawable.ic_skip_next, RowSwitch.Offered(false)),
                    row(Tile.MediaVolume, "Media Volume", null, R.drawable.ic_music_video, RowSwitch.Offered(true)),
                ),
            ),
            ShelfGroupModel(
                "Open Apps",
                listOf(
                    row(Tile.CustomOne, "Custom App", "No app chosen", R.drawable.ic_open_in_new, RowSwitch.Offered(false)),
                    row(Tile.OpenCalculator, "Calculator", null, R.drawable.ic_grid_on, RowSwitch.Offered(false)),
                ),
            ),
            ShelfGroupModel(
                "System Utilities",
                listOf(
                    row(Tile.ClearClipboard, "Clear clipboard", null, R.drawable.ic_clipboard_clear, RowSwitch.Offered(true)),
                    row(Tile.KeepScreenOn, "Keep Screen On", null, R.drawable.ic_keep_screen_on_enabled, RowSwitch.Offered(false)),
                    row(
                        Tile.Grayscale,
                        "Grayscale",
                        "No longer offered. You can turn it off.",
                        R.drawable.ic_grayscale,
                        RowSwitch.TurnOffOnly,
                    ),
                ),
            ),
            ShelfGroupModel(
                "Development",
                listOf(
                    row(
                        Tile.ShowTaps,
                        "Show Taps",
                        "No longer offered. You can turn it off.",
                        R.drawable.ic_show_taps_enabled,
                        RowSwitch.TurnOffOnly,
                    ),
                    row(Tile.UsbDebugging, "USB Debugging", null, R.drawable.ic_usb_debugging_enabled, RowSwitch.Offered(false)),
                ),
            ),
        )
        return ShelfModel.Ready(
            publishedCount = 5,
            catalogSize = 55,
            query = query,
            groups = built,
        )
    }

    private fun keepScreen() = DetailModel.KeepScreen(
        title = "Keep Screen On",
        mode = KeepScreenMode.Any,
        choices = listOf(
            ModeChoice(KeepScreenMode.Ac, "When connected to an AC adapter"),
            ModeChoice(KeepScreenMode.Usb, "When connected to an USB port"),
            ModeChoice(KeepScreenMode.Wireless, "When connected to a wireless source"),
            ModeChoice(KeepScreenMode.Any, "When connected to any power source"),
        ),
    )

    private fun grantsMissing() = GrantsModel.Missing(
        tileTitle = "Demo Mode",
        steps = listOf(
            GrantStepModel(
                GrantNeed.WriteSecure,
                "Secure settings",
                "Run this on a computer with USB debugging:\n\nadb shell pm grant com.tileshelf.app android.permission.WRITE_SECURE_SETTINGS",
            ),
            GrantStepModel(
                GrantNeed.Dump,
                "Dump permission",
                "Run this on a computer with USB debugging:\n\nadb shell pm grant com.tileshelf.app android.permission.DUMP",
            ),
        ),
    )

    private fun pickerReady() = PickerModel.Ready(
        listOf(
            LauncherApp("com.android.calculator2", "Calculator"),
            LauncherApp("com.google.android.apps.maps", "Maps"),
            LauncherApp("com.android.settings", "Settings"),
        ),
    )
}
