package com.roland.android.data_repository.repository

import android.net.Uri
import com.roland.android.data_repository.data_source.local.LocalMusicSource
import com.roland.android.data_repository.data_util.local.LocalMusicUtil
import com.roland.android.data_repository.data_util.player.MusicQueueUtil
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.NowPlayingFrom
import com.roland.android.domain.model.QueueMediaItem
import com.roland.android.domain.repository.MusicQueueRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MusicQueueRepositoryImpl : MusicQueueRepository, KoinComponent {
	private val localMusicSource by inject<LocalMusicSource>()
	private val localMusicUtil by inject<LocalMusicUtil>()
	private val musicQueueUtil by inject<MusicQueueUtil>()
	private val coroutineScope by inject<CoroutineScope>()

	override fun getSongsOnQueue(): Flow<List<Music>> {
		return localMusicSource.getSongsOnQueue()
	}

	override fun restorePlaylistDetails() {
		coroutineScope.launch {
			localMusicUtil.getCurrentPlaylist().collect {
				musicQueueUtil.restorePlaylistDetails(it)
			}
		}
	}

	override fun playNext(uri: Uri) {
		musicQueueUtil.playNext(uri)
	}

	override fun playNext(songs: List<Music>, nowPlayingFrom: NowPlayingFrom?) {
		musicQueueUtil.playNext(songs)
		nowPlayingFrom?.let(localMusicUtil::saveCurrentPlaylistDetails)
	}

	override fun addToQueue(uri: Uri) {
		musicQueueUtil.addToQueue(uri)
	}

	override fun addToQueue(songs: List<Music>, nowPlayingFrom: NowPlayingFrom?) {
		musicQueueUtil.addToQueue(songs)
		nowPlayingFrom?.let(localMusicUtil::saveCurrentPlaylistDetails)
	}

	override fun playFromQueue(song: QueueMediaItem) {
		musicQueueUtil.playFromQueue(song)
	}

	override fun duplicateSong(song: QueueMediaItem) {
		musicQueueUtil.duplicateSong(song)
	}

	override fun removeSong(song: QueueMediaItem) {
		musicQueueUtil.removeSong(song)
	}
}