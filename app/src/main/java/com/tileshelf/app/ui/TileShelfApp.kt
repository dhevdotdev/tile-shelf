package com.tileshelf.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tileshelf.app.R

private data class Destination(val tab: RootTab, val icon: ImageVector, val label: Int)

private val MaxContentWidth = 720.dp

private val destinations = listOf(
    Destination(RootTab.Shelf, Icons.Rounded.Home, R.string.nav_shelf),
    Destination(RootTab.Grants, Icons.Rounded.Lock, R.string.nav_grants),
    Destination(RootTab.About, Icons.Rounded.Info, R.string.nav_about),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TileShelfApp(
    screen: Screen,
    tab: RootTab,
    onTab: (RootTab) -> Unit,
    onUp: () -> Unit,
    actions: ScreenActions,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val widthDp = LocalConfiguration.current.screenWidthDp
    val wide = widthDp >= 600
    val showUp = screen is Screen.Detail || screen is Screen.Picker
    val title = when (screen) {
        is Screen.Shelf -> stringResource(R.string.app_name)
        is Screen.Detail -> screen.model.title
        is Screen.Grants -> stringResource(R.string.nav_grants)
        is Screen.Picker -> stringResource(R.string.select_an_app)
        is Screen.About -> stringResource(R.string.nav_about)
    }
    Row(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer),
    ) {
        if (wide) {
            Rail(selected = if (showUp) null else tab, onTab = onTab, startExpanded = widthDp >= 840)
        }
        Scaffold(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        if (showUp) {
                            IconButton(onClick = onUp) {
                                Icon(
                                    Icons.Rounded.ArrowBack,
                                    contentDescription = stringResource(R.string.navigate_up),
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                )
            },
            bottomBar = {
                if (!wide) {
                    ShortNavigationBar {
                        destinations.forEach { destination ->
                            ShortNavigationBarItem(
                                selected = !showUp && tab == destination.tab,
                                onClick = { onTab(destination.tab) },
                                icon = { Icon(destination.icon, contentDescription = null) },
                                label = { Text(stringResource(destination.label)) },
                            )
                        }
                    }
                }
            },
        ) { padding ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                Box(Modifier.widthIn(max = MaxContentWidth).fillMaxSize()) {
                    when (screen) {
                        is Screen.Shelf -> ShelfScreen(screen.model, padding, actions)
                        is Screen.Detail -> DetailScreen(screen.model, padding, actions)
                        is Screen.Grants -> GrantsScreen(screen.model, padding, actions)
                        is Screen.Picker -> PickerScreen(screen.model, padding, actions)
                        is Screen.About -> AboutScreen(screen.model, padding, actions)
                    }
                }
            }
        }
    }
}

@Composable
private fun Rail(selected: RootTab?, onTab: (RootTab) -> Unit, startExpanded: Boolean) {
    val state = rememberWideNavigationRailState(
        if (startExpanded) WideNavigationRailValue.Expanded else WideNavigationRailValue.Collapsed,
    )
    val expanded = state.targetValue == WideNavigationRailValue.Expanded
    WideNavigationRail(state = state, modifier = Modifier.fillMaxHeight()) {
        destinations.forEach { destination ->
            WideNavigationRailItem(
                selected = selected == destination.tab,
                onClick = { onTab(destination.tab) },
                icon = { Icon(destination.icon, contentDescription = null) },
                label = { Text(stringResource(destination.label)) },
                railExpanded = expanded,
            )
        }
    }
}
