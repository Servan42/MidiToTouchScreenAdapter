package com.example.miditotouchscreenadapter

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.miditotouchscreenadapter.ui.theme.MidiToTouchscreenAdapterTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("MAIN","APP STARTING");
        super.onCreate(savedInstanceState)

        val notificationService = NotificationService(this)
        notificationService.createNotificationChannel()

        // Compose UI state
        val currentNote = mutableStateOf("Waiting for MIDI input...")

        val midiAdapter = MidiAdapter(this, notificationService, currentNote)
        midiAdapter.startListening()

        // Compose UI
        setContent {
            MidiToTouchscreenAdapterTheme {
                Text(text = currentNote.value, modifier = Modifier.fillMaxSize())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}