# LoadApp

Android Download Manager ProjectLoadApp is a polished Android project built to demonstrate key concepts in custom UI, system notifications, file downloads, and animation using MotionLayout. This app allows users to select a repository (or enter a custom URL) and download a zip file using Android's DownloadManager. It includes real-time UI feedback via a custom loading button, system notifications, and animated transitions.

# Features
 Custom Download Button: Built with a custom View and Canvas, animating width and showing circular progress.
 Repository Selector: RadioGroup for selecting predefined repos (Glide, Retrofit, LoadApp) or entering a custom URL.
 Download via DownloadManager: Background downloading using system services, with real-time completion checks.
 System Notification: Shows download status with tap-to-view action and custom design.
 DetailActivity: Displays download status and selected repo name. Animates in with MotionLayout.

# Tech StackKotlin + Coroutines
Custom View (Canvas drawing)
DownloadManager
NotificationManager
MotionLayout

# How to Run
 Clone the repo
 Open in Android Studio
 Run on emulator or physical device (ensure storage + notification permissions are granted)

# Notes
 Compatible with Android API 28+
 Tested on Android 11+ with scoped storage

# License
 This project is intended for educational purposes only as part of the Udacity Advanced Android Developer Nanodegree.