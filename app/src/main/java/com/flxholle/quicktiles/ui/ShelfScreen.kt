package com.flxholle.quicktiles.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.flxholle.quicktiles.R
import com.flxholle.quicktiles.catalog.RowSwitch
import com.flxholle.quicktiles.ui.theme.spacing
import com.flxholle.quicktiles.ui.theme.tabular

@Composable
fun ShelfScreen(
    model: ShelfModel,
    padding: PaddingValues,
    actions: ScreenActions,
) {
    when (model) {
        ShelfModel.Loading -> Box(Modifier.fillMaxSize().padding(padding)) {
            LoadingPane(stringResource(R.string.reading_tiles))
        }
        ShelfModel.Failed -> Box(Modifier.fillMaxSize().padding(padding)) {
            ErrorPane(
                title = stringResource(R.string.tiles_unreadable),
                body = stringResource(R.string.tiles_unreadable_body),
                action = stringResource(R.string.try_again),
                onAction = actions.onRetry,
            )
        }
        is ShelfModel.Ready -> ReadyShelf(model, padding, actions)
    }
}

@Composable
private fun ReadyShelf(model: ShelfModel.Ready, padding: PaddingValues, actions: ScreenActions) {
    val space = MaterialTheme.spacing
    val haptic = LocalHapticFeedback.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = space.margin,
            end = space.margin,
            top = padding.calculateTopPadding() + space.space400,
            bottom = padding.calculateBottomPadding() + space.space600,
        ),
    ) {
        item {
            PublishedHero(model.publishedCount, model.catalogSize)
        }
        item {
            OutlinedTextField(
                value = model.query,
                onValueChange = actions.onQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = space.space400),
                textStyle = MaterialTheme.typography.bodyLarge,
                placeholder = {
                    Text(
                        stringResource(R.string.search_tiles),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                trailingIcon = if (model.query.isNotEmpty()) {
                    {
                        IconButton(onClick = actions.onClearQuery) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.clear_search),
                            )
                        }
                    }
                } else {
                    null
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                shape = MaterialTheme.shapes.extraLarge,
            )
        }
        if (model.query.isNotBlank() && model.groups.isEmpty()) {
            item {
                Column(Modifier.fillMaxWidth().padding(top = space.space300)) {
                    SmallEmptyMark(Icons.Rounded.Search)
                    Text(
                        stringResource(R.string.no_matching_tiles),
                        style = MaterialTheme.typography.headlineSmallEmphasized,
                        modifier = Modifier.padding(top = space.space200),
                    )
                    Text(
                        stringResource(R.string.no_matching_tiles_body),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = space.space100),
                    )
                    FilledTonalButton(
                        onClick = actions.onClearQuery,
                        shapes = ButtonDefaults.shapesFor(ButtonDefaults.MediumContainerHeight),
                        contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight),
                        modifier = Modifier.padding(top = space.space300),
                    ) {
                        Text(
                            stringResource(R.string.clear_search),
                            style = ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight),
                        )
                    }
                }
            }
        }
        model.groups.forEach { group ->
            item {
                Text(
                    group.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = space.space300, bottom = space.space100),
                )
            }
            group.rows.forEachIndexed { index, row ->
                item(key = row.tile.name) {
                    val checked = when (val rowSwitch = row.switch) {
                        is RowSwitch.Offered -> rowSwitch.published
                        RowSwitch.TurnOffOnly -> true
                    }
                    SegmentedListItem(
                        onClick = { actions.onOpen(row.tile) },
                        shapes = ListItemDefaults.segmentedShapes(index = index, count = group.rows.size),
                        colors = ListItemDefaults.segmentedColors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright,
                        ),
                        leadingContent = {
                            Box(
                                Modifier
                                    .size(space.space500)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painterResource(row.iconRes),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(space.space300),
                                )
                            }
                        },
                        supportingContent = row.supporting?.let { note -> { Text(note) } },
                        trailingContent = {
                            Switch(
                                checked = checked,
                                onCheckedChange = { want ->
                                    if (row.switch is RowSwitch.TurnOffOnly && want) return@Switch
                                    haptic.performHapticFeedback(
                                        if (want) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff,
                                    )
                                    actions.onToggle(row.tile, want)
                                },
                            )
                        },
                    ) {
                        Text(
                            row.title,
                            style = if (checked) {
                                MaterialTheme.typography.bodyLargeEmphasized
                            } else {
                                MaterialTheme.typography.bodyLarge
                            },
                        )
                    }
                    if (index < group.rows.lastIndex) {
                        Spacer(Modifier.height(ListItemDefaults.SegmentedGap))
                    }
                }
            }
        }
    }
}

@Composable
private fun PublishedHero(published: Int, catalogSize: Int) {
    val type = MaterialTheme.typography
    val space = MaterialTheme.spacing
    val display = type.displayLargeEmphasized
    val fraction = if (catalogSize == 0) 0f else published.toFloat() / catalogSize.toFloat()
    Column {
        Text(
            published.toString(),
            style = display.tabular.copy(
                fontSize = display.fontSize * 1.45f,
                lineHeight = display.lineHeight * 1.45f,
            ),
        )
        Text(
            stringResource(R.string.published_label),
            style = type.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = space.space050),
        )
        LinearWavyProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = space.space250),
        )
        Text(
            stringResource(R.string.published_of, catalogSize),
            style = type.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = space.space100),
        )
    }
}
