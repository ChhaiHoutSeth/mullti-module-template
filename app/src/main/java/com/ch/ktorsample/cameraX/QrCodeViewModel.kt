package com.ch.ktorsample.cameraX

import android.content.Intent
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import androidx.core.net.toUri
import com.google.mlkit.vision.barcode.common.Barcode

class QrCodeViewModel(barcode: Barcode) {
    var boundingRect: Rect = barcode.boundingBox!!
    var qrContent: String = ""
    var qrCodeTouchCallback = { v: View, e: MotionEvent -> false} //no-op

    init {
        when (barcode.valueType) {
            Barcode.TYPE_URL -> {
                qrContent = barcode.url?.url!!
                qrCodeTouchCallback = { v: View, e: MotionEvent ->
                    if (e.action == MotionEvent.ACTION_DOWN && boundingRect.contains(e.getX().toInt(), e.getY().toInt())) {
                        val openBrowserIntent = Intent(Intent.ACTION_VIEW)
                        openBrowserIntent.data = qrContent.toUri()
                        v.context.startActivity(openBrowserIntent)
                    }
                    true // return true from the callback to signify the event was handled
                }
            }
            // Add other QR Code types here to handle other types of data,
            // like Wifi credentials.
            else -> {
                qrContent = "Unsupported data type: ${barcode.rawValue.toString()}"
            }
        }
    }
}