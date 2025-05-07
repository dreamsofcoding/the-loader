package com.udacity

import android.app.Application
import timber.log.Timber

class LoaderApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

    }
}