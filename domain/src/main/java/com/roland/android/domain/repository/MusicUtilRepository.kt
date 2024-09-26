package com.roland.android.domain.repository

import com.roland.android.domain.model.Music
import com.roland.android.domain.util.SortOptions
import kotlinx.coroutines.flow.Flow

interface MusicUtilRepository {

	fun renameSong(song: Music)

	fun favoriteSong(song: Music, favorite: Boolean)

	fun deleteSong(song: Music)

	fun setAsRingtone(song: Music, ringType: Int)

	fun shareSongs(songs: List<Music>)

	fun getSortOption(): Flow<SortOptions>

	fun toggleSortOption(option: SortOptions)

	fun getPermissionStatus(): Flow<Boolean>

	fun savePermissionStatus(permanentlyDenied: Boolean)

}