package com.ch.ktorsample

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.unit.dp

@Composable
fun ScanningFrameOverlay(isQRCodeDetected: Boolean) {
    // Scanning line animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanning")
    val scanLineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanLine"
    )

    // Pulse animation for corners
    val cornerPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cornerPulse"
    )

    // Color animation when QR detected
    val borderColor by animateColorAsState(
        targetValue = if (isQRCodeDetected) Color.Green else Color.White,
        animationSpec = tween(300),
        label = "borderColor"
    )

    val cornerColor by animateColorAsState(
        targetValue = if (isQRCodeDetected) Color.Green else Color(0xFF00E5FF),
        animationSpec = tween(300),
        label = "cornerColor"
    )

    // Scale animation when detected
    val frameScale by animateFloatAsState(
        targetValue = if (isQRCodeDetected) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "frameScale"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val baseFrameSize = size.minDimension * 0.7f
        val frameSize = baseFrameSize * frameScale
        val frameOffset = Offset(
            (size.width - frameSize) / 2,
            (size.height - frameSize) / 2
        )

        // Draw semi-transparent overlay
        /*drawRect(
            color = Color.Black.copy(alpha = 0.5f),
            size = size
        )*/

        // Draw scanning frame (transparent area)
        /*drawRoundRect(
            color = Color.Transparent,
            topLeft = frameOffset,
            size = Size(frameSize, frameSize),
            cornerRadius = CornerRadius(20f)
        )*/

        // Draw frame border
//        drawRoundRect(
//            color = borderColor,
//            topLeft = frameOffset,
//            size = Size(frameSize, frameSize),
//            cornerRadius = CornerRadius(20f),
//            style = Stroke(width = 8f)
//        )

        // Draw animated scanning line (only when not detected)
        /*if (!isQRCodeDetected) {
            val lineY = frameOffset.y + (frameSize * scanLineOffset)
            drawLine(
                color = Color(0xFF00E5FF),
                start = Offset(frameOffset.x, lineY),
                end = Offset(frameOffset.x + frameSize, lineY),
                strokeWidth = 3f
            )

            // Scanning line glow effect
            drawLine(
                color = Color(0xFF00E5FF).copy(alpha = 0.3f),
                start = Offset(frameOffset.x, lineY),
                end = Offset(frameOffset.x + frameSize, lineY),
                strokeWidth = 10f
            )
        }*/

        // Draw animated corner indicators
        val cornerLength = 100f * if (isQRCodeDetected) 1f else cornerPulse
        val cornerStrokeWidth = 16f

        val corners = listOf(
            // Top-left
            Pair(frameOffset, frameOffset + Offset(cornerLength, 0f)),
            Pair(frameOffset, frameOffset + Offset(0f, cornerLength)),
            // Top-right
            Pair(
                frameOffset + Offset(frameSize, 0f),
                frameOffset + Offset(frameSize - cornerLength, 0f)
            ),
            Pair(
                frameOffset + Offset(frameSize, 0f),
                frameOffset + Offset(frameSize, cornerLength)
            ),
            // Bottom-left
            Pair(
                frameOffset + Offset(0f, frameSize),
                frameOffset + Offset(cornerLength , frameSize)
            ),
            Pair(
                frameOffset + Offset(0f, frameSize),
                frameOffset + Offset(0f, frameSize - cornerLength)
            ),
            // Bottom-right
            Pair(
                frameOffset + Offset(frameSize, frameSize),
                frameOffset + Offset(frameSize - cornerLength, frameSize)
            ),
            Pair(
                frameOffset + Offset(frameSize, frameSize),
                frameOffset + Offset(frameSize, frameSize - cornerLength)
            )
        )

        val strokeWidth = 8.dp.toPx()
        val cornerRadius = 20.dp.toPx()

        val path = Path().apply {
            // Start from left
//            moveTo(900f, 300f)

            // Draw horizontal line to the corner
            lineTo(size.width - 50f, 150f)

            // Draw rounded corner using arcTo
           /* arcTo(
                rect = Rect(
                    left = size.width - cornerRadius * 2 - 50f,
                    top = 300f,
                    right = size.width - 50f,
                    bottom = 300f + cornerRadius * 2
                ),
                startAngleDegrees = -90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = true
            )*/

            // Draw vertical line down
//            lineTo(size.width - 50f, 300f)
        }

        drawPath(
            path = path,
            color = Color(0xFF7BBE45),
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Square,
                join = StrokeJoin.Round
            )
        )

        corners.forEach { (start, end) ->
            drawLine(
                color = cornerColor,
                start = start,
                end = end,
                strokeWidth = 25f,
                cap = StrokeCap.Round
            )
        }
    }
}