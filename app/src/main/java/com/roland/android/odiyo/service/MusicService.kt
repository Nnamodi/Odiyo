package com.roland.android.odiyo.service

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.roland.android.odiyo.OdiyoApp
import com.roland.android.odiyo.service.Util.audioAttribute
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.pendingIntent
import com.roland.android.odiyo.service.Util.toMediaItem
import kotlinx.coroutines.*

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
class MusicService : MediaSessionService() {
	private lateinit var notificationManager: OdiyoNotificationManager
	private val repository = OdiyoApp.mediaRepository
	private val serviceJob = SupervisorJob()
	private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
	private var mediaItems by mutableStateOf<List<MediaItem>>(emptyList())

	override fun onCreate() {
		super.onCreate()
		val player = ExoPlayer.Builder(this)
			.setAudioAttributes(audioAttribute, true)
			.build()
		mediaSession = MediaSession.Builder(this, player)
			.setSessionActivity(this.pendingIntent)
			.build()
		mediaSession?.player?.addListener(PlayerListener())
		serviceScope.launch {
			repository.getAllSongs.collect { songs ->
				mediaItems = songs.map { it.uri.toMediaItem }
				mediaSession?.player?.apply {
					setMediaItems(mediaItems)
					prepare()
				}
			}
		}
		notificationManager = OdiyoNotificationManager(this, mediaSession!!)
		notificationManager.showNotification(player)
	}

	override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

	override fun onDestroy() {
		notificationManager.hideNotification()
		mediaSession?.apply {
			player.apply { removeListener(PlayerListener()); release() }
			release()
			mediaSession = null
		}
		serviceScope.cancel()
		super.onDestroy()
	}
}