package com.roland.android.player.player_utils

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaSession
import com.roland.android.data_repository.data_source.local.LocalMusicSource
import com.roland.android.domain.model.Music
import com.roland.android.player.player_utils.States.currentMediaItem
import com.roland.android.player.player_utils.States.mediaItemsFlow
import com.roland.android.player.util.Constants.toMediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.URLDecoder
import java.nio.charset.StandardCharsets.UTF_8

class PlayerUtils(private val context: Context) : KoinComponent {
	private val mediaSession by inject<MediaSession>()
	private val musicSource by inject<LocalMusicSource>()
	private var initialDeviceVolume = 0

	fun playSong(
		uri: Uri,
		index: Int,
		mediaItems: List<MediaItem>
	) {
		mediaSession.player.apply {
			if (isLoading) return
			// reset playlist when a mediaItem is selected from list
			mediaItemsFlow.value = mediaItems.toMutableList()
			preparePlaylist()
			seekTo(index, 0)
			play()
//			updateMusicQueue()
			Log.d("ViewModelInfo", "playAudio: $index\n$uri")
		}
	}

	fun playPause(): Flow<Boolean> {
		mediaSession.player.apply {
			if (isLoading) return flowOf(isPlaying)
			if (isPlaying) pause() else { prepare(); play() }
			Log.d("ViewModelInfo", "playAudio: ${currentMediaItem?.localConfiguration?.uri?.toMediaItem}")
			return flowOf(isPlaying)
		}
	}

	fun getCurrentSong(): Flow<Music?> {
		return combine(
			musicSource.getAllSongs(),
			currentMediaItem
		) { allSongs, nowPlaying ->
			val songUri = nowPlaying?.localConfiguration?.uri
			val songPath = URLDecoder.decode(songUri.toString(), UTF_8.name())
			allSongs.find {
				it.uri == songUri || songPath.contains(it.path)
			}
		}
			.flowOn(Dispatchers.IO)
	}

	fun seek(previous: Boolean, next: Boolean) {
		mediaSession.player.apply {
			val songHasStarted = currentPosition >= 5000 // streamed for 5secs or more
			when {
				previous -> if (songHasStarted) seekToPrevious() else seekToPreviousMediaItem()
				next -> seekToNextMediaItem()
			}
		}
	}

	fun onSeekToPosition(position: Long) {
		mediaSession.player.seekTo(position)
	}

	fun onMuteDevice() {
		val audioManager = (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
		val streamType = AudioManager.STREAM_MUSIC
		val setVolume: (Int) -> Unit = { audioManager.setStreamVolume(streamType, it, 0) }

		if (audioManager.isStreamMute(streamType)) {
			if (initialDeviceVolume == 0) initialDeviceVolume++
			setVolume(initialDeviceVolume)
		} else {
			initialDeviceVolume = audioManager.getStreamVolume(streamType)
			setVolume(0)
		}
	}

	fun setRepeatMode(repeatMode: Int) {
		mediaSession.player.repeatMode = repeatMode
	}

	fun setShuffleMode(shuffleMode: Boolean) {
		mediaSession.player.shuffleModeEnabled = shuffleMode
	}

	private fun preparePlaylist() {
		mediaSession.player.apply {
			clearMediaItems()
			setMediaItems(mediaItemsFlow.value)
			prepare()
		}
	}
}