package com.roland.android.player.data_util

import android.net.Uri
import com.roland.android.data_repository.data_util.player.MusicQueueUtil
import com.roland.android.domain.model.CurrentPlaylist
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.QueueMediaItem
import com.roland.android.player.player_utils.MusicQueueUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MusicQueueUtilImpl : MusicQueueUtil, KoinComponent {
	private val musicQueueUtils by inject<MusicQueueUtils>()

	override fun restorePlaylistDetails(playlistDetails: CurrentPlaylist) {
		musicQueueUtils.restorePlaylistDetails(playlistDetails)
	}

	override fun playNext(uri: Uri) {
		musicQueueUtils.playNext(uri)
	}

	override fun playNext(songs: List<Music>) {
		musicQueueUtils.playNext(songs)
	}

	override fun addToQueue(uri: Uri) {
		musicQueueUtils.addToQueue(uri)
	}

	override fun addToQueue(songs: List<Music>) {
		musicQueueUtils.addToQueue(songs)
	}

	override fun playFromQueue(song: QueueMediaItem) {
		musicQueueUtils.playSongFromQueue(song)
	}

	override fun duplicateSong(song: QueueMediaItem) {
		musicQueueUtils.duplicateSongInQueue(song)
	}

	override fun removeSong(song: QueueMediaItem) {
		musicQueueUtils.removeSongFromQueue(song)
	}
}