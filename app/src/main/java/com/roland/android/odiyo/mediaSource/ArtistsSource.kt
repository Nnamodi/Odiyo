package com.roland.android.odiyo.mediaSource

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.roland.android.odiyo.model.Artist
import kotlinx.coroutines.flow.MutableStateFlow
import okio.use

class ArtistsSource(
	resolver: ContentResolver
) {
	private val query = resolver.query(
		MediaDetails.artistCollection,
		MediaDetails.artistProjection,
		null,
		null,
		MediaDetails.artistSortOrder
	)

	private val artists = MutableStateFlow<List<Artist>>(mutableListOf())

	fun artists(): MutableStateFlow<List<Artist>> {
		query?.use { cursor ->
				val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
				val numberOfTracksColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
				val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)

				while (cursor.moveToNext()) {
					val id = cursor.getLong(idColumn)
					val numberOfTracks = cursor.getString(numberOfTracksColumn)
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