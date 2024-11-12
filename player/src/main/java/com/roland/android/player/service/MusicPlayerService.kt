package com.roland.android.player.service

import android.util.Log
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.roland.android.player.notification.PlayerNotification
import com.roland.android.player.player_utils.PlayerListener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MusicPlayerService : MediaSessionService(), KoinComponent {
	private val mediaSession by inject<MediaSession>()
	private val notificationManager by inject<PlayerNotification>()

	override fun onCreate() {
		super.onCreate()
		mediaSession.player.addListener(PlayerListener(this))
		notificationManager.showNotification(mediaSession.player)
		Log.i("MusicServiceInfo", "MusicService created")
	}

	override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

	override fun onDestroy() {
		notificationManager.hideNotification()
		mediaSession.apply {
			player.apply {
				removeListener(PlayerListener(this@MusicPlayerService))
				release()
			}
			release()
		}
		super.onDestroy()
	}
}