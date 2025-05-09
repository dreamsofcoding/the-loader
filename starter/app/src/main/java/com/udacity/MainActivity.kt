package com.udacity

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.udacity.Constants.DOWNLOAD_PREFS
import com.udacity.Constants.GLIDE
import com.udacity.Constants.LOADAPP
import com.udacity.Constants.RETROFIT
import com.udacity.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        this.plantTimber()

        this.setSystemBars()

        NotificationHelper.setupChannel(this, CHANNEL_ID)

        setupListeners()
    }

    private fun setupListeners() {
        binding.mainContentLayout.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_glide -> {
                    selectedUrl = URL_GLIDE
                    selectedRepoName = GLIDE
                }

                R.id.radio_loadapp -> {
                    selectedUrl = URL_LOAD_APP
                    selectedRepoName = LOADAPP
                }

                R.id.radio_retrofit -> {
                    selectedUrl = URL_RETROFIT
                    selectedRepoName = RETROFIT
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

    @SuppressLint("Range")
    private fun download() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1002
                )
            }

            val request = DownloadManager.Request(selectedUrl?.toUri())
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalFilesDir(
                    this,
                    Environment.DIRECTORY_DOWNLOADS,
                    "${selectedRepoName}.zip"
                )
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as? DownloadManager
            if (downloadManager != null) {
                downloadID = downloadManager.enqueue(request)

                val prefs = getSharedPreferences(DOWNLOAD_PREFS, MODE_PRIVATE)
                prefs.edit { putString(downloadID.toString(), selectedRepoName) }

                lifecycleScope.launch {
                    while (true) {
                        val query = DownloadManager.Query().setFilterById(downloadID)
                        val cursor = downloadManager.query(query)
                        var completed = false

                        cursor?.use {
                            if (it.moveToFirst()) {
                                val statusIndex = it.getColumnIndex(DownloadManager.COLUMN_STATUS)
                                val statusCode = it.getInt(statusIndex)
                                when (statusCode) {
                                    DownloadManager.STATUS_SUCCESSFUL, DownloadManager.STATUS_FAILED -> {
                                        updateButtonOnDownloadComplete()
                                        completed = true
                                    }
                                }
                            }
                        }

                        if (completed) break
                        delay(1000L)
                    }
                }

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

    private fun updateButtonOnDownloadComplete() {
        binding.mainContentLayout.customButton.buttonState = ButtonState.Completed
    }


    companion object {
        const val URL_GLIDE = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        const val URL_LOAD_APP =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        const val URL_RETROFIT = "https://github.com/square/retrofit/archive/refs/heads/trunk.zip"
        const val CHANNEL_ID = "download_channel"
    }
}

