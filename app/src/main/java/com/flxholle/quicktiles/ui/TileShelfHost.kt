package com.flxholle.quicktiles.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.flxholle.quicktiles.R
import com.flxholle.quicktiles.catalog.Detail
import com.flxholle.quicktiles.catalog.Tile
import kotlinx.coroutines.launch

@Composable
fun DhevHost(
    device: DeviceActions,
    grantsRoute: String?,
    routeToken: Int,
    generation: Int,
) {
    var tabName by rememberSaveable {
        mutableStateOf(if (grantsRoute != null) RootTab.Grants.name else RootTab.Shelf.name)
    }
    var detailName by rememberSaveable { mutableStateOf<String?>(null) }
    var pickerName by rememberSaveable { mutableStateOf<String?>(null) }
    var grantsName by rememberSaveable { mutableStateOf(grantsRoute) }
    var query by rememberSaveable { mutableStateOf("") }
    var revision by rememberSaveable { mutableIntStateOf(0) }
    var seenRoute by rememberSaveable { mutableIntStateOf(Int.MIN_VALUE) }
    var picker by remember { mutableStateOf<PickerModel>(PickerModel.Loading) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val copied = stringResource(R.string.copied)

    LaunchedEffect(routeToken) {
        if (routeToken == seenRoute) return@LaunchedEffect
        seenRoute = routeToken
        if (grantsRoute != null) {
            tabName = RootTab.Grants.name
            grantsName = grantsRoute
            detailName = null
            pickerName = null
        }
    }

    val detailTile = detailName?.let(Tile::fromRoute)
    LaunchedEffect(pickerName, generation, revision) {
        if (pickerName == null) return@LaunchedEffect
        picker = PickerModel.Loading
        picker = device.loadPicker()
    }

    val shelf = remember(query, generation, revision) { device.loadShelf(query) }
    val about = remember(generation, revision) { device.loadAbout() }
    val grants = remember(grantsName, generation, revision) { device.loadGrants(grantsName) }
    val detail = remember(detailName, generation, revision) {
        detailTile?.let(device::loadDetail)
    }

    fun openGrants(name: String) {
        tabName = RootTab.Grants.name
        grantsName = name
        detailName = null
        pickerName = null
    }

    fun applyToggle(tile: Tile, on: Boolean) {
        when (val outcome = device.toggle(tile, on)) {
            ToggleOutcome.Updated -> revision++
            ToggleOutcome.Refused -> Unit
            is ToggleOutcome.NeedsGrant -> openGrants(outcome.tile.name)
            is ToggleOutcome.NeedsApp -> {
                detailName = outcome.tile.name
                pickerName = null
                revision++
            }
        }
    }

    val tab = RootTab.entries.firstOrNull { it.name == tabName } ?: RootTab.Shelf
    val screen = when {
        pickerName != null -> Screen.Picker(picker)
        detail != null -> Screen.Detail(detail)
        tab == RootTab.Grants -> Screen.Grants(grants)
        tab == RootTab.About -> Screen.About(about)
        else -> Screen.Shelf(shelf)
    }

    BackHandler(enabled = pickerName != null || detailName != null) {
        if (pickerName != null) pickerName = null else detailName = null
    }

    DhevApp(
        screen = screen,
        tab = tab,
        onTab = { next ->
            tabName = next.name
            detailName = null
            pickerName = null
        },
        onUp = {
            if (pickerName != null) pickerName = null else detailName = null
        },
        snackbarHostState = snackbar,
        actions = ScreenActions(
            onQuery = { query = it },
            onClearQuery = { query = "" },
            onToggle = { tile, on -> applyToggle(tile, on) },
            onOpen = { tile ->
                if (tile.detail !is Detail.None) {
                    detailName = tile.name
                    pickerName = null
                } else {
                    applyToggle(tile, !device.isOn(tile))
                }
            },
            onRetry = { revision++ },
            onResetCounter = {
                device.resetCounter()
                revision++
            },
            onKeepMode = { mode ->
                device.setKeepMode(mode)
                revision++
            },
            onChooseApp = {
                val slot = (detailTile?.detail as? Detail.CustomApp)?.slot ?: return@ScreenActions
                pickerName = slot.name
            },
            onPickApp = { app ->
                val tile = detailTile ?: return@ScreenActions
                device.writeApp(tile, app)
                pickerName = null
                revision++
            },
            onPublish = {
                val tile = detailTile ?: return@ScreenActions
                applyToggle(tile, true)
            },
            onGrant = { need ->
                val command = device.grantCommand(need)
                if (command != null) {
                    device.copy(command)
                    scope.launch { snackbar.showSnackbar(copied) }
                } else {
                    device.openGrant(need)
                }
            },
        ),
    )
}
