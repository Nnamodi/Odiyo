package com.roland.android.odiyo.ui.screens.nowPlayingScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.roland.android.odiyo.R
import com.roland.android.odiyo.service.Util.getBitmap
import com.roland.android.odiyo.states.NowPlayingUiState
import com.roland.android.odiyo.ui.components.MediaImage
import com.roland.android.odiyo.ui.components.NowPlayingIconButton
import com.roland.android.odiyo.ui.screens.MediaControls
import com.roland.android.odiyo.ui.screens.MediaDescription
import com.roland.android.odiyo.util.MediaControls

@Composable
fun NowPlayingLandscapeView(
	paddingValues: PaddingValues,
	uiState: NowPlayingUiState,
	componentColor: Color,
	backgroundColor: Color,
	mediaControl: (MediaControls) -> Unit,
	goToCollection: (String, String) -> Unit,
	openMusicQueue: (Boolean) -> Unit
) {
	val imageSize = LocalConfiguration.current.screenWidthDp * 0.35
	val inMultiWindowMode = false//rememberWindowSize().width == WindowType.Portrait
	val currentSong = uiState.musicQueue.getOrNull(uiState.currentSongIndex)
	val context = LocalContext.current
	val artwork by remember(currentSong) { mutableStateOf(currentSong?.getBitmap(context)) }
	var songIsFavorite by remember { mutableStateOf(currentSong?.favorite == true) }
	songIsFavorite = currentSong?.favorite == true

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(paddingValues)
			.padding(start = 30.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		if (!inMultiWindowMode) {
			MediaImage(
				modifier = Modifier
					.size(imageSize.dp)
					.padding(end = 14.dp),
				artwork = artwork
			)
		}

		Column(
			modifier = Modifier
				.fillMaxHeight()
				.padding(bottom = 10.dp)
				.weight(1f),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.SpaceEvenly
		) {
			Row {
				if (inMultiWindowMode) {
					MediaImage(
						modifier = Modifier
							.size(imageSize.dp)
							.padding(end = 14.dp),
						artwork = artwork
					)
				}

				MediaDescription(
					currentSong = currentSong, componentColor = componentColor,
					backgroundColor = backgroundColor, portraitView = false,
					onFavorite = mediaControl, goToCollection = goToCollection
				)
			}

			MediaControls(
				uiState = uiState, currentSong = currentSong,
				backgroundColor = backgroundColor,
				mediaControl = mediaControl, showMusicQueue = openMusicQueue,
			)
		}

		MediaUtilActionsLandscape(
			uiState = uiState, backgroundColor = backgroundColor,
			mediaControl = mediaControl
		)
	}
}

@Composable
fun MediaUtilActionsLandscape(
	uiState: NowPlayingUiState,
	backgroundColor: Color,
	mediaControl: (MediaControls) -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxHeight()
			.padding(horizontal = 10.dp),
		horizontalAlignment = Alignment.CenterHorizontally
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