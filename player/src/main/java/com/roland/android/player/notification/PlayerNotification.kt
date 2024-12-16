package com.roland.android.player.notification

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import com.roland.android.player.R
import com.roland.android.player.util.Constants.MUSIC_NOTIFICATION_CHANNEL_ID
import com.roland.android.player.util.Constants.MUSIC_NOTIFICATION_ID
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(UnstableApi::class)
class PlayerNotification(context: Context) : KoinComponent {
	private val mediaSession by inject<MediaSession>()
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
			setMediaDescriptionAdapter(PlayerNotificationAdapter(context, mediaSession))
			setNextActionIconResourceId(R.drawable.skip_next_icon)
//			setNotificationListener(PlayerNotificationListener(service))
			setPauseActionIconResourceId(R.drawable.pause_icon)
			setPlayActionIconResourceId(R.drawable.play_icon)
			setPreviousActionIconResourceId(R.drawable.skip_previous_icon)
		}
		notificationManager = builder.build()
		notificationManager.apply {
			setSmallIcon(R.drawable.player_notification_icon)
			setUseFastForwardAction(false)
			setUseNextActionInCompactView(true)
			setUsePreviousActionInCompactView(true)
			setUseRewindAction(false)
		}
	}

	fun showNotification(player: Player) {
		notificationManager.setPlayer(player)
	}

	fun hideNotification() {
		notificationManager.setPlayer(null)
	}
}