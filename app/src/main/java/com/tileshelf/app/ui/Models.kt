package com.tileshelf.app.ui

import com.tileshelf.app.catalog.GrantNeed
import com.tileshelf.app.catalog.KeepScreenMode
import com.tileshelf.app.catalog.RowSwitch
import com.tileshelf.app.catalog.Tile

enum class RootTab { Shelf, Grants, About }

sealed interface ShelfModel {
    data object Loading : ShelfModel
    data object Failed : ShelfModel
    data class Ready(
        val query: String,
        val yours: List<ShelfRowModel>,
        val groups: List<ShelfGroupModel>,
    ) : ShelfModel
}

data class ShelfGroupModel(val title: String, val rows: List<ShelfRowModel>)

data class ShelfRowModel(
    val tile: Tile,
    val title: String,
    val supporting: String?,
    val iconRes: Int,
    val switch: RowSwitch,
) {
    val isOn: Boolean
        get() = when (switch) {
            is RowSwitch.Offered -> switch.published
            RowSwitch.TurnOffOnly -> true
        }
}

sealed interface DetailModel {
    val title: String

    data class Counter(override val title: String, val value: Int) : DetailModel

    data class KeepScreen(
        override val title: String,
        val mode: KeepScreenMode,
        val choices: List<ModeChoice>,
    ) : DetailModel

    data class CustomEmpty(override val title: String) : DetailModel

    data class CustomReady(
        override val title: String,
        val label: String,
        val subtitle: String,
        val isLink: Boolean,
        val published: Boolean,
    ) : DetailModel
}

data class ModeChoice(val mode: KeepScreenMode, val label: String)

data class LauncherApp(val packageName: String, val label: String)

sealed interface PickerModel {
    data object Loading : PickerModel
    data object Failed : PickerModel
    data object Empty : PickerModel
    data class Ready(val apps: List<LauncherApp>) : PickerModel
}

data class GrantStepModel(val need: GrantNeed, val title: String, val body: String)

data class GrantStatusModel(val need: GrantNeed, val title: String, val body: String, val held: Boolean)

sealed interface GrantsModel {
    data class Overview(val rows: List<GrantStatusModel>) : GrantsModel
    data class Missing(val tileTitle: String, val steps: List<GrantStepModel>) : GrantsModel
    data class Clear(val tileTitle: String) : GrantsModel
    data object Unknown : GrantsModel
}

sealed interface AboutModel {
    data object Loading : AboutModel
    data object Failed : AboutModel
    data class Ready(val versionName: String) : AboutModel
}

sealed interface Screen {
    data class Shelf(val model: ShelfModel) : Screen
    data class Detail(val model: DetailModel) : Screen
    data class Grants(val model: GrantsModel) : Screen
    data class Picker(val model: PickerModel) : Screen
    data class About(val model: AboutModel) : Screen
}

class ScreenActions(
    val onQuery: (String) -> Unit = { _ -> },
    val onClearQuery: () -> Unit = {},
    val onToggle: (Tile, Boolean) -> Unit = { _, _ -> },
    val onOpen: (Tile) -> Unit = { _ -> },
    val onRetry: () -> Unit = {},
    val onResetCounter: () -> Unit = {},
    val onKeepMode: (KeepScreenMode) -> Unit = { _ -> },
    val onChooseApp: () -> Unit = {},
    val onPickApp: (LauncherApp) -> Unit = { _ -> },
    val onPublish: () -> Unit = {},
    val onGrant: (GrantNeed) -> Unit = { _ -> },
    val onSaveLink: (String, String) -> LinkSave = { _, _ -> LinkSave.Invalid },
)
