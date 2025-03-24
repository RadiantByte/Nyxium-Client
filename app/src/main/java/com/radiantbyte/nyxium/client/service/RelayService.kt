package com.radiantbyte.nyxium.client.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.radiantbyte.nyxium.client.application.AppContext
import com.radiantbyte.nyxium.client.game.ModuleManager
import com.radiantbyte.nyxium.client.model.GameSettingsModel
import com.radiantbyte.nyxium.client.overlay.OverlayManager
import kotlin.concurrent.thread

class RelayService : Service() {
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "relay_service"
        private const val NOTIFICATION_ID = 1

        var isActive = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        startRelay()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRelay()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Relay Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Nyxium Relay")
            .setContentText("Relay service is running")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun startRelay() {
        thread(name = "RakThread") {

            val gameSettingsSharedPreferences =
                AppContext.instance.getSharedPreferences("game_settings", Context.MODE_PRIVATE)
          GameSettingsModel.from(gameSettingsSharedPreferences)

            ModuleManager.loadConfig()

            runCatching {
                isActive = true
                OverlayManager.show(this@RelayService)

            }.onFailure { error ->
                error.printStackTrace()
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun stopRelay() {
        isActive = false
        ModuleManager.saveConfig()
        OverlayManager.dismiss()
    }
}