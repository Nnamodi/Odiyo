package com.roland.android.domain.repository

import android.net.Uri
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.QueueMediaItem
import kotlinx.coroutines.flow.Flow

interface MusicQueueRepository {

	fun getSongsOnQueue(): Flow<List<Music>>

	fun playNext(uri: Uri)

	fun playNext(
		songs: List<Music>,
		collectionType: String = "",
		collectionName: String = ""
	)

	fun populateMusicQueue(songs: List<Music>)

	fun addToQueue(uri: Uri)

	fun addToQueue(
		songs: List<Music>,
		collectionType: String = "",
		collectionName: String = ""
	)

	fun playFromQueue(song: QueueMediaItem)

	fun duplicateSong(song: QueueMediaItem)

	fun removeSong(song: QueueMediaItem)

}