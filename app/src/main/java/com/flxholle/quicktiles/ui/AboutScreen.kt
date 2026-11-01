package com.flxholle.quicktiles.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flxholle.quicktiles.R
import com.flxholle.quicktiles.ui.theme.spacing

@Composable
fun AboutScreen(model: AboutModel, padding: PaddingValues, actions: ScreenActions) {
    when (model) {
        AboutModel.Loading -> Box(Modifier.fillMaxSize().padding(padding)) {
            LoadingPane(stringResource(R.string.reading_about))
        }
        AboutModel.Failed -> Box(Modifier.fillMaxSize().padding(padding)) {
            ErrorPane(
                title = stringResource(R.string.about_unreadable),
                body = stringResource(R.string.about_unreadable_body),
                action = stringResource(R.string.try_again),
                onAction = actions.onRetry,
            )
        }
        is AboutModel.Ready -> AboutReady(model, padding)
    }
}

@Composable
private fun AboutReady(model: AboutModel.Ready, padding: PaddingValues) {
    val space = MaterialTheme.spacing
    val credits = listOf(
        stringResource(R.string.credit_dev_tiles) to stringResource(R.string.credit_apache),
        stringResource(R.string.credit_dev_quicksettings) to stringResource(R.string.credit_apache),
        stringResource(R.string.credit_adaptive_brightness) to stringResource(R.string.credit_gplv3),
        stringResource(R.string.credit_power_button) to stringResource(R.string.credit_gplv3),
        stringResource(R.string.credit_greyscale) to stringResource(R.string.credit_apache),
        stringResource(R.string.credit_icon_freepik) to stringResource(R.string.credit_flaticon),
        stringResource(R.string.credit_icon_smashicons) to stringResource(R.string.credit_flaticon),
    )
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
            Text(
                stringResource(R.string.about_fork_title),
                style = MaterialTheme.typography.headlineLargeEmphasized,
            )
            Text(
                stringResource(R.string.about_fork_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = space.space150),
            )
            Text(
                stringResource(R.string.about_version, model.versionName),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = space.space200, bottom = space.space300),
            )
            Text(
                stringResource(R.string.credits),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = space.space100),
            )
        }
        credits.forEachIndexed { index, (name, license) ->
            item(key = name) {
                SegmentedListItem(
                    shapes = ListItemDefaults.segmentedShapes(index, credits.size),
                    colors = ListItemDefaults.segmentedColors(
                        containerColor = MaterialTheme.colorScheme.surfaceBright,
                    ),
                    supportingContent = { Text(license) },
                ) {
                    Text(name, style = MaterialTheme.typography.bodyLarge)
                }
                if (index < credits.lastIndex) {
                    Spacer(Modifier.height(ListItemDefaults.SegmentedGap))
                }
            }
        }
    }
}
