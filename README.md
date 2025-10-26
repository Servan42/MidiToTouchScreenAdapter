# MidiToTouchScreenAdapter

Map keys from a piano (MIDI) to positions on a phone screen.

This repository contains an Android app (Kotlin + some Java) that listens for MIDI input and converts MIDI note events into touchscreen gestures on the device. It's useful for driving apps that accept touch input using a real piano or any MIDI controller.

Minimum Android version: `13`.

Table of contents
- Features
- How it works
- Requirements
- Permissions & setup
- Build & install
- Usage
- Important

## Features
- Listen to MIDI events (USB / Bluetooth MIDI where supported by Android).
- Translate MIDI note on / off (and velocity) into screen gestures (tap / press / swipe).
- Hardcoded mapping between MIDI notes and screen positions.
- Lightweight Kotlin-first Android codebase with a small Java interop surface.

## How it works
1. The app opens a MIDI input (USB or Bluetooth MIDI device).
2. When a MIDI Note On is received, the app looks up the corresponding screen position for that note.
3. The app dispatches an accessibility gesture / touch event at the mapped position to simulate a real touch.
4. Note Off or velocity/hold logic can be used to release or change gestures depending on mapping and implementation.

## Requirements
- Android device (API level 33).
- An Android build environment (Android Studio 4.0+ recommended).
- A MIDI input device (USB MIDI keyboard, MIDI over Bluetooth LE device) supported by your device and Android build.
- Accessibility permission must be granted to the app to dispatch gestures.

## Permissions & setup
- Accessibility Service: The app needs an Accessibility service to dispatch gestures. Go to Settings → Accessibility and enable the app's service.
- USB/Bluetooth Permissions: When connecting a USB MIDI device you will be prompted to grant USB access. For Bluetooth MIDI, pair the device in Android settings first.
- Notification, as the app displays a notification everytime a MIDI device is connected successfuly.
- Optional: "Display over other apps" or other special permissions may be requested depending on the implementation used to surface UI or overlays.

## Build & install
1. Clone the repository:
   git clone https://github.com/Servan42/MidiToTouchScreenAdapter.git
2. Open the project in Android Studio.
3. Let Gradle sync and install required SDK components.
4. Build & run on a device (recommended) rather than an emulator (MIDI hardware support is limited in emulators).
   - From command line:
     ./gradlew assembleDebug
     adb install -r app/build/outputs/apk/debug/app-debug.apk

## Usage
1. Install the app on your Android device.
2. Enable the app's Accessibility service (Settings → Accessibility → MidiToTouchScreenAdapter).
3. Connect your MIDI keyboard (USB or Bluetooth).
5. Play keys. The app will convert incoming Note On events to touches at configured positions.

## Important

Since the app is considered as an accessibility app, it will run in background even after being closed. In order to interrup MIDI reading and touchscreen tap after the app is closed, you need to go to in the accessibility settings and disable it for this app. Then open an close the app again, that should kill everything. 