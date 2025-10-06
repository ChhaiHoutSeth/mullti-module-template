package com.ch.ktorsample

import android.annotation.SuppressLint
import android.graphics.Rect
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.ch.ktorsample.CameraWithQRCodeScanner.rememberQrBitmap
import com.ch.ktorsample.frameanimate.PulsatingImageToRect
import com.ch.ktorsample.frameanimate.expand
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalGetImage::class)
@Composable
fun QRCodeScanners() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val screenWidthPx = getScreenWidth()

    var qrCodeDetected by remember { mutableStateOf(false) }
    var boundingRect by remember { mutableStateOf<Rect?>(null) }

    var qrBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val cameraController = remember { LifecycleCameraController(context) }

    // State to hold the detected barcode value
    var barcode by remember { mutableStateOf<String?>(null) }

    // AndroidView to integrate the camera preview and barcode scanning
    AndroidView(
        modifier = Modifier.fillMaxSize(), // Make the view take up the entire screen
        factory = { ctx ->
            PreviewView(ctx).apply {

                // Configure barcode scanning options for supported formats
                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build()

                // Initialize the barcode scanner client with the configured options
                val barcodeScanner = BarcodeScanning.getClient(options)

                // Set up the image analysis analyzer for barcode detection
                cameraController.setImageAnalysisAnalyzer(
                    ContextCompat.getMainExecutor(ctx), // Use the main executor
                    MlKitAnalyzer(
                        listOf(barcodeScanner), // Pass the barcode scanner
                        ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED, // Use view-referenced coordinates
                        ContextCompat.getMainExecutor(ctx) // Use the main executor
                    ) { result: MlKitAnalyzer.Result? ->
                        // Process the barcode scanning results
                        val barcodeResults = result?.getValue(barcodeScanner)
                        if (!barcodeResults.isNullOrEmpty()) {
                            lifecycleOwner.lifecycleScope.launch {
                                cameraController.unbind()
                                delay(1000)
                                // Update the bounding rectangle of the detected barcode
                                boundingRect = barcodeResults.first().boundingBox?.expand(50)
                                qrBitmap = rememberQrBitmap(barcodeResults.first()?.rawValue)
                            }


                            /*lifecycleOwner.lifecycleScope.launch {
                                val qrWidthPx = boundingRect?.width() ?: 0
                                val qrWidthRatio = qrWidthPx / screenWidthPx  // 0.0 to 1.0
                                if (qrWidthRatio > 0.5) {
                                    Log.d("Barcode Ratio", "very close: $qrWidthRatio")
                                } else if (qrWidthRatio > 0.2 && qrWidthRatio < 0.5) {
                                    Log.d("Barcode Ratio", "medium: $qrWidthRatio")
                                } else if (qrWidthRatio < 0.2) {
                                    cameraController.cameraControl?.setLinearZoom(0.4f)
                                    cameraController.unbind()
                                    Log.d("Barcode Ratio", "far: $qrWidthRatio")
                                }


                                delay(800)
                                // Update the bounding rectangle of the detected barcode
                                boundingRect = barcodeResults.first().boundingBox

                                // Update the state to indicate a barcode has been detected
                                qrCodeDetected = true

                                // Update the barcode state with the first detected barcode
                                barcode = barcodeResults.first().rawValue

                            }*/

                        }
                    }
                )

                // Bind the camera controller to the lifecycle owner
                cameraController.bindToLifecycle(lifecycleOwner)

                // Set the camera controller for the PreviewView
                this.controller = cameraController
            }
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        PulsatingImageToRect(boundingBoxPx = boundingRect, qrBitmap)
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun getScreenWidth(): Float {
    val configuration = LocalConfiguration.current
    return with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
}
