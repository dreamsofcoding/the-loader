package com.udacity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.udacity.Constants.CUSTOM
import com.udacity.Constants.DOWNLOAD_PREFS
import com.udacity.Constants.FILE_NAME
import com.udacity.Constants.GLIDE
import com.udacity.Constants.LOADAPP
import com.udacity.Constants.REPO_URL
import com.udacity.Constants.RETROFIT
import com.udacity.Constants.STATUS
import com.udacity.Constants.UNKNOWN
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var prefs: SharedPreferences
    private var downloadUrl: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        prefs = getSharedPreferences(DOWNLOAD_PREFS, MODE_PRIVATE)

        this.plantTimber()

        this.setSystemBars()

        setViewVisibility()

        updateUIWithTheStatus(intent)

        setClickListener()

        binding.detailContentLayout.motionLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.detailContentLayout.motionLayout.viewTreeObserver.removeOnGlobalLayoutListener(
                    this
                )
                binding.detailContentLayout.motionLayout.progress = 0f
                binding.detailContentLayout.motionLayout.postDelayed({
                    binding.detailContentLayout.motionLayout.transitionToEnd()
                }, 2500)
            }
        })

        NotificationHelper.dismissNotification(this)

    }

    private fun setViewVisibility() {
        binding.detailContentLayout.apply {
            fileRow.alpha = 0f
            statusRow.alpha = 0f
            okButton.alpha = 0f
        }
    }

    private fun updateUIWithTheStatus(intent: Intent) {
        downloadUrl = prefs.getString(REPO_URL, UNKNOWN)

        binding.detailContentLayout.apply {
            filenameText.text = when (intent.getStringExtra(FILE_NAME)) {
                GLIDE -> binding.root.context.getString(R.string.radio_option_glide)
                LOADAPP -> binding.root.context.getString(R.string.radio_option_loadapp)
                RETROFIT -> binding.root.context.getString(R.string.radio_option_retrofit)
                CUSTOM -> downloadUrl
                else -> UNKNOWN
            }

            statusText.text = intent.getStringExtra(STATUS) ?: UNKNOWN
        }
    }

    private fun setClickListener() {
        binding.detailContentLayout.okButton.setOnClickListener {
            prefs.edit {
                clear()
            }

            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(intent)
            finish()
        }
    }
}
