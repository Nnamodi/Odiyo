package com.roland.android.data_system.data_source

import android.provider.MediaStore
import com.roland.android.data_repository.data_source.system.SystemMusicSource
import com.roland.android.data_repository.model.MusicFromSystem
import com.roland.android.data_system.database.AlbumsSource
import com.roland.android.data_system.database.ArtistsSource
import com.roland.android.data_system.database.MusicSource
import com.roland.android.domain.model.Album
import com.roland.android.domain.model.Artist
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SystemMusicSourceImpl : SystemMusicSource, KoinComponent {
	private val musicSource by inject<MusicSource>()
	private val albumsSource by inject<AlbumsSource>()
	private val artistsSource by inject<ArtistsSource>()

	private val albumSelection = "${MediaStore.Audio.Media.ALBUM} == ?"
	private val artistSelection = "${MediaStore.Audio.Media.ARTIST} == ?"

	override fun getAllSongs(): Flow<List<MusicFromSystem>> {
		return musicSource.getSongs()
	}

	override fun getAlbums(): Flow<List<Album>> {
		return albumsSource.getAlbums()
	}

	override fun getArtists(): Flow<List<Artist>> {
		return artistsSource.getArtists()
	}

	override fun getSongsFromAlbum(selectionArgs: Array<String>): Flow<List<MusicFromSystem>> {
		return musicSource.getSongs(albumSelection, selectionArgs)
	}

	override fun getSongsFromArtist(selectionArgs: Array<String>): Flow<List<MusicFromSystem>> {
		return musicSource.getSongs(artistSelection, selectionArgs)
	}
}