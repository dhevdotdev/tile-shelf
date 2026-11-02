package com.tileshelf.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tileshelf.app.R
import com.tileshelf.app.ui.theme.spacing
import com.tileshelf.app.ui.theme.tabular

@Composable
fun DetailScreen(model: DetailModel, padding: PaddingValues, actions: ScreenActions) {
    when (model) {
        is DetailModel.Counter -> CounterDetail(model, padding, actions)
        is DetailModel.KeepScreen -> KeepDetail(model, padding, actions)
        is DetailModel.CustomEmpty -> Box(Modifier.fillMaxSize().padding(padding)) {
            EmptyPane(
                title = stringResource(R.string.custom_empty_title),
                body = stringResource(R.string.custom_empty_body),
                icon = Icons.Rounded.Refresh,
                action = stringResource(R.string.choose_an_app),
                onAction = actions.onChooseApp,
            )
        }
        is DetailModel.CustomReady -> CustomReadyDetail(model, padding, actions)
    }
}

@Composable
private fun CounterDetail(model: DetailModel.Counter, padding: PaddingValues, actions: ScreenActions) {
    val space = MaterialTheme.spacing
    val display = MaterialTheme.typography.displayLargeEmphasized
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = space.margin, vertical = space.space400),
    ) {
        Text(
            model.value.toString(),
            style = display.tabular.copy(
                fontSize = display.fontSize * 1.45f,
                lineHeight = display.lineHeight * 1.45f,
            ),
        )
        Text(
            stringResource(R.string.counter_times),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = space.space050),
        )
        Button(
            onClick = actions.onResetCounter,
            shapes = ButtonDefaults.shapesFor(ButtonDefaults.MediumContainerHeight),
            contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight),
            modifier = Modifier.padding(top = space.space500),
        ) {
            Text(
                stringResource(R.string.reset_counter),
                style = ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight),
            )
        }
    }
}

@Composable
private fun KeepDetail(model: DetailModel.KeepScreen, padding: PaddingValues, actions: ScreenActions) {
    val space = MaterialTheme.spacing
    LazyColumn(
        modifier = Modifier.fillMaxSize().selectableGroup(),
        contentPadding = PaddingValues(
            start = space.margin,
            end = space.margin,
            top = padding.calculateTopPadding() + space.space300,
            bottom = padding.calculateBottomPadding() + space.space600,
        ),
    ) {
        item {
            Text(
                stringResource(R.string.keep_screen_lead),
                style = MaterialTheme.typography.headlineSmallEmphasized,
            )
            Text(
                stringResource(R.string.on_which_connection),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = space.space100, bottom = space.space300),
            )
        }
        items(model.choices.size) { index ->
            val choice = model.choices[index]
            val selected = choice.mode == model.mode
            SegmentedListItem(
                selected = selected,
                onClick = { actions.onKeepMode(choice.mode) },
                shapes = ListItemDefaults.segmentedShapes(index, model.choices.size),
                colors = ListItemDefaults.segmentedColors(
                    containerColor = MaterialTheme.colorScheme.surfaceBright,
                ),
                leadingContent = { RadioButton(selected = selected, onClick = null) },
            ) {
                Text(
                    choice.label,
                    style = if (selected) {
                        MaterialTheme.typography.bodyLargeEmphasized
                    } else {
                        MaterialTheme.typography.bodyLarge
                    },
                )
            }
            if (index < model.choices.lastIndex) {
                Spacer(Modifier.height(ListItemDefaults.SegmentedGap))
            }
        }
    }
}

@Composable
private fun CustomReadyDetail(model: DetailModel.CustomReady, padding: PaddingValues, actions: ScreenActions) {
    val space = MaterialTheme.spacing
    val height = ButtonDefaults.MediumContainerHeight
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = space.margin, vertical = space.space400),
    ) {
        Text(model.label, style = MaterialTheme.typography.headlineLargeEmphasized)
        Text(
            model.packageName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = space.space100),
        )
        if (model.published) {
            Text(
                stringResource(R.string.published_on_shelf),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = space.space300),
            )
        } else {
            Button(
                onClick = actions.onPublish,
                shapes = ButtonDefaults.shapesFor(height),
                contentPadding = ButtonDefaults.contentPaddingFor(height),
                modifier = Modifier.padding(top = space.space400),
            ) {
                Text(stringResource(R.string.publish_tile), style = ButtonDefaults.textStyleFor(height))
            }
        }
        FilledTonalButton(
            onClick = actions.onChooseApp,
            shapes = ButtonDefaults.shapesFor(height),
            contentPadding = ButtonDefaults.contentPaddingFor(height),
            modifier = Modifier.padding(top = space.space150),
        ) {
            Text(stringResource(R.string.change_app), style = ButtonDefaults.textStyleFor(height))
        }
    }
}
