package com.roland.android.data_local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.roland.android.data_local.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
	@Query("SELECT * FROM playlistentity")
	fun getAllPlaylists(): Flow<List<PlaylistEntity>>

	@Query("SELECT * FROM playlistentity WHERE name=(:playlistName)")
	fun getPlaylist(playlistName: String): Flow<PlaylistEntity>

	@Insert
	suspend fun createPlaylist(playlist: PlaylistEntity)

	@Update
	suspend fun updatePlaylist(playlist: PlaylistEntity)

	@Delete
	suspend fun deletePlaylist(playlist: PlaylistEntity)
}