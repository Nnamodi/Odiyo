package com.roland.android.data_system.util

import com.roland.android.data_system.model.SongDetails
import com.roland.android.data_system.util.Constants.toMediaItem
import com.roland.android.domain.model.Music

object Converters {
	fun Music.convertToSongDetails() = SongDetails(
		id = id,
		uri = uri,
		title = title,
		artist = artist
	)

	fun List<Music>.toMediaItems() = map { it.uri.toMediaItem }
}