package com.udacity

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.udacity.Constants.DOWNLOAD_PREFS
import com.udacity.Constants.FAILED
import com.udacity.Constants.SUCCESS
import com.udacity.Constants.UNKNOWN
import timber.log.Timber

class DownloadBroadcastReceiver : BroadcastReceiver() {

    var status = UNKNOWN
    var repoName = UNKNOWN

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        if (downloadId == -1L) return

        val prefs = context.getSharedPreferences(DOWNLOAD_PREFS, Context.MODE_PRIVATE)
        repoName = prefs.getString(downloadId.toString(), UNKNOWN) ?: UNKNOWN

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)

        cursor?.use {
            if (it.moveToFirst()) {
                val statusIndex = it.getColumnIndex(DownloadManager.COLUMN_STATUS)
                status = if (it.getInt(statusIndex) == DownloadManager.STATUS_SUCCESSFUL) SUCCESS
                else FAILED
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