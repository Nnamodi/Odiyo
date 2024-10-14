package com.roland.android.data_repository.repository

import android.net.Uri
import com.roland.android.data_repository.data_source.local.LocalMusicSource
import com.roland.android.data_repository.data_util.local.LocalMusicUtil
import com.roland.android.data_repository.data_util.player.MusicQueueUtil
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.NowPlayingFrom
import com.roland.android.domain.model.QueueMediaItem
import com.roland.android.domain.repository.MusicQueueRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MusicQueueRepositoryImpl : MusicQueueRepository, KoinComponent {
	private val localMusicSource by inject<LocalMusicSource>()
	private val localMusicUtil by inject<LocalMusicUtil>()
	private val musicQueueUtil by inject<MusicQueueUtil>()

	override fun getSongsOnQueue(): Flow<List<Music>> {
		return localMusicSource.getSongsOnQueue()
	}

	override fun playNext(uri: Uri) {
		musicQueueUtil.playNext(uri)
	}

	override fun playNext(songs: List<Music>, collectionType: String, collectionName: String) {
		musicQueueUtil.playNext(songs)
		val nowPlayingFrom = NowPlayingFrom(collectionName, collectionType)
		localMusicUtil.saveCurrentPlaylistDetails(nowPlayingFrom)
	}

	override fun addToQueue(uri: Uri) {
		musicQueueUtil.addToQueue(uri)
	}

	override fun addToQueue(songs: List<Music>, collectionType: String, collectionName: String) {
		musicQueueUtil.addToQueue(songs)
		val nowPlayingFrom = NowPlayingFrom(collectionName, collectionType)
		localMusicUtil.saveCurrentPlaylistDetails(nowPlayingFrom)
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