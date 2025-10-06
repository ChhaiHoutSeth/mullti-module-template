package com.ch.ktorsample

import android.Manifest
import android.content.Context
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.ch.core_ui.MyApplicationTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    QRCodeScanners()
                    /*var isFound by remember { mutableStateOf<Boolean>(false) }
                    var qrBox by remember { mutableStateOf<RectF?>(null) }
                    val previewViewRef = remember { mutableStateOf<PreviewView?>(null) }
                    QRZoomScannerScreen { value, qrBoundingBox, cam, provider, previewView ->
                        if (value.equals("hello", true) && !isFound){
                            isFound = true
                            qrBox = qrBoundingBox
                            previewViewRef.value = previewView
                        }
                        *//*lifecycleScope.launch {
                            cam.let { cam ->
                                *//**//*val zoomState = cam.cameraInfo.zoomState.value
                                val maxZoom = zoomState?.maxZoomRatio ?: 4f
                                val currentBoxW = Animatable(rectF.width()).value

                                val suggested =
                                    if (currentBoxW > 0) (1 * 0.6f / currentBoxW) else 1f
                                val targetRatio = suggested.coerceIn(1f, maxZoom)*//**//*

                                // Compute target zoom ratio so QR fills ~60% of the screen width
                                val previewW = previewView.width.toFloat()
                                val boxW = qrBoundingBox.width()
                                val zoomState = cam.cameraInfo.zoomState.value
                                val maxZoom = zoomState?.maxZoomRatio ?: 4f

                                val desiredBoxW = previewW * 0.6f
                                val suggestedRatio = if (boxW > 0) desiredBoxW / boxW else 1f
                                val targetRatio = suggestedRatio.coerceIn(1f, maxZoom)

                                val targetLinearZoom = zoomState?.let { zoom ->
                                    (targetRatio - zoom.minZoomRatio) / (zoom.maxZoomRatio - zoom.minZoomRatio)
                                } ?: 0f

                                if (value.equals("hello", true) && !isFound) {
                                    isFound = true
                                    val steps = 14
                                    for (i in 1..steps) {
                                        val linear = (i / steps.toFloat()) * targetLinearZoom
                                        Log.d("Chhaihout", "linear: $linear")
                                        try {
                                            cam.cameraControl.setLinearZoom(0.8f)
                                        } catch (_: Exception) {
                                        }
                                        delay(30)
                                    }
                                    Toast.makeText(
                                        this@MainActivity,
                                        "hello",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    *//**//*try {
                                        cam.cameraControl.setZoomRatio(targetRatio)
                                        delay(10000)
                                        Toast.makeText(
                                            this@MainActivity,
                                            "hello",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        provider.unbindAll()
                                    } catch (_: Exception) {
                                    }*//**//*
                                }

                            }
                        }*//*
                    }
                    DrawBox(qrBox, previewViewRef.value, isFound)*/
                }
            }
        }
    }
}

/*@OptIn(ExperimentalGetImage::class)
@Composable
fun QRCodeScanner() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var detectedQRCode by remember { mutableStateOf<String?>(null) }
    var qrCodeBounds by remember { mutableStateOf<Rect?>(null) }
    var zoomRatio by remember { mutableStateOf(1f) }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                val barcodeScanner = BarcodeScanning.getClient()

                imageAnalysis.setAnalyzer(executor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )

                        barcodeScanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    if (barcode.valueType == Barcode.TYPE_TEXT ||
                                        barcode.valueType == Barcode.TYPE_URL) {
                                        detectedQRCode = barcode.rawValue
                                        qrCodeBounds = barcode.boundingBox

                                        // Auto zoom when QR code detected
                                        if (zoomRatio < 2f) {
                                            zoomRatio = 2f
                                        }
                                    }
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )

                    // Apply zoom
                    camera.cameraControl.setLinearZoom(
                        (zoomRatio - 1f) / (camera.cameraInfo.zoomState.value?.maxZoomRatio ?: 1f)
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Scanning frame overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val frameSize = size.minDimension * 0.7f
            val frameOffset = Offset(
                (size.width - frameSize) / 2,
                (size.height - frameSize) / 2
            )

            // Draw semi-transparent overlay
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                size = size
            )

            // Draw scanning frame
            drawRoundRect(
                color = Color.Transparent,
                topLeft = frameOffset,
                size = Size(frameSize, frameSize),
                cornerRadius = CornerRadius(16f)
            )

            // Draw frame border
            drawRoundRect(
                color = if (detectedQRCode != null) Color.Green else Color.White,
                topLeft = frameOffset,
                size = Size(frameSize, frameSize),
                cornerRadius = CornerRadius(16f),
                style = Stroke(width = 4f)
            )

            // Draw corner indicators
            val cornerLength = 40f
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
                    frameOffset + Offset(cornerLength, frameSize)
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

            corners.forEach { (start, end) ->
                drawLine(
                    color = if (detectedQRCode != null) Color.Green else Color.White,
                    start = start,
                    end = end,
                    strokeWidth = 8f
                )
            }
        }

        // Display detected QR code
        detectedQRCode?.let { qrCode ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "QR Code Detected!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = qrCode,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Instructions
        if (detectedQRCode == null) {
            Text(
                text = "Position QR code within the frame",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}*/

