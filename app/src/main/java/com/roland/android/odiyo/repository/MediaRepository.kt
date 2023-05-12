package com.roland.android.odiyo.repository

import android.content.ContentResolver
import com.roland.android.odiyo.mediaSource.AlbumsSource
import com.roland.android.odiyo.mediaSource.ArtistsSource
import com.roland.android.odiyo.mediaSource.MediaSource
import com.roland.android.odiyo.model.Album
import com.roland.android.odiyo.model.Artist
import com.roland.android.odiyo.model.Music
import kotlinx.coroutines.flow.MutableStateFlow

class MediaRepository(
	resolver: ContentResolver
) {
	private val mediaSource = MediaSource(resolver)
	private val albumsSource = AlbumsSource(resolver)
	private val artistsSource = ArtistsSource(resolver)

	val getAllSongs: MutableStateFlow<List<Music>> = mediaSource.media()

	val getAlbums: MutableStateFlow<List<Album>> = albumsSource.albums()

	val getSongsFromAlbum: (Array<String>) -> MutableStateFlow<List<Music>> =
		{ mediaSource.mediaFromAlbum(selectionArgs = it) }

	val getArtists: MutableStateFlow<List<Artist>> = artistsSource.artists()

	val getSongsFromArtist: (Array<String>) -> MutableStateFlow<List<Music>> =
		{ mediaSource.mediaFromArtist(selectionArgs = it) }
}