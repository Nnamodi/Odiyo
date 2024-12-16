package com.roland.android.player.notification

import android.app.Notification
import android.content.Intent
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import com.roland.android.player.service.MusicPlayerService
import com.roland.android.player.util.Constants.MUSIC_NOTIFICATION_ID

@OptIn(UnstableApi::class)
class PlayerNotificationListener(
	private val service: MusicPlayerService,
) : PlayerNotificationManager.NotificationListener {
	private var isForegroundService = false

	override fun onNotificationPosted(
		notificationId: Int,
		notification: Notification,
		ongoing: Boolean,
	) {
		super.onNotificationPosted(notificationId, notification, ongoing)
		if (ongoing && !isForegroundService) {
			ContextCompat.startForegroundService(
				service,
				Intent(service.applicationContext, service::class.java)
			)
			service.startForeground(MUSIC_NOTIFICATION_ID, notification)
			isForegroundService = true
		}
		Log.i("MusicServiceInfo", "onNotificationPosted called")
	}

	override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
		service.apply {
			stopForeground(notificationId)
			isForegroundService = false
			stopSelf()
		}
	}
}