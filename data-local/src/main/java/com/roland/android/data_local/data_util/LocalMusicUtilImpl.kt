package com.roland.android.data_local.data_util

import com.roland.android.data_local.database.MusicDao
import com.roland.android.data_local.datastore.MusicUtilStore
import com.roland.android.data_local.util.Converters.convertToMusicEntity
import com.roland.android.data_repository.data_util.local.LocalMusicUtil
import com.roland.android.domain.model.Music
import com.roland.android.domain.util.SortOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocalMusicUtilImpl : LocalMusicUtil, KoinComponent {
	private val musicDao by inject<MusicDao>()
	private val musicUtilStore by inject<MusicUtilStore>()
	private val coroutineScope by inject<CoroutineScope>()

	override fun addNewSongs(songs: List<Music>) {
		coroutineScope.launch {
			val songEntities = songs.map { it.convertToMusicEntity() }
			musicDao.addNewSongs(songEntities)
		}
	}

	override fun renameSong(song: Music) {
		coroutineScope.launch {
			val musicEntity = song.convertToMusicEntity()
			musicDao.updateSong(musicEntity)
		}
	}

	override fun favoriteSong(song: Music, favorite: Boolean) {
		coroutineScope.launch {
			val musicEntity = song.convertToMusicEntity(favorite)
			musicDao.updateSong(musicEntity)
		}
	}

	override fun deleteSong(song: Music) {
		coroutineScope.launch {
			val musicEntity = song.convertToMusicEntity()
			musicDao.deleteSong(musicEntity)
		}
	}

	override fun getSortOption(): Flow<SortOptions> {
		return musicUtilStore.getSortPreference()
	}

	override fun toggleSortOption(option: SortOptions) {
		coroutineScope.launch {
			musicUtilStore.saveSortPreference(option)
		}
	}

	override fun getPermissionStatus(): Flow<Boolean> {
		return musicUtilStore.getPermissionStatus()
	}

	override fun savePermissionStatus(permanentlyDenied: Boolean) {
		coroutineScope.launch {
			musicUtilStore.savePermissionStatus(permanentlyDenied)
		}
	}
}