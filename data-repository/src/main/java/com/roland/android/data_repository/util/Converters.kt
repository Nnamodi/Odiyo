package com.roland.android.data_repository.util

import com.roland.android.data_repository.model.MusicFromSystem
import com.roland.android.data_repository.util.Extensions.date
import com.roland.android.data_repository.util.Extensions.time
import com.roland.android.data_repository.util.Extensions.toMb
import com.roland.android.domain.model.Music

object Converters {

	fun MusicFromSystem.convertToMusic() = Music(
		id  = id,
		uri = uri,
		name = name,
		title = title,
		artist = artist,
		album = album,
		path = path,
		addedOn = addedOn.date,
		duration = millis.time,
		size = "${bytes.toMb} MB"
	)

}