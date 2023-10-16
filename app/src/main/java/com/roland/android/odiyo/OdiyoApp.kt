package com.roland.android.odiyo

import android.app.Application
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.roland.android.odiyo.service.OdiyoNotificationManager
import com.roland.android.odiyo.service.PlayerListener
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.notificationManager
import com.roland.android.odiyo.service.Util.pendingIntent
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class OdiyoApp : Application() {
	@Inject lateinit var player: ExoPlayer

	override fun onCreate() {
		super.onCreate()
		// mediaSession and notificationManager will be initialized and managed in the Service class for background media playback
		mediaSession = MediaSession.Builder(this, player)
			.setSessionActivity(this.pendingIntent)
			.build()
		mediaSession?.player?.addListener(PlayerListener(this))
		notificationManager = OdiyoNotificationManager(this, mediaSession!!)
		notificationManager.showNotification(player)
	}

	override fun onTerminate() {
		super.onTerminate()
		notificationManager.hideNotification()
		mediaSession?.apply {
			player.apply { removeListener(PlayerListener(this@OdiyoApp)); release() }
			release()
			mediaSession = null
		}
	}
}