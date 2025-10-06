package com.ch.ktorsample.frameanimate

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.ch.ktorsample.R

enum class ImageState { Idle, Detected }

@Composable
fun PulsatingImageToRect(
    targetRect: Rect?,  // null = no QR code
    modifier: Modifier = Modifier,
    imageRes: Int = R.drawable.img_qr_code_frame
) {
    val density = LocalDensity.current
    val parentSize = remember { mutableStateOf(IntSize.Zero) }

    // Define states
    val state = if (targetRect == null) ImageState.Idle else ImageState.Detected
    val transition = updateTransition(targetState = state, label = "imageTransition")

    // Center position when idle
    val centerSize = 150.dp
    val centerX = { ((parentSize.value.width / density.density) / 2).dp - centerSize / 2 }
    val centerY = { ((parentSize.value.height / density.density) / 2).dp - centerSize / 2 }

    // Animate position + size
    val x by transition.animateDp(
        transitionSpec = { tween(500, easing = FastOutSlowInEasing) },
        label = "x"
    ) { s ->
        if (s == ImageState.Detected) with(density) { targetRect!!.left.toDp() } else centerX()
    }

    val y by transition.animateDp(
        transitionSpec = { tween(500, easing = FastOutSlowInEasing) },
        label = "y"
    ) { s ->
        if (s == ImageState.Detected) with(density) { targetRect!!.top.toDp() } else centerY()
    }

    val w by transition.animateDp(
        transitionSpec = { tween(500, easing = FastOutSlowInEasing) },
        label = "w"
    ) { s ->
        if (s == ImageState.Detected) with(density) { targetRect?.width?.toDp() as Dp } else centerSize
    }

    val h by transition.animateDp(
        transitionSpec = { tween(500, easing = FastOutSlowInEasing) },
        label = "h"
    ) { s ->
        if (s == ImageState.Detected) with(density) { targetRect?.height?.toDp() as Dp } else centerSize
    }

    // Animate scale: pulsating when Idle, 1f when Detected
    val infiniteTransition = rememberInfiniteTransition(label = "pulsate")
    val pulsate by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulsate"
    )
    val scale by transition.animateFloat(
        transitionSpec = { tween(300) },
        label = "scale"
    ) { s -> if (s == ImageState.Detected) 1f else pulsate }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { parentSize.value = it.size }
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "QR Frame",
            colorFilter = ColorFilter.tint(Color.Red),
            modifier = Modifier
                .absoluteOffset(x = x, y = y)
                .size(w, h)
                .scale(scale)
        )
    }
}
