package com.roland.android.odiyo.service

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import com.roland.android.odiyo.R
import com.roland.android.odiyo.service.Util.NOTHING_PLAYING
import com.roland.android.odiyo.service.Util.mediaItems
import com.roland.android.odiyo.service.Util.mediaItemsUiState
import com.roland.android.odiyo.service.Util.mediaUiState
import com.roland.android.odiyo.service.Util.nowPlayingMetadata
import com.roland.android.odiyo.service.Util.nowPlayingUiState
import com.roland.android.odiyo.service.Util.playingState
import com.roland.android.odiyo.service.Util.time
import kotlinx.coroutines.flow.update

class PlayerListener(private val context: Context) : Player.Listener {
	override fun onEvents(player: Player, events: Player.Events) {
		super.onEvents(player, events)
		playingState.value = player.isPlaying
		nowPlayingUiState.update {
			it.copy(
				playingState = player.isPlaying,
				deviceMuted = player.isDeviceMuted,
				currentDuration = player.currentPosition.time,
				seekProgress = player.currentPosition.toFloat(),
				currentSongIndex = player.currentMediaItemIndex
			)
		}
	}

	override fun onPlayerError(error: PlaybackException) {
		super.onPlayerError(error)
		Log.i("PlaybackInfo", "${error.message}", error)
		if (mediaItems.value.isEmpty()) return
		Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
	}

	override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
		super.onMediaItemTransition(mediaItem, reason)
		val currentMediaItem = mediaItem ?: NOTHING_PLAYING
		mediaUiState.update { it.copy(currentMediaItem = currentMediaItem) }
		mediaItemsUiState.update { it.copy(currentMediaItem = currentMediaItem) }
	}

	override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
		super.onMediaMetadataChanged(mediaMetadata)
		nowPlayingMetadata.value = mediaMetadata
		Log.i("MediaMetaData", "New song[$mediaMetadata]\ntitle: ${mediaMetadata.title}\n" +
				"artworkData: ${mediaMetadata.artworkData}\nartist: ${mediaMetadata.artist}\n" +
				"albumTitle: ${mediaMetadata.albumTitle}\nartworkDataType: ${mediaMetadata.artworkDataType}"
		)
	}
}

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