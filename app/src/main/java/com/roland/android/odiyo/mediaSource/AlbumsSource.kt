package com.roland.android.odiyo.mediaSource

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.roland.android.odiyo.model.Album
import kotlinx.coroutines.flow.MutableStateFlow
import okio.use

class AlbumsSource(
	private val resolver: ContentResolver
) {
	private fun query(): Cursor? = resolver.query(
		MediaDetails.albumCollection,
		MediaDetails.albumProjection,
		null,
		null,
		MediaDetails.albumSortOrder
	)

	private val albums = MutableStateFlow<List<Album>>(mutableListOf())

	fun albums(): MutableStateFlow<List<Album>> {
		// empty list of albums previously fetched and re-fetch
		albums.value = mutableListOf()
		query()?.use { cursor ->
			val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
			val numberOfSongsColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
			val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)

			while (cursor.moveToNext()) {
				val id = cursor.getLong(idColumn)
				val numberOfSongs = cursor.getInt(numberOfSongsColumn)
				val albumContent = cursor.getString(albumColumn)

				val contentUri: Uri = ContentUris.withAppendedId(
					MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
					id
				)

				val album = Album(contentUri, numberOfSongs, albumContent)
				albums.value += album
			}
		}
		return albums
	}
}