/*@Composable
fun QRZoomScannerScreen(onMatch: (String, rectF: RectF, cam: Camera, provider: ProcessCameraProvider, preview: PreviewView) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Permission
    var permissionGranted by remember { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            permissionGranted = granted
        }
    LaunchedEffect(Unit) { launcher.launch(Manifest.permission.CAMERA) }

    // previewView ref & camera instance
    val previewViewRef = remember { mutableStateOf<PreviewView?>(null) }
    var cameraRef by remember { mutableStateOf<Camera?>(null) }

    // detected barcode rect in preview coordinates (px)
    var detectedRectPx by remember { mutableStateOf<RectF?>(null) }
    var lastValue by remember { mutableStateOf<String?>(null) }
    var previewSizePx by remember { mutableStateOf(IntSize(0, 0)) }

    val density = LocalDensity.current
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var triggerEffect by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        if (!permissionGranted) {
            // friendly message while permission is requested
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Camera permission required")
            }
            return@Box
        }

        // Camera preview via AndroidView (PreviewView)
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    previewViewRef.value = this

                    // start camera after view is laid out
                    post {
                        startCamera(context, lifecycleOwner, this) { value, rectF, cam, provider ->
                            Handler(Looper.getMainLooper()).post {
                                detectedRectPx = rectF
                                cameraRef = cam
                                onMatch(value, rectF, cam, provider, this)
                            }
                        }
                    }
                }
            }, modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coords ->
                    previewSizePx = coords.size
                }
        )

        // Reset button / small UI to allow testing
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = {
                // reset zoom & overlay
                detectedRectPx = null
                lastValue = null
                cameraRef?.cameraControl?.setLinearZoom(0f)
            }) {
                Text("Reset")
            }
        }

        *//*detectedRectPx?.let { rectPx ->
            // convert px to dp for Compose layout
            val leftDp = with(density) { rectPx.left.toDp() }
            val topDp = with(density) { rectPx.top.toDp() }
            val widthDp = with(density) { rectPx.width().toDp() }
            val heightDp = with(density) { rectPx.height().toDp() }

            // Animatable floats (px-based for smoothness)
            val animLeft = remember { Animatable(rectPx.left) }
            val animTop = remember { Animatable(rectPx.top) }
            val animW = remember { Animatable(rectPx.width()) }
            val animH = remember { Animatable(rectPx.height()) }

            val scope = rememberCoroutineScope()
            LaunchedEffect(rectPx) {
                // animate to the detected rect (short tween)
                animLeft.animateTo(rectPx.left, animationSpec = tween(300))
                animTop.animateTo(rectPx.top, animationSpec = tween(300))
                animW.animateTo(rectPx.width(), animationSpec = tween(300))
                animH.animateTo(rectPx.height(), animationSpec = tween(300))

                // Then animate an expansion to the center (visual zoom) while also triggering camera zoom
                // compute final centered rect (e.g., make it 60% of preview width)
                val previewW = previewViewRef.value?.width?.toFloat() ?: previewSizePx.width.toFloat()
                val previewH = previewViewRef.value?.height?.toFloat() ?: previewSizePx.height.toFloat()
                val targetW = previewW * 0.6f
                val targetH = (targetW / (animW.value / animH.value)).coerceAtMost(previewH * 0.8f)

                val centerX = rectPx.centerX()
                val centerY = rectPx.centerY()
                val finalLeft = (centerX - targetW / 2f).coerceAtLeast(0f)
                val finalTop = (centerY - targetH / 2f).coerceAtLeast(0f)

                // launch camera zoom animation in parallel
                scope.launch {
                    cameraRef?.let { cam ->
                        // compute a target zoom ratio (heuristic)
                        val zoomState = cam.cameraInfo.zoomState.value
                        val maxZoom = zoomState?.maxZoomRatio ?: 4f
                        // want QR to cover ~60% width -> targetRatio = previewW * 0.6 / boxWidth
                        val currentBoxW = animW.value
                        val suggested = if (currentBoxW > 0) (previewW * 0.6f / currentBoxW) else 1f
                        val targetRatio = suggested.coerceIn(1f, maxZoom)

                        // animate linear zoom in small steps (simple but smooth)
                        val steps = 14
                        for (i in 1..steps) {
                            val t = i / steps.toFloat()
                            // map t to linear zoom [0..1] approximately (this is a heuristic)
                            val linear = t
                            try {
                                cam.cameraControl.setLinearZoom(linear)
                            } catch (e: Exception) {
                                // ignore animation errors
                            }
                            delay(12)
                        }
                        // ensure final zoom ratio is set
                        try {
                            cam.cameraControl.setZoomRatio(targetRatio)
                        } catch (_: Exception) {
                        }
                    }
                }

                // animate overlay to the final centered larger rect
                animLeft.animateTo(finalLeft, animationSpec = tween(350))
                animTop.animateTo(finalTop, animationSpec = tween(350))
                animW.animateTo(targetW, animationSpec = tween(350))
                animH.animateTo(targetH, animationSpec = tween(350))
            }

            // convert anim values to dp
            val animLeftDp = with(density) { animLeft.value.toDp() }
            val animTopDp = with(density) { animTop.value.toDp() }
            val animWDp = with(density) { animW.value.toDp() }
            val animHDp = with(density) { animH.value.toDp() }

            Box(
                Modifier
                    .offset(x = animLeftDp, y = animTopDp)
                    .size(animWDp, animHDp)
                    .border(width = 3.dp, color = androidx.compose.ui.graphics.Color.Green)
            )
        }*//*
    }
}*/

