package com.roland.android.data_repository.data_util.system

import android.net.Uri
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.QueueMediaItem

interface SystemMusicUtil {

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

	fun renameSong(song: Music)

	fun deleteSong(song: Music)

	fun setAsRingtone(songs: Music, ringType: Int)

	fun shareSong(songs: List<Music>)

}