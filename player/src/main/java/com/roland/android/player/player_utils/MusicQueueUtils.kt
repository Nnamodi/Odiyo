package com.roland.android.player.player_utils

import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.session.MediaSession
import com.roland.android.data_repository.data_util.local.LocalMusicUtil
import com.roland.android.domain.model.CurrentPlaylist
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.QueueMediaItem
import com.roland.android.player.player_utils.States.currentMediaItem
import com.roland.android.player.player_utils.States.mediaItemsFlow
import com.roland.android.player.util.Constants.NOTHING_PLAYING
import com.roland.android.player.util.Constants.toMediaItem
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MusicQueueUtils : KoinComponent {
	private val mediaSession by inject<MediaSession>()
	private val localMusicUtil by inject<LocalMusicUtil>()

	fun restorePlaylistDetails(playlistDetails: CurrentPlaylist) {
		mediaSession.player.apply {
			val mediaItems = playlistDetails.playlist.map { it.toUri().toMediaItem }
			mediaItemsFlow.value = mediaItems.toMutableList()
			preparePlaylist()
			seekTo(
				playlistDetails.currentSongPosition,
				playlistDetails.currentSongSeekPosition
			)
		}
	}

	fun playNext(uri: Uri) {
		val mediaItem = uri.toMediaItem
		mediaSession.player.apply {
			if (mediaItemsFlow.value.isNotEmpty()) {
				val index = currentMediaItemIndex + 1
				addMediaItem(index, mediaItem)
				mediaItemsFlow.value.add(index, mediaItem)
			} else {
				pause()
				mediaItemsFlow.value = mutableListOf(mediaItem)
				preparePlaylist()
			}
		}
	}

	fun playNext(songs: List<Music>) {
		val songUris = songs.map { it.uri }
		val mediaItems = songUris.map { it.toMediaItem }
//		val newSongsIsLessThanQueuedSongs = songs.size <= mediaItemsFlow.value.size
		mediaSession.player.apply {
			if (mediaItemsFlow.value.isNotEmpty()) {
				val index = currentMediaItemIndex + 1
				addMediaItems(index, mediaItems)
				mediaItemsFlow.value.addAll(index, mediaItems)
			} else {
				pause()
				mediaItemsFlow.value = mediaItems.toMutableList()
				preparePlaylist()
			}
			val currentPlaylist = CurrentPlaylist(
				playlist = songUris.map { it.toString() },
				currentSongPosition = currentMediaItemIndex,
				currentSongSeekPosition = currentPosition
			)
			localMusicUtil.saveCurrentPlaylist(currentPlaylist)
		}
	}

	fun addToQueue(uri: Uri) {
		val mediaItem = uri.toMediaItem
		mediaSession.player.apply {
			if (mediaItemsFlow.value.isNotEmpty()) {
				addMediaItem(mediaItem)
				mediaItemsFlow.value.add(mediaItem)
			} else {
				pause()
				mediaItemsFlow.value = mutableListOf(mediaItem)
				preparePlaylist()
			}
		}
	}

	fun addToQueue(songs: List<Music>) {
		val songUris = songs.map { it.uri }
		val mediaItems = songUris.map { it.toMediaItem }
//		val newSongsIsLessThanQueuedSongs = songs.size <= mediaItemsFlow.value.size
		mediaSession.player.apply {
			if (mediaItemsFlow.value.isNotEmpty()) {
				addMediaItems(mediaItems)
				mediaItemsFlow.value.addAll(mediaItems)
			} else {
				pause()
				mediaItemsFlow.value = mediaItems.toMutableList()
				preparePlaylist()
			}
			val currentPlaylist = CurrentPlaylist(
				playlist = songUris.map { it.toString() },
				currentSongPosition = currentMediaItemIndex,
				currentSongSeekPosition = currentPosition
			)
			localMusicUtil.saveCurrentPlaylist(currentPlaylist)
		}
	}

	fun playSongFromQueue(song: QueueMediaItem) {
		mediaSession.player.apply {
			seekTo(song.index, 0)
			prepare(); play()
		}
	}

	fun duplicateSongInQueue(song: QueueMediaItem) {
		val index = song.index + 1
		val mediaItem = song.uri.toMediaItem
		mediaSession.player.addMediaItem(index, mediaItem)
		mediaItemsFlow.value.add(index, mediaItem)
	}

	fun removeSongFromQueue(song: QueueMediaItem) {
		mediaSession.player.removeMediaItem(song.index)
		mediaItemsFlow.value.removeAt(song.index)
		if (mediaItemsFlow.value.isNotEmpty()) return
		currentMediaItem.value = NOTHING_PLAYING
	}

	private fun preparePlaylist() {
		mediaSession.player.apply {
			clearMediaItems()
			setMediaItems(mediaItemsFlow.value)
			prepare()
		}
	}
}