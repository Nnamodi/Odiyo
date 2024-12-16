package com.roland.android.data_local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.roland.android.data_local.entity.MusicEntity
import com.roland.android.data_local.entity.SearchQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
	@Query("SELECT * FROM musicentity WHERE `title` LIKE :query OR `artist` LIKE :query")
	fun getSongsFromSearch(query: String): Flow<List<MusicEntity>>

	@Query("SELECT * FROM searchqueryentity ORDER BY `id` DESC")
	fun getSearchHistory(): Flow<List<SearchQueryEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun addSearchQuery(query: SearchQueryEntity)

	@Query("DELETE FROM searchqueryentity")
	suspend fun clearSearchHistory()
}