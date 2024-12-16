package com.roland.android.data_system.database

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.roland.android.domain.model.Album
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlbumsSource : KoinComponent {
	private val resolver by inject<ContentResolver>()

	// query parameters for albums
	private val albumCollection: Uri =
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			MediaStore.Audio.Albums.getContentUri(
				MediaStore.VOLUME_EXTERNAL
			)
		} else {
			MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
		}
	private val albumProjection = arrayOf(
		MediaStore.Audio.Albums._ID,
		MediaStore.Audio.Albums.NUMBER_OF_SONGS,
		MediaStore.Audio.Albums.ALBUM
	)
	private val albumSortOrder = "${MediaStore.Audio.Albums.ALBUM} ASC"

	private fun query(): Cursor? = resolver.query(
		albumCollection,
		albumProjection,
		null,
		null,
		albumSortOrder
	)

	fun getAlbums(): MutableStateFlow<List<Album>> {
		val albums = MutableStateFlow<List<Album>>(mutableListOf())
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