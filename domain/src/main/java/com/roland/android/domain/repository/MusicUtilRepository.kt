package com.roland.android.domain.repository

import com.roland.android.domain.model.Music
import com.roland.android.domain.util.SortOptions
import kotlinx.coroutines.flow.Flow

interface MusicUtilRepository {

	fun renameSong(song: Music)

	fun favoriteSong(song: Music, favorite: Boolean)

	fun setAsRingtone(songs: Music, ringType: Int)

	fun shareSong(songs: List<Music>)

	fun getSortOption(): Flow<SortOptions>

	fun toggleSortOption(option: SortOptions)

	fun deleteSong(song: Music)

}