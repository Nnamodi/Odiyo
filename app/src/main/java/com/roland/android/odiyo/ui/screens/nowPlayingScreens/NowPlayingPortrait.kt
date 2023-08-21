package com.roland.android.odiyo.ui.screens.nowPlayingScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.roland.android.odiyo.R
import com.roland.android.odiyo.states.NowPlayingUiState
import com.roland.android.odiyo.ui.components.NowPlayingIconButton
import com.roland.android.odiyo.ui.screens.MediaControls
import com.roland.android.odiyo.ui.screens.MediaDescription
import com.roland.android.odiyo.util.MediaControls

@Composable
fun NowPlayingPortraitView(
	paddingValues: PaddingValues,
	uiState: NowPlayingUiState,
	componentColor: Color,
	backgroundColor: Color,
	mediaControl: (MediaControls) -> Unit,
	goToCollection: (String, String) -> Unit,
	openMusicQueue: (Boolean) -> Unit
) {
	val currentSong = if (uiState.musicQueue.isNotEmpty()) {
		uiState.musicQueue[uiState.currentSongIndex]
	} else null

	Column(
		modifier = Modifier
			.padding(paddingValues)
			.padding(start = 30.dp, end = 30.dp, bottom = 10.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.SpaceBetween
	) {
		MediaDescription(
			uiState = uiState, currentSong = currentSong,
			componentColor = componentColor, backgroundColor = backgroundColor,
			portraitView = true, onFavorite = mediaControl, goToCollection = goToCollection
		)

		Spacer(Modifier.weight(1f))

		MediaControls(
			uiState = uiState, currentSong = currentSong,
			backgroundColor = backgroundColor,
			mediaControl = mediaControl, showMusicQueue = openMusicQueue,
		)

		Spacer(Modifier.weight(1f))

		MediaUtilActionsPortrait(
			uiState = uiState, backgroundColor = backgroundColor,
			mediaControl = mediaControl
		)
	}
}

@Composable
fun MediaUtilActionsPortrait(
	uiState: NowPlayingUiState,
	backgroundColor: Color,
	mediaControl: (MediaControls) -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically
	) {
		NowPlayingIconButton(
			onClick = { mediaControl(MediaControls.Mute) },
			modifier = Modifier.size(50.dp),
			toggled = uiState.deviceMuted, color = backgroundColor
		) {
			Icon(
				imageVector = Icons.Rounded.VolumeOff,
				contentDescription = stringResource(if (uiState.deviceMuted) R.string.unmute else R.string.mute),
				modifier = Modifier.fillMaxSize(0.75f)
			)
		}
		Spacer(Modifier.weight(1f))
		NowPlayingIconButton(
			onClick = { mediaControl(MediaControls.RepeatMode) },
			modifier = Modifier.size(50.dp), color = backgroundColor,
			toggled = uiState.repeatMode != Player.REPEAT_MODE_OFF
		) {
			Icon(
				imageVector = if (uiState.repeatMode == Player.REPEAT_MODE_ONE) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
				contentDescription = stringResource(R.string.repeat_mode),
				modifier = Modifier.fillMaxSize(0.75f)
			)
		}
	}
}