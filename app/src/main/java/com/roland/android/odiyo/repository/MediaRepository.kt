package com.roland.android.odiyo.repository

import android.content.ContentResolver
import com.roland.android.odiyo.data.AlbumsSource
import com.roland.android.odiyo.data.MediaSource
import com.roland.android.odiyo.model.Album
import com.roland.android.odiyo.model.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

class MediaRepository(
	scope: CoroutineScope,
	resolver: ContentResolver
) {
	private val mediaSource = MediaSource(scope, resolver)
	private val albumsSource = AlbumsSource(scope, resolver)

	val getAllSongs: MutableStateFlow<List<Music>> = mediaSource.media()

	val getAlbums: MutableStateFlow<List<Album>> = albumsSource.albums()

	val getSongsFromAlbum: (Array<String>) -> MutableStateFlow<List<Music>> =
		{ mediaSource.mediaFromAlbum(selectionArgs = it) }
}