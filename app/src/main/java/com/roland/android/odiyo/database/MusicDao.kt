package com.roland.android.odiyo.database

import androidx.room.*
import com.roland.android.odiyo.model.Music
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
	@Query("SELECT * FROM music ORDER BY title")
	fun getAllSongs(): Flow<List<Music>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun addSongs(songs: List<Music>)

	@Update
	suspend fun updateSong(music: Music)

	@Delete
	suspend fun deleteSong(music: Music)
}