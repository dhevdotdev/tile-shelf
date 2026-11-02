package com.tileshelf.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import com.tileshelf.app.R
import com.tileshelf.app.ui.theme.spacing

@Composable
fun PickerScreen(model: PickerModel, padding: PaddingValues, actions: ScreenActions) {
    val space = MaterialTheme.spacing
    val note = stringResource(R.string.system_apps_included)
    when (model) {
        PickerModel.Loading -> Box(Modifier.fillMaxSize().padding(padding)) {
            ColumnNote(note) { LoadingPane(stringResource(R.string.reading_apps)) }
        }
        PickerModel.Failed -> Box(Modifier.fillMaxSize().padding(padding)) {
            ColumnNote(note) {
                ErrorPane(
                    title = stringResource(R.string.apps_unreadable),
                    body = stringResource(R.string.apps_unreadable_body),
                    action = stringResource(R.string.try_again),
                    onAction = actions.onRetry,
                )
            }
        }
        PickerModel.Empty -> Box(Modifier.fillMaxSize().padding(padding)) {
            ColumnNote(note) {
                EmptyPane(
                    title = stringResource(R.string.no_launcher_apps),
                    body = stringResource(R.string.no_launcher_apps_body),
                    icon = Icons.Rounded.Search,
                    action = stringResource(R.string.try_again),
                    onAction = actions.onRetry,
                    strongAction = false,
                )
            }
        }
        is PickerModel.Ready -> PickerReady(model, note, padding, actions)
    }
}

@Composable
private fun ColumnNote(note: String, content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Column(Modifier.fillMaxSize()) {
        Text(
            note,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                start = MaterialTheme.spacing.margin,
                end = MaterialTheme.spacing.margin,
                top = MaterialTheme.spacing.space200,
            ),
        )
        Box(Modifier.weight(1f)) { content() }
    }
}

@Composable
private fun PickerReady(
    model: PickerModel.Ready,
    note: String,
    padding: PaddingValues,
    actions: ScreenActions,
) {
    val space = MaterialTheme.spacing
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = space.margin,
            end = space.margin,
            top = padding.calculateTopPadding() + space.space200,
            bottom = padding.calculateBottomPadding() + space.space600,
        ),
    ) {
        item {
            Text(
                note,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = space.space300),
            )
        }
        model.apps.forEachIndexed { index, app ->
            item(key = app.packageName) {
                SegmentedListItem(
                    onClick = withHaptic(HapticFeedbackType.Confirm) { actions.onPickApp(app) },
                    shapes = ListItemDefaults.segmentedShapes(index, model.apps.size),
                    colors = ListItemDefaults.segmentedColors(
                        containerColor = MaterialTheme.colorScheme.surfaceBright,
                    ),
                    leadingContent = {
                        Box(
                            Modifier
                                .size(space.space600)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                app.label.take(1).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    },
                    supportingContent = { Text(app.packageName) },
                ) {
                    Text(app.label, style = MaterialTheme.typography.bodyLargeEmphasized)
                }
                if (index < model.apps.lastIndex) {
                    Spacer(Modifier.height(ListItemDefaults.SegmentedGap))
                }
            }
        }
    }
}
