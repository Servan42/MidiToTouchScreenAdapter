package com.example.miditotouchscreenadapter

import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiInputPort
import android.media.midi.MidiManager
import android.media.midi.MidiReceiver
import android.os.Handler
import android.os.Looper
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.miditotouchscreenadapter.ui.theme.MidiToTouchscreenAdapterTheme
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    //private lateinit var deviceCallback: MidiManager.DeviceCallback
    private val openDevices = mutableSetOf<Int>() // Keep track of already opened deviceIds
    private lateinit var midiManager: MidiManager
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("MIDI","APP STARTING");
        super.onCreate(savedInstanceState)

        midiManager = getSystemService(MidiManager::class.java)

        setContent {
            MidiToTouchscreenAdapterTheme {
                var noteText by remember { mutableStateOf("Waiting for MIDI input...") }

                Text(text = noteText, modifier = Modifier)

                LaunchedEffect(Unit) {   // runs only once when the Composable enters composition
                    val handler = Handler(Looper.getMainLooper())

                    // Register MIDI device callback once
                    midiManager.registerDeviceCallback(object : MidiManager.DeviceCallback() {
                        override fun onDeviceAdded(info: MidiDeviceInfo) {
                            openMidiDevice(info) { note, velocity ->
                                noteText = "Note: $note velocity=$velocity"
                            }
                        }
                        override fun onDeviceRemoved(info: MidiDeviceInfo) {
                            Log.i("MIDI", "Device removed: ${info.properties}")
                            // Optional: remove from openDevices
                            openDevices.remove(info.id)
                        }
                    }, handler)

                    // Open already-connected devices once
                    for (device in midiManager.devices) {
                        openMidiDevice(device) { note, velocity ->
                            noteText = "Note: $note velocity=$velocity"
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        openDevices.clear()
    }

    private fun openMidiDevice(info: MidiDeviceInfo, onNote: (Int, Int) -> Unit) {
        if (openDevices.contains(info.id)) {
            Log.i("MIDI", "Skip already open Device: ${info.properties}")
            return
        }

        midiManager.openDevice(info, { device ->
            if (device == null) {
                Log.e("MIDI", "Failed to open device")
                return@openDevice
            }
            listenToMidi(device, onNote)
        }, handler)
    }

    private fun listenToMidi(device: MidiDevice, onNote: (Int, Int) -> Unit) {
        Log.i("MIDI", "Listening to MIDI from ${device.info.properties}")

        val outputPort = device.openOutputPort(0)
        if (outputPort == null) {
            Log.e("MIDI", "No output port found on device ${device.info.properties}")
            return
        }

        val receiver = object : MidiReceiver() {
            override fun onSend(msg: ByteArray, offset: Int, count: Int, timestamp: Long) {
                if (count < 3) {
                    Log.w("MIDI", "Received short message: ${msg.joinToString()}")
                    return
                }

                val status = msg[offset].toInt() and 0xFF
                val note = msg[offset + 1].toInt() and 0xFF
                val velocity = msg[offset + 2].toInt() and 0xFF

                when {
                    status and 0xF0 == 0x90 && velocity > 0 -> {
                        Log.i("MIDI", "Note ON: $note velocity=$velocity")
                        onNote(note, velocity)
                    }
                    status and 0xF0 == 0x80 || (status and 0xF0 == 0x90 && velocity == 0) -> {
                        Log.i("MIDI", "Note OFF: $note velocity=$velocity")
                        onNote(note, 0)
                    }
                }
            }
        }

        outputPort.connect(receiver)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MidiToTouchscreenAdapterTheme {
        Greeting("Android")
    }
}