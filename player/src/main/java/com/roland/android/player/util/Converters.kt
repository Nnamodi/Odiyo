package com.roland.android.player.util

import com.roland.android.domain.model.Music
import com.roland.android.player.util.Constants.toMediaItem

object Converters {
	fun List<Music>.toMediaItems() = map {
		it.uri.toMediaItem
	}
}