package com.example.miditotouchscreenadapter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

public class NotificationService(private val context: Context) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val CHANNEL_ID = "midi_device_channel"

    public fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val name = "MIDI Devices"
        val descriptionText = "Notifications when a MIDI device is connected"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        notificationManager.createNotificationChannel(channel)
    }

    public fun sendDeviceConnectedNotification(name: String) {
        val notification = Notification.Builder(context, CHANNEL_ID)
            .setContentTitle("MIDI Device Connected")
            .setContentText("MIDI device connected: $name")
            .setSmallIcon(android.R.drawable.ic_media_play) // use any icon you like
            .setAutoCancel(true)
            .build()

        this.notificationManager.notify(0, notification) // unique ID per device
    }
}