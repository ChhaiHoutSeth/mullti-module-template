package com.ch.ktorsample.frameanimate

import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import android.util.Size
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.animateRectAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.ch.ktorsample.R

@Composable
fun PulsatingImageToRect(
    boundingBoxPx: Rect?, // bounding box in pixels (e.g. barcode.boundingBox)
    qrCodeBitmap: ImageBitmap?,
    previewSizePx: Size? = null,        // optional: size of camera preview (w,h) in px if boundingBox is in preview coords
    centerSizeDp: Dp = 350.dp
) {
    val density = LocalDensity.current
    val parentPx = remember { mutableStateOf(IntSize.Zero) }

    val infiniteTransition = rememberInfiniteTransition()
    val pulsate by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // --- map bounding box from preview coords -> parent coords (px) if previewSizePx is provided ---
    val mappedRectPx: RectF? = remember(boundingBoxPx, previewSizePx, parentPx.value) {
        if (boundingBoxPx == null) return@remember null

        // If parent size is not known yet, return null (we can't map)
        if (parentPx.value.width == 0 || parentPx.value.height == 0) return@remember null

        if (previewSizePx == null) {
            // assume boundingBox already in the same coordinate space as parent (view) -> use as-is
            RectF(boundingBoxPx)
        } else {
            // scale from preview -> parent
            val scaleX = parentPx.value.width.toFloat() / previewSizePx.width.toFloat()
            val scaleY = parentPx.value.height.toFloat() / previewSizePx.height.toFloat()

            // If your preview is rotated or mirrored, you'll need to transform coordinates here.
            val left = boundingBoxPx.left * scaleX
            val top = boundingBoxPx.top * scaleY
            val right = boundingBoxPx.right * scaleX
            val bottom = boundingBoxPx.bottom * scaleY
            RectF(left, top, right, bottom)
        }
    }

    // --- target top-left (dp) and size (dp) ---
    val parentWidthDp = (parentPx.value.width.toFloat() / density.density).dp
    val parentHeightDp = (parentPx.value.height.toFloat() / density.density).dp

    val centerXdp = ((parentWidthDp - centerSizeDp) / 2f).coerceAtLeast(0.dp)
    val centerYdp = ((parentHeightDp - centerSizeDp) / 2f).coerceAtLeast(0.dp)


    val targetXdp = boundingBoxPx?.left?.let { (it / density.density).dp } ?: centerXdp
    val targetYdp = boundingBoxPx?.top?.let { (it / density.density).dp } ?: centerYdp
    val targetWdp = boundingBoxPx?.width()?.let { ((it / density.density)).dp } ?: centerSizeDp
    val targetHdp = boundingBoxPx?.height()?.let { ((it / density.density)).dp } ?: centerSizeDp

   /* val targetXdp = mappedRectPx?.left?.let { (it / density.density).dp } ?: centerXdp
    val targetYdp = mappedRectPx?.top?.let { (it / density.density).dp } ?: centerYdp
    val targetWdp = mappedRectPx?.width()?.let { ((it / density.density) + 15f).dp } ?: centerSizeDp
    val targetHdp = mappedRectPx?.height()?.let { ((it / density.density) + 15f).dp } ?: centerSizeDp*/

    val animSpec = tween<Dp>(durationMillis = 150, easing = FastOutSlowInEasing)
    val animatedX by animateDpAsState(targetXdp, animationSpec = animSpec)
    val animatedY by animateDpAsState(targetYdp, animationSpec = animSpec)
    val animatedW by animateDpAsState(targetWdp, animationSpec = animSpec)
    val animatedH by animateDpAsState(targetHdp, animationSpec = animSpec)

    val scaleFactor = if (mappedRectPx == null) pulsate else 1f


    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                parentPx.value = size
            }
    ) {
        val painter = if (boundingBoxPx == null) {
            painterResource(id = R.drawable.img_qr_code_frame)
        } else if (qrCodeBitmap != null) {
            BitmapPainter(qrCodeBitmap)
        } else {
            painterResource(id = R.drawable.img_qr_code_frame_blue)
        }
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .offset(x = animatedX, y = animatedY)
                .size(animatedW, animatedH)
                .scale(scaleFactor)
        )
    }

}

fun Rect.expand(paddingPx: Int): Rect {
    return Rect(
        left - paddingPx,
        top - paddingPx,
        right + paddingPx,
        bottom + paddingPx
    )
}
