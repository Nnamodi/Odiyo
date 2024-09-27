package com.roland.android.data_repository.repository

import com.roland.android.data_repository.data_source.local.LocalMusicSource
import com.roland.android.data_repository.data_util.local.LocalMusicUtil
import com.roland.android.domain.model.Music
import com.roland.android.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SearchRepositoryImpl : SearchRepository, KoinComponent {
	private val localMusicSource by inject<LocalMusicSource>()
	private val localMusicUtil by inject<LocalMusicUtil>()

	override fun getSongsFromSearch(query: String): Flow<List<Music>> {
		return localMusicSource.getSongsFromSearch(query)
	}

	override fun getSearchHistory(): Flow<List<String>> {
		return localMusicSource.getSearchHistory()
	}
}