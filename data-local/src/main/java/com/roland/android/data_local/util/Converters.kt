package com.roland.android.data_local.util

import com.roland.android.data_local.entity.MusicEntity
import com.roland.android.data_local.entity.PlaylistEntity
import com.roland.android.data_repository.util.Constants.DATE
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.Playlist
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Converters {

	fun MusicEntity.convertToMusic() = Music(
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
		favorite = favorite,
		lastPlayed = lastPlayed,
		timesPlayed = timesPlayed
	)

	fun Music.convertToMusicEntity(isFavorite: Boolean? = null) = MusicEntity(
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
		favorite = isFavorite ?: favorite,
		lastPlayed = lastPlayed,
		timesPlayed = timesPlayed
	)

	fun PlaylistEntity.convertToPlaylist() = Playlist(
		id  = id,
		name = name,
		songs = songs,
		numOfSongs = numOfSongs(),
		dateCreated = dateCreated,
		dateModified = dateModified
	)

	fun Playlist.convertToPlaylistEntity(modified: Boolean = false) = PlaylistEntity(
		id  = id,
		name = name,
		songs = songs,
		dateCreated = dateCreated,
		dateModified = if (modified) Calendar.getInstance().time else dateModified
	)

	fun String.toDate(): Date? {
		val format = SimpleDateFormat(DATE, Locale.getDefault())
		return format.parse(this)
	}

}