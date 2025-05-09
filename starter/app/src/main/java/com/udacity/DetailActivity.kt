package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.Constants.FILE_NAME
import com.udacity.Constants.GLIDE
import com.udacity.Constants.LOADAPP
import com.udacity.Constants.RETROFIT
import com.udacity.Constants.STATUS
import com.udacity.Constants.UNKNOWN
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        this.plantTimber()

        this.setSystemBars()

        updateUIWithTheStatus(intent)

        setClickListener()
    }

    private fun updateUIWithTheStatus(intent: Intent) {

        binding.detailContentLayout.filenameText.text = when(intent.getStringExtra(FILE_NAME)){
            GLIDE -> binding.root.context.getString(R.string.radio_option_glide)
            LOADAPP -> binding.root.context.getString(R.string.radio_option_loadapp)
            RETROFIT -> binding.root.context.getString(R.string.radio_option_retrofit)
            else -> UNKNOWN
        }

        binding.detailContentLayout.statusText.text = intent.getStringExtra(STATUS) ?: UNKNOWN
    }

    private fun setClickListener() {
     binding.detailContentLayout.okButton.setOnClickListener {
         val intent = Intent(this, MainActivity::class.java).apply {
             addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
         }
         startActivity(intent)
         finish()
     }
    }
}
