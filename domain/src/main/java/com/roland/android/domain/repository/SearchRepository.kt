package com.roland.android.domain.repository

import com.roland.android.domain.model.Music
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

	fun getSongsFromSearch(query: String): Flow<List<Music>>

	fun getSearchHistory(): Flow<List<String>>

}