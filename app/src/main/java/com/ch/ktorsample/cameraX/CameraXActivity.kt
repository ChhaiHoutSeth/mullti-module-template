package com.ch.ktorsample.cameraX

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.ch.core_ui.MyApplicationTheme
import com.ch.ktorsample.CameraWithQRCodeScanner.CameraWithQRCodeScanner
import com.ch.ktorsample.QRCodeScanners

class CameraXActivity : ComponentActivity() {

    companion object {
        val CAMERAX_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasRequiredPermissions()) {
            requestPermissions(CAMERAX_PERMISSION, 0)
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val controller = remember {
                        LifecycleCameraController(applicationContext).apply {
                            setEnabledUseCases(CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS)
                        }
                    }
//                    CameraWithQRCodeScanner()
                    QRCodeScanners()
//                    CameraPreview(cameraController = controller, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSION.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}