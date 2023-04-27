package com.roland.android.odiyo.service

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import com.roland.android.odiyo.R
import com.roland.android.odiyo.service.Constants.MUSIC_NOTIFICATION_CHANNEL_ID
import com.roland.android.odiyo.service.Constants.MUSIC_NOTIFICATION_ID

@UnstableApi class OdiyoNotificationManager(context: Context) {
	private val notificationManager: PlayerNotificationManager

	init {
		val builder = PlayerNotificationManager.Builder(
			context,
			MUSIC_NOTIFICATION_ID,
			MUSIC_NOTIFICATION_CHANNEL_ID
		)
		with (builder) {
			setChannelDescriptionResourceId(R.string.channel_description)
			setChannelNameResourceId(R.string.notification_channel)
		}
		notificationManager = builder.build()
		notificationManager.apply {
			setSmallIcon(android.R.drawable.ic_media_play)
			setUseFastForwardAction(false)
			setUseNextActionInCompactView(true)
			setUsePreviousActionInCompactView(true)
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