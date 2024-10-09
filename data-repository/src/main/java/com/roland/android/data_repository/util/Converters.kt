package com.roland.android.data_repository.util

import android.content.Context
import android.graphics.Bitmap
import com.roland.android.data_repository.model.MusicFromSystem
import com.roland.android.data_repository.util.Extensions.date
import com.roland.android.data_repository.util.Extensions.getBitmap
import com.roland.android.data_repository.util.Extensions.time
import com.roland.android.data_repository.util.Extensions.toMb
import com.roland.android.domain.model.Album
import com.roland.android.domain.model.Artist
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object Converters {

	fun MusicFromSystem.convertToMusic() = Music(
		id  = id,
		uri = uri,
		name = name,
		title = title,
		artist = artist,
		album = album,
		path = path,
		addedOn = addedOn.date,
		duration = millis.time,
		size = "${bytes.toMb} MB"
	)

	fun Flow<List<Music>>.includeArtworks(context: Context): Flow<List<Music>> {
		return map { songs ->
			songs.map {
				val artwork = it.getBitmap(context)
				it.includeArtwork(artwork)
			}
		}
	}

	fun Music.includeArtwork(artwork: Bitmap) = Music(
		generatedId = generatedId,
		id  = id,
		uri = uri,
		name = name,
		title = title,
		artist = artist,
		album = album,
		path = path,
		addedOn = addedOn,
		duration = duration,
		size = size,
		artwork = artwork,
		favorite = favorite,
		lastPlayed = lastPlayed,
		timesPlayed = timesPlayed
	)

	fun Album.includeArtwork(artwork: Bitmap) = Album(
		uri = uri,
		numberOfSongs = numberOfSongs,
		album = album,
		artwork = artwork
	)

	fun Artist.includeArtwork(artwork: Bitmap) = Artist(
		uri = uri,
		numberOfTracks = numberOfTracks,
		artist = artist,
		artwork = artwork
	)

	fun Playlist.includeArtwork(artwork: Bitmap) = Playlist(
		id  = id,
		name = name,
		songs = songs,
		numOfSongs = numOfSongs,
		dateCreated = dateCreated,
		dateModified = dateModified,
		artwork = artwork
	)

}