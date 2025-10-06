package com.ch.ktorsample.CameraWithQRCodeScanner

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.concurrent.Executors
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

@Composable
fun CameraWithQRCodeScanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val cameraController = remember { LifecycleCameraController(context) }
    val previewView = remember { PreviewView(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    // Configure the scanner to only detect QR codes for better performance
    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    val barcodeScanner = remember { BarcodeScanning.getClient(options) }

    DisposableEffect(Unit) {
        cameraController.setImageAnalysisAnalyzer(
            executor,
            QRCodeAnalyzer(
                barcodeScanner = barcodeScanner,
                onQRCodeDetected = { bitmap ->
                    // The bitmap is already cropped in the analyzer
//                    capturedBitmap = bitmap

                }
            )
        )
        onDispose {
            executor.shutdown()
            barcodeScanner.close()
        }
    }

    LaunchedEffect(Unit) {
        cameraController.bindToLifecycle(lifecycleOwner)
        previewView.controller = cameraController
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

        capturedBitmap?.let { bmp ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Detected QR Code:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Captured QR Code",
                        modifier = Modifier.size(150.dp)
                    )
                }
            }
        }
    }
}


fun rememberQrBitmap(
    content: String?,
    size: Int = 800, // The desired width and height of the QR code
    padding: Int = 1 // The white border around the QR code
): ImageBitmap? {
    return try {
        // Configure the QR code writer
        val hints = mapOf(EncodeHintType.MARGIN to padding)
        val qrCodeWriter = QRCodeWriter()

        // Encode the string into a BitMatrix
        val bitMatrix = qrCodeWriter.encode(
            content,
            BarcodeFormat.QR_CODE,
            size,
            size,
            hints
        )

        // Create an empty Bitmap
        val bitmap = createBitmap(bitMatrix.width, bitMatrix.height)

        // Manually fill the Bitmap with pixels based on the BitMatrix
        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height) {
                val color = if (bitMatrix[x, y]) Color.Black.toArgb() else Color.White.toArgb()
                bitmap[x, y] = color
            }
        }

        // Convert the Android Bitmap to a Compose ImageBitmap
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun QRCodeGeneratorScreen(qrBitmap: ImageBitmap?) {
    // State to hold the text from the TextField
    var text by remember { mutableStateOf("Hello Jetpack Compose") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Enter text to generate QR Code")

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Content") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display the QR Code
        // It will be null if the text is blank, so the Box will be empty.
        qrBitmap?.let {
            Image(
                bitmap = it,
                contentDescription = "QR Code for '$text'",
                modifier = Modifier.size(250.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

// --- The Corrected Analyzer Class ---
class QRCodeAnalyzer(
    private val barcodeScanner: BarcodeScanner,
    private val onQRCodeDetected: (Bitmap) -> Unit
) : ImageAnalysis.Analyzer {

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            barcodeScanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        val barcode = barcodes.first()
                        val boundingBox = barcode.boundingBox ?: return@addOnSuccessListener

                        // The magic happens here: Convert the ImageProxy to a Bitmap.
                        // This is necessary because we need the full image to crop from.
                        val sourceBitmap = imageProxy.toBitmap() ?: return@addOnSuccessListener

                        // **CRITICAL STEP: Coordinate Transformation**
                        // The bounding box from ML Kit is relative to the analysis image's dimensions.
                        // We must scale it to the source bitmap's dimensions.
                        val scaledBoundingBox = scaleBoundingBox(
                            boundingBox,
                            image.width,
                            image.height,
                            sourceBitmap.width,
                            sourceBitmap.height
                        )

                        // Ensure the bounding box is within the bitmap's bounds
                        val left = scaledBoundingBox.left.coerceAtLeast(0)
                        val top = scaledBoundingBox.top.coerceAtLeast(0)
                        val width =
                            scaledBoundingBox.width().coerceAtMost(sourceBitmap.width - left)
                        val height =
                            scaledBoundingBox.height().coerceAtMost(sourceBitmap.height - top)

                        if (width > 0 && height > 0) {
                            val croppedBitmap = Bitmap.createBitmap(
                                sourceBitmap, left, top, width, height
                            )
                            onQRCodeDetected(croppedBitmap)
                        }
                    }
                }
                .addOnFailureListener { it.printStackTrace() }
                .addOnCompleteListener { imageProxy.close() }
        }
    }

    private fun scaleBoundingBox(
        box: Rect,
        analysisImageWidth: Int,
        analysisImageHeight: Int,
        bitmapWidth: Int,
        bitmapHeight: Int
    ): Rect {
        val scaleX = bitmapWidth.toFloat() / analysisImageWidth
        val scaleY = bitmapHeight.toFloat() / analysisImageHeight

        return Rect(
            (box.left * scaleX).toInt(),
            (box.top * scaleY).toInt(),
            (box.right * scaleX).toInt(),
            (box.bottom * scaleY).toInt()
        )
    }
}