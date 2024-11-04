package com.roland.android.odiyo.ui.screens.nowPlayingScreens

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.roland.android.domain.model.Music
import com.roland.android.domain.repository.MusicQueueRepository
import com.roland.android.domain.repository.MusicUtilRepository
import com.roland.android.domain.repository.PlayerRepository
import com.roland.android.domain.usecase.GetPlaylistsUseCase
import com.roland.android.domain.usecase.GetSongsOnQueueUseCase
import com.roland.android.odiyo.service.Util.mediaItems
import com.roland.android.odiyo.util.MediaControls
import com.roland.android.player.player_utils.States.currentDuration
import com.roland.android.player.player_utils.States.currentMediaItemIndex
import com.roland.android.player.player_utils.States.isPlaying
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NowPlayingViewModel : ViewModel(), KoinComponent {
	private val getSongsOnQueueUseCase by inject<GetSongsOnQueueUseCase>()
	private val musicQueueRepository by inject<MusicQueueRepository>()
	private val musicUtilRepository by inject<MusicUtilRepository>()
	private val playerRepository by inject<PlayerRepository>()
	private val getPlaylistsUseCase by inject<GetPlaylistsUseCase>()

	private var canAccessStorage by mutableStateOf(false)
	private var updateProgress = true

	private val _nowPlayingUiState = MutableStateFlow(NowPlayingUiState())
	var nowPlayingUiState by mutableStateOf(_nowPlayingUiState.value); private set

	init {
		musicQueueRepository.restorePlaylistDetails()

		viewModelScope.launch {
			combine(
				playerRepository.getShuffleState(),
				playerRepository.getRepeatMode(),
				getPlaylistsUseCase()
			) { shuffleState, repeatMode, playlists ->
				_nowPlayingUiState.update {
					it.copy(
						shuffleState = shuffleState.state,
						repeatMode = repeatMode,
						playlists = playlists
					)
				}
			}
		}
		viewModelScope.launch {
			combine(
				getSongsOnQueueUseCase(),
				currentMediaItemIndex,
				musicUtilRepository.getCurrentPlaylistDetails()
			) { musicQueue, songIndex, nowPlayingFrom ->
				_nowPlayingUiState.update {
					it.copy(
						musicQueue = musicQueue,
						currentSongIndex = songIndex,
						nowPlayingFrom = nowPlayingFrom
					)
				}
			}
		}
		viewModelScope.launch {
			isPlaying.collect { state ->
				_nowPlayingUiState.update { it.copy(isPlaying = state) }
				updateProgress()
			}
		}
		viewModelScope.launch {
			_nowPlayingUiState.collect {
				nowPlayingUiState = it
			}
		}
	}

	private fun updateProgress(): Boolean {
		return Handler(Looper.getMainLooper()).postDelayed({
			_nowPlayingUiState.update {
				it.copy(
					currentDuration = currentDuration.value.time,
					seekProgress = currentDuration.value.toFloat()
				)
			}
			if (updateProgress) updateProgress()
		}, 100L)
	}

	fun mediaControl(action: MediaControls) {
		if (!canAccessStorage) return
		when (action) {
			is MediaControls.PlayPause -> playPause()
			is MediaControls.Favorite -> onFavoriteSong(action.song)
			MediaControls.RepeatMode -> setRepeatMode()
			is MediaControls.Seek -> onSeek(action.previous, action.next)
			is MediaControls.SeekToPosition -> onSeekToPosition(action.position)
			is MediaControls.Share -> onShareSong(listOf(action.music))
			MediaControls.Shuffle -> onShuffle()
			MediaControls.Mute -> onMuteDevice()
		}
//		updateMusicQueue(queueEdited = false)
	}

	private fun playPause() {
		playerRepository.playPause()
	}

	private fun onFavoriteSong(song: Music) {
		musicUtilRepository.favoriteSong(song, !song.favorite)
	}

	private fun onSeek(previous: Boolean, next: Boolean) {
		playerRepository.seek(previous, next)
	}

	private fun onSeekToPosition(position: Long) {
		playerRepository.onSeekToPosition(position)
	}

	private fun onShareSong(songs: List<Music>) {
		musicUtilRepository.shareSongs(songs)
	}

	private fun setRepeatMode() {
		viewModelScope.launch {
			val mode = when (nowPlayingUiState.repeatMode) {
				Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
				Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
				else -> Player.REPEAT_MODE_OFF
			}
			playerRepository.setRepeatMode(mode)
				.collect { repeatMode ->
					_nowPlayingUiState.update {
						it.copy(repeatMode = repeatMode)
					}
				}
		}
	}

	private fun onShuffle() {
		viewModelScope.launch(Dispatchers.IO) {
			val randomSeed = (0..mediaItems.value.size).random()
			playerRepository.onShuffle(nowPlayingUiState.shuffleState, randomSeed)
				.collect { shuffleState ->
					_nowPlayingUiState.update {
						it.copy(shuffleState = shuffleState.state)
					}
				}
		}
	}

	private fun onMuteDevice() {
		playerRepository.onMuteDevice()
	}

	override fun onCleared() {
		super.onCleared()
		updateProgress = false
	}
}