package com.roland.android.odiyo.ui.screens.nowPlayingScreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.states.NowPlayingUiState
import com.roland.android.odiyo.ui.components.MediaImage
import com.roland.android.odiyo.ui.screens.MediaControls
import com.roland.android.odiyo.ui.screens.MediaDescription
import com.roland.android.odiyo.ui.theme.color.CustomColors.componentColor
import com.roland.android.odiyo.util.MediaControls

@RequiresApi(Build.VERSION_CODES.Q)
@androidx.annotation.OptIn(UnstableApi::class)
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
	val currentSong = if (uiState.musicQueue.isNotEmpty()) {
		uiState.musicQueue[uiState.currentSongIndex]
	} else null
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
				artwork = uiState.artwork
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
						artwork = uiState.artwork
					)
				}

				MediaDescription(
					uiState = uiState, currentSong = currentSong,
					componentColor = componentColor, backgroundColor = backgroundColor,
					portraitView = false, onFavorite = mediaControl, goToCollection = goToCollection
				)
			}

			MediaControls(
				uiState = uiState, currentSong = currentSong,
				componentColor = componentColor, backgroundColor = backgroundColor,
				mediaControl = mediaControl, showMusicQueue = openMusicQueue,
			)
		}

		MediaUtilActionsLandscape(
			uiState = uiState, componentColor = componentColor,
			backgroundColor = backgroundColor, mediaControl = mediaControl
		)
	}
}

@Composable
fun MediaUtilActionsLandscape(
	uiState: NowPlayingUiState,
	componentColor: Color,
	backgroundColor: Color,
	mediaControl: (MediaControls) -> Unit
) {
	val toggleableComponentColor = componentColor(
		generatedColor = backgroundColor, componentIsToggleable = true
	)

	Column(
		modifier = Modifier
			.fillMaxHeight()
			.padding(horizontal = 10.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		IconButton(
			onClick = { mediaControl(MediaControls.Mute) },
			modifier = Modifier.size(50.dp)
		) {
			Icon(
				imageVector = Icons.Rounded.VolumeOff,
				contentDescription = stringResource(if (uiState.deviceMuted) R.string.unmute else R.string.mute),
				modifier = Modifier.fillMaxSize(0.75f),
				tint = if (uiState.deviceMuted) toggleableComponentColor else componentColor
			)
		}
		Spacer(Modifier.weight(1f))
		IconButton(
			onClick = { mediaControl(MediaControls.RepeatMode) },
			modifier = Modifier.size(50.dp)
		) {
			Icon(
				imageVector = if (uiState.repeatMode == Player.REPEAT_MODE_ONE) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
				contentDescription = stringResource(R.string.repeat_mode),
				modifier = Modifier.fillMaxSize(0.75f),
				tint = if (uiState.repeatMode == Player.REPEAT_MODE_OFF) componentColor else toggleableComponentColor
			)
		}
	}
}