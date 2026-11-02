package com.tileshelf.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.tileshelf.app.R
import com.tileshelf.app.catalog.GrantNeed
import com.tileshelf.app.ui.theme.spacing

@Composable
fun GrantsScreen(model: GrantsModel, padding: PaddingValues, actions: ScreenActions) {
    when (model) {
        is GrantsModel.Missing -> MissingGrants(model, padding, actions)
        is GrantsModel.Clear -> Box(Modifier.fillMaxSize().padding(padding)) {
            EmptyPane(
                title = stringResource(R.string.grants_clear),
                body = stringResource(R.string.grants_clear_body, model.tileTitle),
                icon = Icons.Rounded.Check,
            )
        }
        GrantsModel.Unknown -> Box(Modifier.fillMaxSize().padding(padding)) {
            EmptyPane(
                title = stringResource(R.string.unknown_tile),
                body = stringResource(R.string.unknown_tile_body),
                icon = Icons.Rounded.Warning,
            )
        }
    }
}

@Composable
private fun MissingGrants(model: GrantsModel.Missing, padding: PaddingValues, actions: ScreenActions) {
    val space = MaterialTheme.spacing
    val height = ButtonDefaults.MediumContainerHeight
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = space.margin,
            end = space.margin,
            top = padding.calculateTopPadding() + space.space300,
            bottom = padding.calculateBottomPadding() + space.space600,
        ),
    ) {
        item {
            Text(model.tileTitle, style = MaterialTheme.typography.headlineLargeEmphasized)
            Text(
                pluralStringResource(R.plurals.steps_missing, model.steps.size, model.steps.size),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = space.space100, bottom = space.space300),
            )
        }
        model.steps.forEachIndexed { index, step ->
            item(key = step.need.name) {
                SegmentedListItem(
                    onClick = { actions.onGrant(step.need) },
                    shapes = ListItemDefaults.segmentedShapes(index, model.steps.size),
                    colors = ListItemDefaults.segmentedColors(
                        containerColor = MaterialTheme.colorScheme.surfaceBright,
                    ),
                    supportingContent = { Text(step.body) },
                ) {
                    Text(step.title, style = MaterialTheme.typography.titleMedium)
                }
                if (index < model.steps.lastIndex) {
                    Spacer(Modifier.height(ListItemDefaults.SegmentedGap))
                }
            }
        }
        item {
            Column(Modifier.padding(top = space.space300)) {
                model.steps.forEachIndexed { index, step ->
                    val label = stringResource(actionLabel(step.need), step.title)
                    if (index == 0) {
                        Button(
                            onClick = { actions.onGrant(step.need) },
                            shapes = ButtonDefaults.shapesFor(height),
                            contentPadding = ButtonDefaults.contentPaddingFor(height),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(label, style = ButtonDefaults.textStyleFor(height))
                        }
                    } else {
                        FilledTonalButton(
                            onClick = { actions.onGrant(step.need) },
                            shapes = ButtonDefaults.shapesFor(height),
                            contentPadding = ButtonDefaults.contentPaddingFor(height),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = space.space150),
                        ) {
                            Text(label, style = ButtonDefaults.textStyleFor(height))
                        }
                    }
                }
            }
        }
    }
}

private fun actionLabel(need: GrantNeed): Int = when (need) {
    GrantNeed.WriteSecure, GrantNeed.Dump -> R.string.grant_action_copy
    GrantNeed.ModifySystem, GrantNeed.NotificationPolicy, GrantNeed.Accessibility -> R.string.grant_action_open
}
