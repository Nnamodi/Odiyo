package com.roland.android.player.player_utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import com.roland.android.player.player_utils.States.currentDuration
import com.roland.android.player.player_utils.States.currentMediaItem
import com.roland.android.player.player_utils.States.currentMediaItemIndex
import com.roland.android.player.player_utils.States.isDeviceMuted
import com.roland.android.player.player_utils.States.isPlaying
import com.roland.android.player.player_utils.States.nowPlayingMetadata
import com.roland.android.player.util.Constants.NOTHING_PLAYING
import com.roland.android.player.util.Constants.UNRECOGNIZED_INPUT_FORMAT_EXCEPTION
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayerListener(
	private val context: Context
) : Player.Listener, KoinComponent {
	private val mediaSession by inject<MediaSession>()

	override fun onEvents(player: Player, events: Player.Events) {
		super.onEvents(player, events)
		isPlaying.value = player.isPlaying
		isDeviceMuted.value = player.isDeviceMuted
		currentDuration.value = player.currentPosition
		currentMediaItemIndex.value = player.currentMediaItemIndex
	}

	override fun onPlayerError(error: PlaybackException) {
		super.onPlayerError(error)
		Log.e("PlaybackInfo", "${error.message} | ${error.cause} | ${error.errorCode}", error)
		if (error.errorCode != UNRECOGNIZED_INPUT_FORMAT_EXCEPTION) return
		Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT).show()
	}

	override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
		super.onMediaItemTransition(mediaItem, reason)
		currentMediaItem.value = mediaItem ?: NOTHING_PLAYING
	}

	override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
		super.onMediaMetadataChanged(mediaMetadata)
		nowPlayingMetadata.value = mediaMetadata
		currentMediaItem.value = mediaSession.player.currentMediaItem ?: NOTHING_PLAYING
		Log.i("MediaMetaData", "New song[$mediaMetadata]\ntitle: ${mediaMetadata.title}\n" +
				"artworkData: ${mediaMetadata.artworkData}\nartist: ${mediaMetadata.artist}\n" +
				"albumTitle: ${mediaMetadata.albumTitle}\nartworkDataType: ${mediaMetadata.artworkDataType}"
		)
	}
}