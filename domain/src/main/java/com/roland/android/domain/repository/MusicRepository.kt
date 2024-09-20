package com.roland.android.domain.repository

import com.roland.android.domain.model.Album
import com.roland.android.domain.model.Artist
import com.roland.android.domain.model.Music
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

	fun getAllSongs(): Flow<List<Music>>

	fun getLastPlayedSongs(): Flow<List<Music>>

	fun getRecentlyAddedSongs(): Flow<List<Music>>

	fun getFavoriteSongs(): Flow<List<Music>>

	fun getCurrentSong(): Flow<Music>

	fun getAlbums(): Flow<List<Album>>

	fun getArtists(): Flow<List<Artist>>

	fun getSongsFromAlbum(selectionArgs: Array<String>): Flow<List<Music>>

	fun getSongsFromArtist(selectionArgs: Array<String>): Flow<List<Music>>

}