package com.roland.android.odiyo.service

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import com.roland.android.odiyo.R
import com.roland.android.odiyo.service.Util.currentMediaIndex
import com.roland.android.odiyo.service.Util.deviceMuteState
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.nowPlaying
import com.roland.android.odiyo.service.Util.nowPlayingMetadata
import com.roland.android.odiyo.service.Util.playingState
import com.roland.android.odiyo.service.Util.progress

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(UnstableApi::class)
class PlayerListener : Player.Listener {
	override fun onEvents(player: Player, events: Player.Events) {
		super.onEvents(player, events)
		playingState.value = player.isPlaying
		deviceMuteState.value = player.isDeviceMuted
		progress.value = player.currentPosition
		currentMediaIndex.value = player.currentMediaItemIndex
	}

	override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
		super.onMediaMetadataChanged(mediaMetadata)
		nowPlaying.value = mediaSession?.player?.currentMediaItem
		nowPlayingMetadata.value = mediaMetadata
		Log.i("MediaMetaData", "New song[$mediaMetadata]\ntitle: ${mediaMetadata.title}\n" +
				"artworkData: ${mediaMetadata.artworkData}\nartist: ${mediaMetadata.artist}\n" +
				"albumTitle: ${mediaMetadata.albumTitle}\nartworkDataType: ${mediaMetadata.artworkDataType}"
		)
	}
}

@UnstableApi
class PlayerNotificationAdapter(
	private val context: Context,
	private val session: MediaSession?
) : PlayerNotificationManager.MediaDescriptionAdapter {
	override fun getCurrentContentTitle(player: Player): CharSequence {
		return player.mediaMetadata.title ?: "Unknown"
	}

	@RequiresApi(Build.VERSION_CODES.Q)
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
		val defaultArt = BitmapFactory.decodeResource(context.resources, R.drawable.default_art)
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