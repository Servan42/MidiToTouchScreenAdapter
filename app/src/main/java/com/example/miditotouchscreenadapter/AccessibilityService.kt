package com.example.miditotouchscreenadapter

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent

@SuppressLint("AccessibilityPolicy")
class MidiTouchAccessibilityService : AccessibilityService() {

    companion object {
        var instance: MidiTouchAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.i("ACCESSIBILITY", "Service connected")
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() {
        Log.w("ACCESSIBILITY", "Service interrupted")
    }

    fun simulateTap(x: Float, y: Float) {
        Log.i("ACCESSIBILITY", "Simulating tap at ${x}:${y}")
        val gesture = GestureDescription.Builder()
            .addStroke(
                GestureDescription.StrokeDescription(
                    Path().apply { moveTo(x, y) },
                    0,
                    50
                )
            )
            .build()
        dispatchGesture(gesture, null, null)
        Log.i("ACCESSIBILITY", "TapEvent sent")
    }
}
