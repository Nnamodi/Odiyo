package com.roland.android.domain.repository

import com.roland.android.domain.model.Album
import com.roland.android.domain.model.Artist
import com.roland.android.domain.model.Music
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

	fun getAllSongs(): Flow<List<Music>>

	fun getAlbums(): Flow<List<Album>>

	fun getArtists(): Flow<List<Artist>>

	fun getSongsFromSearch(query: String): Flow<List<Music>>

	fun getSongsFromAlbum(selectionArgs: Array<String>): Flow<List<Music>>

	fun getSongsFromArtist(selectionArgs: Array<String>): Flow<List<Music>>

}