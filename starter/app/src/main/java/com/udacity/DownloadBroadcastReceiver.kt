package com.udacity

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class DownloadBroadcastReceiver : BroadcastReceiver() {

    var status = "Unknown"
    var repoName = "Unknown"

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        if (downloadId == -1L) return

        val prefs = context.getSharedPreferences("download_prefs", Context.MODE_PRIVATE)
        repoName = prefs.getString(downloadId.toString(), "Unknown") ?: "Unknown"

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)

        cursor?.use {
            if (it.moveToFirst()) {
                val statusIndex = it.getColumnIndex(DownloadManager.COLUMN_STATUS)
                status = if (it.getInt(statusIndex) == DownloadManager.STATUS_SUCCESSFUL) "Success"
                else "Failed"
            }
        }

        Timber.d("Download complete: ID=$downloadId, Repo=$repoName")

        NotificationHelper.sendNotification(
            context = context,
            fileName = repoName,
            status = status,
            channelId = MainActivity.CHANNEL_ID
        )
    }
}