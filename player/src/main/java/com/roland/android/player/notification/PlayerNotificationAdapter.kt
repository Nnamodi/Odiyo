package com.roland.android.player.notification

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import com.roland.android.data_repository.R.drawable

@OptIn(UnstableApi::class)
class PlayerNotificationAdapter(
	private val context: Context,
	private val session: MediaSession?
) : PlayerNotificationManager.MediaDescriptionAdapter {
	override fun getCurrentContentTitle(player: Player): CharSequence {
		return player.mediaMetadata.title ?: "Unknown"
	}

	override fun createCurrentContentIntent(player: Player): PendingIntent? {
		return session?.sessionActivity
	}

	override fun getCurrentContentText(player: Player): CharSequence {
		return player.mediaMetadata.artist ?: "Unknown"
	}

	override fun getCurrentLargeIcon(
		player: Player,
		callback: PlayerNotificationManager.BitmapCallback,
	): Bitmap? {
		val defaultArt = BitmapFactory.decodeResource(context.resources, drawable.default_art)
		return try {
			session?.player?.mediaMetadata?.let {
				session.bitmapLoader.loadBitmapFromMetadata(it)?.get()
			} ?: defaultArt
		} catch(e: Exception) {
			e.message?.let { Log.e("PlayerNotificationLog", it) }
			defaultArt
		}
	}
}