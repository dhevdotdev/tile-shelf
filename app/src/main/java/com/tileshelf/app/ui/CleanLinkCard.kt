package com.tileshelf.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tileshelf.app.ClipCleanOutcome
import com.tileshelf.app.R
import com.tileshelf.app.ui.theme.spacing

private val CardMaxWidth = 480.dp
private val BadgeSize = 48.dp
private val BadgeIconSize = 24.dp

/**
 * The Clean link result, shown at the top of the screen where the tile's launch animation
 * lands. [showing] turns false after a short read or on a tap anywhere ([onHide]), and
 * [onDismiss] runs once the card has left.
 */
@Composable
fun CleanLinkCard(outcome: ClipCleanOutcome?, showing: Boolean, onHide: () -> Unit, onDismiss: () -> Unit) {
    val transition = updateTransition(outcome != null && showing, label = "cleanLinkCard")
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(outcome) {
        when (outcome) {
            is ClipCleanOutcome.Cleaned -> haptics.performHapticFeedback(HapticFeedbackType.Confirm)
            ClipCleanOutcome.Unreadable -> haptics.performHapticFeedback(HapticFeedbackType.Reject)
            else -> Unit
        }
    }
    if (outcome != null && !showing && !transition.currentState && !transition.isRunning) {
        LaunchedEffect(Unit) { onDismiss() }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onHide)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = MaterialTheme.spacing.margin, vertical = MaterialTheme.spacing.space100),
        contentAlignment = Alignment.TopCenter,
    ) {
        val motion = MaterialTheme.motionScheme
        transition.AnimatedVisibility(
            visible = { it },
            enter = slideInVertically(motion.fastSpatialSpec()) { -it / 2 } + fadeIn(motion.fastEffectsSpec()),
            exit = slideOutVertically(motion.fastSpatialSpec()) { -it / 2 } + fadeOut(motion.fastEffectsSpec()),
        ) {
            if (outcome != null) ResultCard(outcome)
        }
    }
}

val ClipCleanOutcome.link: String?
    get() = when (this) {
        is ClipCleanOutcome.Cleaned -> text
        is ClipCleanOutcome.AlreadyClean -> text
        else -> null
    }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ResultCard(outcome: ClipCleanOutcome) {
    val space = MaterialTheme.spacing
    val colors = MaterialTheme.colorScheme
    val (badge, onBadge) = when (outcome) {
        is ClipCleanOutcome.Cleaned -> colors.primaryContainer to colors.onPrimaryContainer
        is ClipCleanOutcome.AlreadyClean -> colors.secondaryContainer to colors.onSecondaryContainer
        ClipCleanOutcome.Unreadable -> colors.errorContainer to colors.onErrorContainer
        else -> colors.surfaceContainerHighest to colors.onSurfaceVariant
    }
    val icon = when (outcome) {
        is ClipCleanOutcome.Cleaned, is ClipCleanOutcome.AlreadyClean -> R.drawable.ic_link
        else -> R.drawable.ic_link_off
    }
    val title = when (outcome) {
        is ClipCleanOutcome.Cleaned -> stringResource(R.string.link_cleaned)
        is ClipCleanOutcome.AlreadyClean -> stringResource(R.string.link_already_clean)
        ClipCleanOutcome.NoLink -> stringResource(R.string.clipboard_has_no_link)
        ClipCleanOutcome.NothingCopied -> stringResource(R.string.clipboard_has_nothing)
        ClipCleanOutcome.Unreadable -> stringResource(R.string.clipboard_unreadable)
    }
    val detail = when (outcome) {
        is ClipCleanOutcome.Cleaned -> when {
            outcome.unwrapped > 0 && outcome.removed > 0 ->
                pluralStringResource(R.plurals.redirect_and_trackers_removed, outcome.removed, outcome.removed)
            outcome.unwrapped > 0 -> stringResource(R.string.redirect_removed)
            else -> pluralStringResource(R.plurals.trackers_removed, outcome.removed, outcome.removed)
        }
        ClipCleanOutcome.NoLink, ClipCleanOutcome.NothingCopied -> stringResource(R.string.clean_link_copy_hint)
        ClipCleanOutcome.Unreadable -> stringResource(R.string.clean_link_try_again)
        is ClipCleanOutcome.AlreadyClean -> null
    }
    Surface(
        modifier = Modifier.widthIn(max = CardMaxWidth).fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = colors.surfaceContainerHigh,
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier.padding(space.space200),
            horizontalArrangement = Arrangement.spacedBy(space.space150),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(BadgeSize),
                shape = MaterialShapes.Cookie9Sided.toShape(),
                color = badge,
                contentColor = onBadge,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(BadgeIconSize))
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(space.space025)) {
                Text(title, style = MaterialTheme.typography.titleLargeEmphasized, color = colors.onSurface)
                outcome.link?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                detail?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (outcome is ClipCleanOutcome.Cleaned) colors.primary else colors.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
