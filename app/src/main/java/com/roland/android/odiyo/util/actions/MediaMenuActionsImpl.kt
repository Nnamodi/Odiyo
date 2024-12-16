package com.roland.android.odiyo.util.actions

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.NowPlayingFrom
import com.roland.android.domain.model.Playlist
import com.roland.android.domain.repository.MusicQueueRepository
import com.roland.android.domain.repository.MusicRepository
import com.roland.android.domain.repository.MusicUtilRepository
import com.roland.android.domain.repository.PlayerRepository
import com.roland.android.domain.repository.PlaylistUtilRepository
import com.roland.android.domain.util.SortOptions
import com.roland.android.player.player_utils.States.mediaItemsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar

class MediaMenuActionsImpl : KoinComponent {
	private val musicRepository by inject<MusicRepository>()
	private val musicQueueRepository by inject<MusicQueueRepository>()
	private val musicUtilRepository by inject<MusicUtilRepository>()
	private val playerRepository by inject<PlayerRepository>()
	private val playlistUtilRepository by inject<PlaylistUtilRepository>()
	private val coroutineScope by inject<CoroutineScope>()
	private var allSongs by mutableStateOf<List<Music>>(emptyList())

	init {
		coroutineScope.launch {
			musicRepository.getAllSongs().collect {
				allSongs = it
			}
		}
	}

	fun actions(action: MediaMenuActions) {
		when (action) {
			is MediaMenuActions.PlayAudio -> onPlayAudio(action.uri, action.index, action.songsToPlay, action.nowPlayingFrom)
			is MediaMenuActions.PlayNext -> onPlayNext(action.songs, action.collectionName, action.collectionType)
			is MediaMenuActions.AddToQueue -> onAddToQueue(action.songs, action.collectionName, action.collectionType)
			is MediaMenuActions.CreatePlaylist -> onCreatePlaylist(action.playlist)
			is MediaMenuActions.AddToPlaylist -> onAddToPlaylist(action.songs, action.playlist)
			is MediaMenuActions.RemoveFromPlaylist -> onRemoveFromPlaylist(action.songs, action.playlist)
			is MediaMenuActions.DeleteSongs -> onDeleteSongs(action.songs)
			is MediaMenuActions.Favorite -> onFavorite(action.song)
			is MediaMenuActions.RenameSong -> onRenameSong(action.details)
			is MediaMenuActions.SetAsRingtone -> onSetAsRingtone(action.music, action.ringType)
			is MediaMenuActions.ShareSong -> onShareSongs(action.songs)
			is MediaMenuActions.SortSongs -> onSortSongs(action.sortOptions)
		}
	}

	private fun onPlayAudio(
		uri: Uri,
		index: Int,
		songsToPlay: List<Music>,
		nowPlayingFrom: NowPlayingFrom
	) {
		playerRepository.playSong(uri, index, songsToPlay, nowPlayingFrom)
	}

	private fun onPlayNext(
		songs: List<Music>,
		collectionName: String?,
		collectionType: String?
	) {
		if (collectionName == null || collectionType == null) {
			musicQueueRepository.playNext(songs, null)
			return
		}
		val nowPlayingFrom = NowPlayingFrom(collectionName, collectionType).takeIf {
			songs.size > mediaItemsFlow.value.size
		}
		musicQueueRepository.playNext(songs, nowPlayingFrom)
	}

	private fun onAddToQueue(
		songs: List<Music>,
		collectionName: String?,
		collectionType: String?
	) {
		if (collectionName == null || collectionType == null) {
			musicQueueRepository.addToQueue(songs, null)
			return
		}
		val nowPlayingFrom = NowPlayingFrom(collectionName, collectionType).takeIf {
			songs.size > mediaItemsFlow.value.size
		}
		musicQueueRepository.addToQueue(songs, nowPlayingFrom)
	}

	private fun onCreatePlaylist(playlist: Playlist) {
		playlistUtilRepository.createPlaylist(playlist)
	}

	private fun onAddToPlaylist(songs: List<Music>, playlist: Playlist) {
		val songsUri = songs.map { it.uri }
		val updatedPlaylist = Playlist(
			id = playlist.id,
			name = playlist.name,
			songs = playlist.songs + songsUri,
			dateModified = Calendar.getInstance().time
		)
		playlistUtilRepository.updatePlaylist(updatedPlaylist)
	}

	private fun onRemoveFromPlaylist(songs: List<Music>, playlist: Playlist) {
		val songsUri = songs.map { it.uri }
		val updatedPlaylist = Playlist(
			id = playlist.id,
			name = playlist.name,
			songs = playlist.songs - songsUri.toSet(),
			dateModified = Calendar.getInstance().time
		)
		playlistUtilRepository.updatePlaylist(updatedPlaylist)
	}

	private fun onDeleteSongs(songs: List<Music>) {
		songs.forEach {
			musicUtilRepository.deleteSong(it)
		}
	}

	private fun onFavorite(song: Music) {
		musicUtilRepository.favoriteSong(song, !song.favorite)
	}

	private fun onRenameSong(song: SongDetails) {
		songToRename(song)?.let {
			musicUtilRepository.renameSong(it)
		}
	}

	private fun onSetAsRingtone(song: Music, ringType: Int) {
		musicUtilRepository.setAsRingtone(song, ringType)
	}

	private fun onShareSongs(songs: List<Music>) {
		musicUtilRepository.shareSongs(songs)
	}

	private fun onSortSongs(sortOption: SortOptions) {
		musicUtilRepository.toggleSortOption(sortOption)
	}

	private fun songToRename(newSongDetails: SongDetails): Music? {
		val songToRename = allSongs.find { it.id == newSongDetails.id }
		return songToRename?.let {
			Music(
				generatedId = it.generatedId,
				id = it.id,
				uri = it.uri,
				name = it.name,
				title = newSongDetails.title ?: it.title,
				artist = newSongDetails.artist ?: it.artist,
				album = it.album,
				path = it.path,
				addedOn = it.addedOn,
				duration = it.duration,
				size = it.size,
				artwork = it.artwork,
				favorite = it.favorite,
				lastPlayed = it.lastPlayed,
				timesPlayed = it.timesPlayed
			)
		}
	}
}