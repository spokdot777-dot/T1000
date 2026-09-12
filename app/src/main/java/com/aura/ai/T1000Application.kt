package com.aura.ai

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class T1000Application : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_AGENT,
                    "T1000 Agent",
                    NotificationManager.IMPORTANCE_LOW,
                ).apply { description = "Autonomous task execution and monitoring" },
            )
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_WAKE,
                    "T1000 Listening",
                    NotificationManager.IMPORTANCE_LOW,
                ).apply { description = "Wake phrase detection service" },
            )
        }
    }

    companion object {
        const val CHANNEL_AGENT = "t1000_agent"
        const val CHANNEL_WAKE = "t1000_wake"
    }
}
