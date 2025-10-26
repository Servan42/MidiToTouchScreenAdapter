package com.example.miditotouchscreenadapter

import android.content.Context
import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiManager
import android.media.midi.MidiReceiver
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.MutableState

class MidiAdapter(
    private val context: Context,
    private val notificationService: NotificationService,
    private val currentNote: MutableState<String>
) {
    private val handler = Handler(Looper.getMainLooper())
    private val mapper = MidiToLocationMapper()
    private lateinit var deviceCallback: MidiManager.DeviceCallback
    private val openDevices = mutableSetOf<Int>() // Keep track of already opened deviceIds
    private lateinit var midiManager: MidiManager

    public fun startListening(){
        //midiManager = getSystemService(MidiManager::class.java)
        midiManager = context.getSystemService(Context.MIDI_SERVICE) as MidiManager

        // Define the callback
        deviceCallback = object : MidiManager.DeviceCallback() {
            override fun onDeviceAdded(info: MidiDeviceInfo) {
                Log.i("MIDI", "Device added: ${info.properties}")
                openMidiDevice(info) { note, velocity ->
                    // UI updated through Compose state
                    currentNote.value = "Note: $note velocity=$velocity"
                    val location = mapper.mapNoteToLocation(note)
                    MidiTouchAccessibilityService.instance?.simulateTap(location.x, location.y)
                }
            }

            override fun onDeviceRemoved(info: MidiDeviceInfo) {
                Log.i("MIDI", "Device removed: ${info.properties}")
                // Optional: remove from openDevices
                openDevices.remove(info.id)
            }
        }

        // Register the callback
        midiManager.registerDeviceCallback(deviceCallback, handler)

        // Open any already-connected devices
        for (device in midiManager.devices) {
            openMidiDevice(device) { note, velocity ->
                currentNote.value = "Note: $note velocity=$velocity"
                val location = mapper.mapNoteToLocation(note)
                MidiTouchAccessibilityService.instance?.simulateTap(location.x, location.y)
            }
        }
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
                        //onNote(note, 0)
                    }
                }
            }
        }

        outputPort.connect(receiver)
        notificationService.sendDeviceConnectedNotification(device.info.properties[MidiDeviceInfo.PROPERTY_NAME] as String)
    }
}