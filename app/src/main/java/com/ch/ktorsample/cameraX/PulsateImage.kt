package com.ch.ktorsample.cameraX

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.ch.ktorsample.R

@Composable
fun PulsateImage() {
    // --- pulsate animation (only used when boundingBoxPx == null) ---
    val infiniteTransition = rememberInfiniteTransition()
    val pulsate by infiniteTransition.animateFloat(
        initialValue = 1.3f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_qr_code_frame),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Green),
            modifier = Modifier.scale(pulsate)
        )
    }
}