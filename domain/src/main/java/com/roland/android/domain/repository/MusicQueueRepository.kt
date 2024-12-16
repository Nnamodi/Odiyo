package com.roland.android.domain.repository

import android.net.Uri
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.NowPlayingFrom
import com.roland.android.domain.model.QueueMediaItem
import kotlinx.coroutines.flow.Flow

interface MusicQueueRepository {

	fun getSongsOnQueue(): Flow<List<Music>>

	fun restorePlaylistDetails()

	fun playNext(uri: Uri)

	fun playNext(
		songs: List<Music>,
		nowPlayingFrom: NowPlayingFrom? = null
	)

	fun addToQueue(uri: Uri)

	fun addToQueue(
		songs: List<Music>,
		nowPlayingFrom: NowPlayingFrom? = null
	)

	fun playFromQueue(song: QueueMediaItem)

	fun duplicateSong(song: QueueMediaItem)

	fun removeSong(song: QueueMediaItem)

}