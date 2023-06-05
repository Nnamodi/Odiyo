package com.roland.android.odiyo.database

import androidx.room.*
import com.roland.android.odiyo.model.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
	@Query("SELECT * FROM playlist")
	fun getAllPlaylists(): Flow<List<Playlist>>

	@Insert
	suspend fun createPlaylist(playlist: Playlist)

	@Update
	suspend fun updatePlaylist(playlist: Playlist)

	@Delete
	suspend fun deletePlaylist(playlist: Playlist)
}