package com.roland.android.odiyo.service

import android.app.Notification
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import com.roland.android.odiyo.service.Util.isForegroundService

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
class MediaNotificationListener(
	private val service: MusicService,
) : PlayerNotificationManager.NotificationListener {
	override fun onNotificationPosted(
		notificationId: Int,
		notification: Notification,
		ongoing: Boolean,
	) {
		if (ongoing && !isForegroundService) {
			ContextCompat.startForegroundService(
				service.applicationContext,
				Intent(service.applicationContext, service.javaClass)
			)
			service.startForeground(notificationId, notification)
			isForegroundService = true
		}
	}

	override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
		service.apply {
			stopForeground(notificationId)
			isForegroundService = false
			stopSelf()
		}
	}
}