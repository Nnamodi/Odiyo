package com.roland.android.odiyo.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.AlbumsSource
import com.roland.android.odiyo.mediaSource.ArtistsSource
import com.roland.android.odiyo.mediaSource.MediaAccessingObject
import com.roland.android.odiyo.mediaSource.MediaSource
import com.roland.android.odiyo.model.Album
import com.roland.android.odiyo.model.Artist
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.model.MusicFromSystem
import com.roland.android.odiyo.util.SongDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class MediaRepository(
	private val mediaSource: MediaSource,
	private val albumsSource: AlbumsSource,
	private val artistsSource: ArtistsSource,
	private val mediaAccessingObject: MediaAccessingObject
) {
	fun getSongsFromSystem(): MutableStateFlow<MutableList<MusicFromSystem>> = mediaSource.media()

	fun getAlbums(): Flow<List<Album>> = albumsSource.albums()

	val getSongsFromAlbum: (Array<String>) -> MutableStateFlow<MutableList<MusicFromSystem>> =
		{ mediaSource.mediaFromAlbum(selectionArgs = it) }

	fun getArtists(): Flow<List<Artist>> = artistsSource.artists()

	val getSongsFromArtist: (Array<String>) -> MutableStateFlow<MutableList<MusicFromSystem>> =
		{ mediaSource.mediaFromArtist(selectionArgs = it) }

	fun updateSongInSystem(songDetails: SongDetails) {
		val renamedSong = getSongsFromSystem().value.find { it.id == songDetails.id }
		renamedSong?.let {
			val inAlbum = getSongsFromAlbum(arrayOf(it.album)).value
			val inArtist = getSongsFromArtist(arrayOf(it.artist)).value
			getSongsFromAlbum(arrayOf(it.album)).value[inAlbum.indexOf(it)] = it
			getSongsFromArtist(arrayOf(it.artist)).value[inArtist.indexOf(it)] = it
		}
		mediaAccessingObject.updateSong(songDetails)
	}

	fun shareSong(context: Context, songs: List<Music>) {
		val uris: ArrayList<Uri> = arrayListOf()
		songs.forEach { uris += it.uri }
		Intent(Intent.ACTION_SEND_MULTIPLE).apply {
			type = "audio/*"
			putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
		}.also { intent ->
			val title = if (songs.size > 1) context.getString(R.string.songs) else songs[0].name
			val chooserIntent = Intent.createChooser(intent, context.getString(R.string.send_audio_file, title))
			context.startActivity(chooserIntent)
		}
	}

	fun deleteSongFromSystem(songDetails: SongDetails) {
		val songToDelete = getSongsFromSystem().value.find { it.id == songDetails.id }
		songToDelete?.let {
			getSongsFromAlbum(arrayOf(it.album)).value.remove(it)
			getSongsFromArtist(arrayOf(it.artist)).value.remove(it)
		}
		mediaAccessingObject.deleteSong(songDetails)
	}
}