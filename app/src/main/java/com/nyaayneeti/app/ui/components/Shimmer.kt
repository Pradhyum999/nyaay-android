package com.nyaayneeti.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.ui.theme.ObsidianSurface
import com.nyaayneeti.app.ui.theme.ObsidianSurfaceElevated

/** Animated shimmer gradient (skeleton-loading) brush. Lightweight, one infinite transition. */
@Composable
fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val x by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(tween(1300, easing = LinearEasing)),
        label = "shimmerX"
    )
    return Brush.linearGradient(
        colors = listOf(ObsidianSurface, ObsidianSurfaceElevated, ObsidianSurface),
        start = Offset(x - 500f, 0f),
        end = Offset(x, 0f)
    )
}

/** A skeleton placeholder box with a moving shimmer. */
@Composable
fun ShimmerBox(modifier: Modifier = Modifier, corner: Int = 8) {
    Box(modifier.background(shimmerBrush(), RoundedCornerShape(corner.dp)))
}
