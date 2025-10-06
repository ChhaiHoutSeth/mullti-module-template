package com.ch.ktorsample

import android.content.Context
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
fun startCamera(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    previewView: PreviewView,
    onDetected: (String, RectF, Camera, ProcessCameraProvider) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val executor = Executors.newSingleThreadExecutor()

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()
            .also { it.setSurfaceProvider(previewView.surfaceProvider) }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetRotation(previewView.display.rotation)
            .build()

        // ML Kit barcode scanner
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE) // only QR
            .build()
        val scanner = BarcodeScanning.getClient(options)

        imageAnalysis.setAnalyzer(executor, ImageAnalysis.Analyzer { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage == null) {
                imageProxy.close()
                return@Analyzer
            }
            val rotation = imageProxy.imageInfo.rotationDegrees
            val inputImage = InputImage.fromMediaImage(mediaImage, rotation)
            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    if (!barcodes.isNullOrEmpty()) {
                        // pick the first barcode that has value
                        val barcode = barcodes.firstOrNull { it.rawValue != null }
                        barcode?.rawValue?.let { raw ->
                            val bbox: Rect? = barcode.boundingBox
                            if (bbox != null) {
                                // map bbox (image coords) to preview view coords using CameraX matrix
                                val matrix = getCorrectionMatrix(imageProxy, previewView)
                                val pts = floatArrayOf(
                                    bbox.left.toFloat(), bbox.top.toFloat(),
                                    bbox.right.toFloat(), bbox.bottom.toFloat()
                                )
                                matrix.mapPoints(pts)
                                val rectF = RectF(pts[0], pts[1], pts[2], pts[3])
                                onDetected(
                                    raw,
                                    rectF,
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        imageAnalysis,
                                    ),
                                    cameraProvider
                                )
                            }
                        }
                    }
                }
                .addOnFailureListener { /* ignore */ }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        })

        // bind (if not yet bound)
        try {
            val cam = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
            // no-op: camera returned if needed by caller
        } catch (e: Exception) {
            Log.e("QRZoom", "Failed to bind camera use cases", e)
        }

    }, ContextCompat.getMainExecutor(context))
}

/** Copied / adapted from CameraX docs: create matrix mapping ImageProxy.cropRect -> PreviewView coords */
private fun getCorrectionMatrix(imageProxy: ImageProxy, previewView: PreviewView): Matrix {
    val cropRect: Rect = imageProxy.cropRect
    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
    val matrix = Matrix()
    val source = floatArrayOf(
        cropRect.left.toFloat(), cropRect.top.toFloat(),
        cropRect.right.toFloat(), cropRect.top.toFloat(),
        cropRect.right.toFloat(), cropRect.bottom.toFloat(),
        cropRect.left.toFloat(), cropRect.bottom.toFloat()
    )
    val destination = floatArrayOf(
        0f, 0f,
        previewView.width.toFloat(), 0f,
        previewView.width.toFloat(), previewView.height.toFloat(),
        0f, previewView.height.toFloat()
    )
    val vertexSize = 2
    val shiftOffset = rotationDegrees / 90 * vertexSize
    val tempArray = destination.clone()
    for (toIndex in source.indices) {
        val fromIndex = (toIndex + shiftOffset) % source.size
        destination[toIndex] = tempArray[fromIndex]
    }
    matrix.setPolyToPoly(source, 0, destination, 0, 4)
    return matrix
}
