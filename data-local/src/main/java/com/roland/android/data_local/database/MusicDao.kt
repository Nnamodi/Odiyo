package com.roland.android.data_local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.roland.android.data_local.entity.MusicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
	@Query("SELECT * FROM musicentity ORDER BY title")
	fun getAllSongs(): Flow<List<MusicEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun addNewSongs(songs: List<MusicEntity>)

	@Update
	suspend fun updateSong(music: MusicEntity)

	@Delete
	suspend fun deleteSong(music: MusicEntity)

	@Query("DELETE FROM musicentity")
	suspend fun clearDatabase()
}