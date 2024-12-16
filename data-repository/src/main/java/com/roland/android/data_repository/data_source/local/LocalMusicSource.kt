package com.roland.android.data_repository.data_source.local

import com.roland.android.domain.model.Music
import kotlinx.coroutines.flow.Flow

interface LocalMusicSource {

	fun getAllSongs(): Flow<List<Music>>

	fun getLastPlayedSongs(): Flow<List<Music>>

	fun getFavoriteSongs(): Flow<List<Music>>

	fun getRecentlyAddedSongs(): Flow<List<Music>>

	fun getSongsOnQueue(): Flow<List<Music>>

	fun getSongsFromSearch(query: String): Flow<List<Music>>

	fun getSearchHistory(): Flow<List<String>>

}