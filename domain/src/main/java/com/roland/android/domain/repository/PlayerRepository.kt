package com.roland.android.domain.repository

import android.net.Uri
import com.roland.android.domain.model.Music

interface PlayerRepository {

	fun playSong(
		uri: Uri,
		index: Int? = null,
		collectionType: String = "",
		collectionName: String = ""
	)

	fun playNext(uri: Uri)

	fun playNext(
		song: List<Music>,
		collectionType: String = "",
		collectionName: String = ""
	)

	fun addToQueue(uri: Uri)

	fun addToQueue(
		song: List<Music>,
		collectionType: String = "",
		collectionName: String = ""
	)

}