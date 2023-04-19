package com.roland.android.odiyo.service

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import com.roland.android.odiyo.R

@UnstableApi class OdiyoNotificationManager(context: Context) {
	private val notificationManager: PlayerNotificationManager

	init {
		val builder = PlayerNotificationManager.Builder(context, 9570, "odiyo_notification")
		with (builder) {
			setChannelNameResourceId(R.string.notification_channel)
			setChannelDescriptionResourceId(R.string.channel_description)
			setPlayActionIconResourceId(android.R.drawable.ic_media_play)
			setPauseActionIconResourceId(android.R.drawable.ic_media_pause)
			setPreviousActionIconResourceId(android.R.drawable.ic_media_previous)
			setNextActionIconResourceId(android.R.drawable.ic_media_next)
		}
		notificationManager = builder.build()
		notificationManager.apply {
//			setMediaSessionToken(sessionToken)
			setSmallIcon(android.R.drawable.ic_media_play)
			setUseFastForwardAction(false)
			setUseRewindAction(false)
		}
	}

	fun showNotification(player: ExoPlayer) {
		notificationManager.setPlayer(player)
	}

	fun hideNotification() {
		notificationManager.setPlayer(null)
	}
}