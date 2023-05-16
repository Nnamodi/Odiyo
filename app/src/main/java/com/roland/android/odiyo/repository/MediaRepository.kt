package com.roland.android.odiyo.repository

import com.roland.android.odiyo.mediaSource.AlbumsSource
import com.roland.android.odiyo.mediaSource.ArtistsSource
import com.roland.android.odiyo.mediaSource.MediaAccessingObject
import com.roland.android.odiyo.mediaSource.MediaSource
import com.roland.android.odiyo.model.Album
import com.roland.android.odiyo.model.Artist
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.util.SongDetails
import kotlinx.coroutines.flow.MutableStateFlow

class MediaRepository(
	mediaSource: MediaSource,
	albumsSource: AlbumsSource,
	artistsSource: ArtistsSource,
	private val mediaAccessingObject: MediaAccessingObject
) {
	val getAllSongs: MutableStateFlow<List<Music>> = mediaSource.media()

	val getAlbums: MutableStateFlow<List<Album>> = albumsSource.albums()

	val getSongsFromAlbum: (Array<String>) -> MutableStateFlow<List<Music>> =
		{ mediaSource.mediaFromAlbum(selectionArgs = it) }

	val getArtists: MutableStateFlow<List<Artist>> = artistsSource.artists()

	val getSongsFromArtist: (Array<String>) -> MutableStateFlow<List<Music>> =
		{ mediaSource.mediaFromArtist(selectionArgs = it) }

	fun updateSong(songDetails: SongDetails) {
		mediaAccessingObject.updateSong(songDetails)
	}

	fun deleteSong(songDetails: SongDetails) {
		mediaAccessingObject.deleteSong(songDetails)
	}
}