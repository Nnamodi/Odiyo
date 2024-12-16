package com.roland.android.data_system.database

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.roland.android.domain.model.Artist
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ArtistsSource : KoinComponent {
	private val resolver by inject<ContentResolver>()

	// query parameters for artists
	private val artistCollection: Uri =
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			MediaStore.Audio.Artists.getContentUri(
				MediaStore.VOLUME_EXTERNAL
			)
		} else {
			MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
		}
	private val artistProjection = arrayOf(
		MediaStore.Audio.Artists._ID,
		MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
		MediaStore.Audio.Artists.ARTIST
	)
	private val artistSortOrder = "${MediaStore.Audio.Artists.ARTIST} ASC"

	private fun query(): Cursor? = resolver.query(
		artistCollection,
		artistProjection,
		null,
		null,
		artistSortOrder
	)

	fun getArtists(): MutableStateFlow<List<Artist>> {
		val artists = MutableStateFlow<List<Artist>>(mutableListOf())
		query()?.use { cursor ->
			val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
			val numberOfTracksColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
			val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)

			while (cursor.moveToNext()) {
				val id = cursor.getLong(idColumn)
				val numberOfTracks = cursor.getInt(numberOfTracksColumn)
				val albumContent = cursor.getString(artistColumn)
				val contentUri: Uri = ContentUris.withAppendedId(
					MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
					id
				)

				val artist = Artist(contentUri, numberOfTracks, albumContent)
				artists.value += artist
			}
		}
		return artists
	}
}