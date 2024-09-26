package com.roland.android.data_repository.repository

import com.roland.android.data_repository.data_util.local.LocalMusicUtil
import com.roland.android.data_repository.data_util.system.SystemMusicUtil
import com.roland.android.domain.model.Music
import com.roland.android.domain.repository.MusicUtilRepository
import com.roland.android.domain.util.SortOptions
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MusicUtilRepositoryImpl : MusicUtilRepository, KoinComponent {
	private val localMusicUtil by inject<LocalMusicUtil>()
	private val systemMusicUtil by inject<SystemMusicUtil>()

	override fun renameSong(song: Music) {
		localMusicUtil.renameSong(song)
		systemMusicUtil.renameSong(song)
	}

	override fun favoriteSong(song: Music, favorite: Boolean) {
		localMusicUtil.favoriteSong(song, favorite)
	}

	override fun deleteSong(song: Music) {
		localMusicUtil.deleteSong(song)
		systemMusicUtil.deleteSong(song)
	}

	override fun setAsRingtone(song: Music, ringType: Int) {
		systemMusicUtil.setAsRingtone(song, ringType)
	}

	override fun shareSongs(songs: List<Music>) {
		systemMusicUtil.shareSong(songs)
	}

	override fun getSortOption(): Flow<SortOptions> {
		return localMusicUtil.getSortOption()
	}

	override fun toggleSortOption(option: SortOptions) {
		localMusicUtil.toggleSortOption(option)
	}

	override fun getPermissionStatus(): Flow<Boolean> {
		return localMusicUtil.getPermissionStatus()
	}

	override fun savePermissionStatus(permanentlyDenied: Boolean) {
		localMusicUtil.savePermissionStatus(permanentlyDenied)
	}
}