package com.roland.android.data_repository.data_source.system

import com.roland.android.data_repository.model.MusicFromSystem
import com.roland.android.domain.model.Album
import com.roland.android.domain.model.Artist
import kotlinx.coroutines.flow.Flow

interface SystemMusicSource {

	fun getAllSongs(): Flow<List<MusicFromSystem>>

	fun getAlbums(): Flow<List<Album>>

	fun getArtists(): Flow<List<Artist>>

	fun getSongsFromAlbum(albumName: String): Flow<List<MusicFromSystem>>

	fun getSongsFromArtist(artistName: String): Flow<List<MusicFromSystem>>

}