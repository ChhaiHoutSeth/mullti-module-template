package com.ch.ktorsample.cameraX

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ch.ktorsample.frameanimate.PulsatingImageToRect
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(
    cameraController: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var boundingRect by remember { mutableStateOf<Rect?>(null) }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    val imageAnalysis = ImageAnalysis.Builder()


    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(options)

    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context)
            val executor = ContextCompat.getMainExecutor(context)
            cameraController.bindToLifecycle(lifecycleOwner)

            previewView.apply {
                cameraController.setImageAnalysisAnalyzer(
                    executor,
                    MlKitAnalyzer(
                        listOf(barcodeScanner),
                        COORDINATE_SYSTEM_VIEW_REFERENCED,
                        executor
                    ) { result: MlKitAnalyzer.Result? ->
                        val barcodeResults = result?.getValue(barcodeScanner)
                        if ((barcodeResults == null) ||
                            (barcodeResults.isEmpty()) ||
                            (barcodeResults.first() == null)
                        ) {
                            previewView.overlay.clear()
                            previewView.setOnTouchListener { _, _ -> false } //no-op
                            return@MlKitAnalyzer
                        } else {
                            boundingRect = barcodeResults.first().boundingBox
                            Log.d("boundingBox", "boundingBox $boundingRect")


                            /*val qrCodeViewModel = QrCodeViewModel(barcodeResults[0])
                            val qrCodeDrawable = QrCodeDrawable(qrCodeViewModel)

                            previewView.setOnTouchListener(qrCodeViewModel.qrCodeTouchCallback)
                            previewView.overlay.clear()
                            previewView.overlay.add(qrCodeDrawable)*/
                        }
                    }
                )

                this.controller = cameraController
            }
        },
        modifier = modifier
    )

    Box(modifier = Modifier.fillMaxSize()) {
//        PulsatingImageToRect(boundingBoxPx = boundingRect)
    }

}

/*
private fun captureImageInMemory(
    context: Context,
    imageCapture: ImageCapture,
    onImageCaptured: (Bitmap) -> Unit
) {
    imageCapture.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                // Convert ImageProxy to Bitmap
                val bitmap = imageProxyToBitmap(image)
                image.close()
                onImageCaptured(bitmap)
            }

            override fun onError(e: ImageCaptureException) {
                e.printStackTrace()
            }
        }
    )
}


private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    // Rotate bitmap if needed based on image rotation
    val rotationDegrees = image.imageInfo.rotationDegrees
    if (rotationDegrees != 0) {
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees.toFloat())
        bitmap = Bitmap.createBitmap(
            bitmap, 0, 0,
            bitmap.width, bitmap.height,
            matrix, true
        )
    }

    return bitmap
}*/
