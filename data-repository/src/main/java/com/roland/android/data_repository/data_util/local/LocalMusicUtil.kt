package com.roland.android.data_repository.data_util.local

import com.roland.android.domain.model.Music
import com.roland.android.domain.util.SortOptions
import kotlinx.coroutines.flow.Flow

interface LocalMusicUtil {

	fun addNewSongs(songs: List<Music>)

	fun renameSong(song: Music)

	fun favoriteSong(song: Music, favorite: Boolean)

	fun deleteSong(song: Music)

	fun getSortOption(): Flow<SortOptions>

	fun toggleSortOption(option: SortOptions)

	fun getPermissionStatus(): Flow<Boolean>

	fun savePermissionStatus(permanentlyDenied: Boolean)

}