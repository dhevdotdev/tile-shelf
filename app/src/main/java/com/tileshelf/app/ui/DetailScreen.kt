package com.tileshelf.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.tileshelf.app.R
import com.tileshelf.app.ui.theme.spacing
import com.tileshelf.app.ui.theme.tabular

@Composable
fun DetailScreen(model: DetailModel, padding: PaddingValues, actions: ScreenActions) {
    var editingLink by rememberSaveable { mutableStateOf(false) }
    if (editingLink && (model is DetailModel.CustomEmpty || model is DetailModel.CustomReady)) {
        BackHandler { editingLink = false }
        val ready = model as? DetailModel.CustomReady
        LinkForm(
            initialLink = if (ready?.isLink == true) ready.subtitle else "",
            initialName = if (ready?.isLink == true) ready.label else "",
            padding = padding,
            onSave = actions.onSaveLink,
            onClose = { editingLink = false },
        )
        return
    }
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
                secondaryAction = stringResource(R.string.use_a_link),
                onSecondaryAction = { editingLink = true },
            )
        }
        is DetailModel.CustomReady -> CustomReadyDetail(model, padding, actions) { editingLink = true }
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
            onClick = withHaptic(HapticFeedbackType.Confirm, actions.onResetCounter),
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
                onClick = withHaptic(HapticFeedbackType.SegmentTick) { actions.onKeepMode(choice.mode) },
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
private fun CustomReadyDetail(
    model: DetailModel.CustomReady,
    padding: PaddingValues,
    actions: ScreenActions,
    onEditLink: () -> Unit,
) {
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
            model.subtitle,
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
                onClick = withHaptic(HapticFeedbackType.ToggleOn, actions.onPublish),
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
            Text(
                stringResource(if (model.isLink) R.string.choose_an_app else R.string.change_app),
                style = ButtonDefaults.textStyleFor(height),
            )
        }
        FilledTonalButton(
            onClick = onEditLink,
            shapes = ButtonDefaults.shapesFor(height),
            contentPadding = ButtonDefaults.contentPaddingFor(height),
            modifier = Modifier.padding(top = space.space150),
        ) {
            Text(
                stringResource(if (model.isLink) R.string.change_link else R.string.use_a_link),
                style = ButtonDefaults.textStyleFor(height),
            )
        }
    }
}

@Composable
private fun LinkForm(
    initialLink: String,
    initialName: String,
    padding: PaddingValues,
    onSave: (String, String) -> LinkSave,
    onClose: () -> Unit,
) {
    val space = MaterialTheme.spacing
    val height = ButtonDefaults.MediumContainerHeight
    var link by rememberSaveable { mutableStateOf(initialLink) }
    var name by rememberSaveable { mutableStateOf(initialName) }
    var problem by remember { mutableStateOf<LinkSave?>(null) }
    val save = withHaptic(HapticFeedbackType.Confirm) {
        when (val result = onSave(link, name)) {
            LinkSave.Saved -> onClose()
            else -> problem = result
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = space.margin, vertical = space.space400),
    ) {
        Text(stringResource(R.string.link_dialog_title), style = MaterialTheme.typography.headlineSmallEmphasized)
        OutlinedTextField(
            value = link,
            onValueChange = {
                link = it
                problem = null
            },
            label = { Text(stringResource(R.string.link_field)) },
            singleLine = true,
            isError = problem != null,
            supportingText = {
                Text(
                    stringResource(
                        when (problem) {
                            LinkSave.Invalid -> R.string.link_invalid
                            LinkSave.NoApp -> R.string.link_no_app
                            else -> R.string.link_hint
                        },
                    ),
                )
            },
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Next,
            ),
            modifier = Modifier.fillMaxWidth().padding(top = space.space300),
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.link_name_field)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (link.isNotBlank()) save() }),
            modifier = Modifier.fillMaxWidth().padding(top = space.space100),
        )
        Button(
            onClick = save,
            enabled = link.isNotBlank(),
            shapes = ButtonDefaults.shapesFor(height),
            contentPadding = ButtonDefaults.contentPaddingFor(height),
            modifier = Modifier.padding(top = space.space300),
        ) {
            Text(stringResource(R.string.save), style = ButtonDefaults.textStyleFor(height))
        }
        TextButton(onClick = onClose, modifier = Modifier.padding(top = space.space100)) {
            Text(stringResource(R.string.cancel))
        }
    }
}
