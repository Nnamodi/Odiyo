package com.roland.android.odiyo.repository

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.roland.android.odiyo.mediaSource.AlbumsSource
import com.roland.android.odiyo.mediaSource.ArtistsSource
import com.roland.android.odiyo.mediaSource.MediaAccessingObject
import com.roland.android.odiyo.mediaSource.MediaSource
import com.roland.android.odiyo.model.Album
import com.roland.android.odiyo.model.Artist
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.util.SongDetails
import kotlinx.coroutines.flow.Flow

@RequiresApi(Build.VERSION_CODES.Q)
class MediaRepository(
	mediaSource: MediaSource,
	albumsSource: AlbumsSource,
	artistsSource: ArtistsSource,
	private val mediaAccessingObject: MediaAccessingObject
) {
	val getAllSongs: Flow<List<Music>> = mediaSource.media()

	val getAlbums: Flow<List<Album>> = albumsSource.albums()

	val getSongsFromAlbum: (Array<String>) -> Flow<List<Music>> =
		{ mediaSource.mediaFromAlbum(selectionArgs = it) }

	val getArtists: Flow<List<Artist>> = artistsSource.artists()

	val getSongsFromArtist: (Array<String>) -> Flow<List<Music>> =
		{ mediaSource.mediaFromArtist(selectionArgs = it) }

	fun updateSong(songDetails: SongDetails) {
		mediaAccessingObject.updateSong(songDetails)
	}

	fun shareSong(context: Context, song: Music) {
		Intent(Intent.ACTION_SEND).apply {
			setDataAndType(song.uri, "audio/*")
			putExtra(Intent.EXTRA_STREAM, song.uri)
		}.also { intent ->
			val chooserIntent = Intent.createChooser(intent, "Send '${song.name}'")
			context.startActivity(chooserIntent)
		}
	}

	fun deleteSong(songDetails: SongDetails) {
		mediaAccessingObject.deleteSong(songDetails)
	}
}