package com.usher.tactical

import android.app.Application
import com.usher.tactical.core.database.TacticalDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TacticalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 加载 SQLCipher 原生库
        try {
            System.loadLibrary("sqlcipher")
            TacticalDatabase.useEncryption = true
        } catch (e: UnsatisfiedLinkError) {
            android.util.Log.w("Tactical", "SQLCipher not available, disabling encryption", e)
            TacticalDatabase.useEncryption = false
        }
    }
}
