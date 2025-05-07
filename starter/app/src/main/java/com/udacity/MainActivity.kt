package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.udacity.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var downloadID: Long = 0
    private var selectedUrl: String? = null
    private var selectedRepoName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        this.setSystemBars()

        NotificationHelper.setupChannel(this, CHANNEL_ID)

        registerTheReceiver()

        setupListeners()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerTheReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)

            registerReceiver(
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
        }
    }

    private fun setupListeners() {
        binding.mainContentLayout.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_glide -> {
                    selectedUrl = URL_GLIDE
                    selectedRepoName = "Glide"
                }
                R.id.radio_loadapp -> {
                    selectedUrl = URL_LOAD_APP
                    selectedRepoName = "LoadApp"
                }
                R.id.radio_retrofit -> {
                    selectedUrl = URL_RETROFIT
                    selectedRepoName = "Retrofit"
                }
            }
        }

        binding.mainContentLayout.customButton.setOnClickListener {
            if (selectedUrl == null) {
                Toast.makeText(this, getString(R.string.select_option), Toast.LENGTH_SHORT).show()
            } else {
                binding.mainContentLayout.customButton.buttonState = ButtonState.Loading
                download()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                Timber.d("Sending notification for: $selectedRepoName")
                NotificationHelper.sendNotification(this@MainActivity, selectedRepoName, "Success", CHANNEL_ID)
                binding.mainContentLayout.customButton.buttonState = ButtonState.Completed
            }
        }
    }

    private fun download() {
        try {
            val request = DownloadManager.Request(selectedUrl?.toUri())
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${selectedRepoName}.zip")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as? DownloadManager

            if (downloadManager != null) {
                downloadID = downloadManager.enqueue(request)

                Timber.d("Download started with ID: $downloadID")
            } else {
                Toast.makeText(this, "DownloadManager not available", Toast.LENGTH_LONG).show()
                Timber.e("DownloadManager service not found")
            }
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, "Invalid URL format", Toast.LENGTH_SHORT).show()
            Timber.e(e, "Invalid URL: $selectedUrl")
        } catch (e: Exception) {
            Toast.makeText(this, "Download failed to start", Toast.LENGTH_LONG).show()
            Timber.e(e, "Unexpected error during download")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    companion object {
        const val URL_GLIDE = "https://github.com/bumptech/glide/archive/master.zip"
        const val URL_LOAD_APP = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        const val URL_RETROFIT = "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "download_channel"
    }
}