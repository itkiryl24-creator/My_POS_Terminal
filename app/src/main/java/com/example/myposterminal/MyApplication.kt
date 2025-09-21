package com.example.myposterminal

import android.app.Application
import com.example.myposterminal.data.storage.Config
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            Config.initializeDefaults(this@MyApplication)
        }
    }
}