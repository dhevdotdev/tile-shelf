package com.tileshelf.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import com.tileshelf.app.ui.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingPane(label: String, modifier: Modifier = Modifier) {
    val space = MaterialTheme.spacing
    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        LoadingIndicator()
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = space.space200, start = space.margin, end = space.margin),
        )
    }
}

@Composable
fun ErrorPane(
    title: String,
    body: String,
    action: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val space = MaterialTheme.spacing
    val colors = MaterialTheme.colorScheme
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = space.margin, vertical = space.space400),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            color = colors.errorContainer,
            contentColor = colors.onErrorContainer,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(space.space300)) {
                Text(title, style = MaterialTheme.typography.headlineSmallEmphasized)
                Text(
                    body,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = space.space100),
                )
                Button(
                    onClick = onAction,
                    shapes = ButtonDefaults.shapesFor(ButtonDefaults.MediumContainerHeight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.error,
                        contentColor = colors.onError,
                    ),
                    contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight),
                    modifier = Modifier.padding(top = space.space300),
                ) {
                    Text(action, style = ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EmptyPane(
    title: String,
    body: String,
    icon: ImageVector,
    action: String? = null,
    onAction: () -> Unit = {},
    strongAction: Boolean = true,
    secondaryAction: String? = null,
    onSecondaryAction: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val space = MaterialTheme.spacing
    val colors = MaterialTheme.colorScheme
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = space.margin, vertical = space.space400),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ShapeCluster(icon)
        Text(
            title,
            style = MaterialTheme.typography.headlineLargeEmphasized,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = space.space400),
        )
        Text(
            body,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = space.space150),
        )
        if (action != null) {
            val height = ButtonDefaults.MediumContainerHeight
            if (strongAction) {
                Button(
                    onClick = onAction,
                    shapes = ButtonDefaults.shapesFor(height),
                    contentPadding = ButtonDefaults.contentPaddingFor(height),
                    modifier = Modifier.padding(top = space.space400),
                ) {
                    Text(action, style = ButtonDefaults.textStyleFor(height))
                }
            } else {
                FilledTonalButton(
                    onClick = onAction,
                    shapes = ButtonDefaults.shapesFor(height),
                    contentPadding = ButtonDefaults.contentPaddingFor(height),
                    modifier = Modifier.padding(top = space.space400),
                ) {
                    Text(action, style = ButtonDefaults.textStyleFor(height))
                }
            }
        }
        if (secondaryAction != null) {
            TextButton(onClick = onSecondaryAction, modifier = Modifier.padding(top = space.space100)) {
                Text(secondaryAction)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SmallEmptyMark(icon: ImageVector, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val space = MaterialTheme.spacing
    Box(modifier.size(space.space800 + space.space400), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(space.space800 + space.space200)
                .clip(MaterialShapes.Cookie9Sided.toShape())
                .background(colors.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = colors.onSecondaryContainer, modifier = Modifier.size(space.space500))
        }
        Box(
            Modifier
                .size(space.space300)
                .align(Alignment.TopEnd)
                .clip(MaterialShapes.Sunny.toShape())
                .background(colors.tertiaryFixedDim),
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ShapeCluster(icon: ImageVector) {
    val colors = MaterialTheme.colorScheme
    val space = MaterialTheme.spacing
    Box(Modifier.size(220.dp)) {
        Box(
            Modifier
                .size(168.dp)
                .align(Alignment.Center)
                .clip(MaterialShapes.Cookie9Sided.toShape())
                .background(colors.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = colors.onSecondaryContainer,
                modifier = Modifier.size(space.space800),
            )
        }
        Blob(
            MaterialShapes.Sunny,
            colors.tertiaryFixedDim,
            space.space900,
            Modifier.align(Alignment.TopEnd),
        )
        Blob(
            MaterialShapes.Circle,
            colors.primary,
            28.dp,
            Modifier.align(Alignment.BottomStart).offset(x = space.space300, y = -space.space250),
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Blob(shape: RoundedPolygon, color: Color, size: Dp, modifier: Modifier) {
    Box(modifier.size(size).clip(shape.toShape()).background(color))
}

@Composable
fun withHaptic(type: HapticFeedbackType, action: () -> Unit): () -> Unit {
    val haptic = LocalHapticFeedback.current
    return {
        haptic.performHapticFeedback(type)
        action()
    }
}
