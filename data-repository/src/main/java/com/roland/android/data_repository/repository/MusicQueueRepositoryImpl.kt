package com.roland.android.data_repository.repository

import android.net.Uri
import com.roland.android.data_repository.data_source.local.LocalMusicSource
import com.roland.android.data_repository.data_util.local.LocalMusicUtil
import com.roland.android.data_repository.data_util.system.SystemMusicUtil
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
	private val systemMusicUtil by inject<SystemMusicUtil>()

	override fun getSongsOnQueue(): Flow<List<Music>> {
		return localMusicSource.getSongsOnQueue()
	}

	override fun playNext(uri: Uri) {
		systemMusicUtil.playNext(uri)
	}

	override fun playNext(songs: List<Music>, collectionType: String, collectionName: String) {
		systemMusicUtil.playNext(songs)
		val nowPlayingFrom = NowPlayingFrom(collectionName, collectionType)
		localMusicUtil.saveCurrentPlaylistDetails(nowPlayingFrom)
	}

	override fun populateMusicQueue(songs: List<Music>) {
		systemMusicUtil.populateMusicQueue(songs)
	}

	override fun addToQueue(uri: Uri) {
		systemMusicUtil.addToQueue(uri)
	}

	override fun addToQueue(songs: List<Music>, collectionType: String, collectionName: String) {
		systemMusicUtil.addToQueue(songs)
		val nowPlayingFrom = NowPlayingFrom(collectionName, collectionType)
		localMusicUtil.saveCurrentPlaylistDetails(nowPlayingFrom)
	}

	override fun playFromQueue(song: QueueMediaItem) {
		systemMusicUtil.playFromQueue(song)
	}

	override fun duplicateSong(song: QueueMediaItem) {
		systemMusicUtil.duplicateSong(song)
	}

	override fun removeSong(song: QueueMediaItem) {
		systemMusicUtil.removeSong(song)
	}
}