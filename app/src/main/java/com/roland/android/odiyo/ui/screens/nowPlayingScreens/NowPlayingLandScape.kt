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
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.ui.components.MediaImage
import com.roland.android.odiyo.ui.screens.MediaControls
import com.roland.android.odiyo.ui.screens.MediaDescription
import com.roland.android.odiyo.util.MediaControls

@RequiresApi(Build.VERSION_CODES.Q)
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun NowPlayingLandscapeView(
	paddingValues: PaddingValues,
	song: Music?,
	artwork: Any?,
	componentColor: Color,
	isPlaying: Boolean,
	repeatMode: Int,
	shuffleState: Boolean,
	progress: Float,
	timeElapsed: String,
	deviceMuted: Boolean,
	mediaControl: (MediaControls) -> Unit,
	goToCollection: (String, String) -> Unit,
	openMusicQueue: (Boolean) -> Unit,
	openDetailsDialog: (Boolean) -> Unit
) {
	val imageSize = LocalConfiguration.current.screenWidthDp * 0.35
	val inMultiWindowMode = false//rememberWindowSize().width == WindowType.Portrait
	var songIsFavorite by remember { mutableStateOf(song?.favorite == true) }
	songIsFavorite = song?.favorite == true

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
					song = song, artwork = artwork,
					componentColor = componentColor, portraitView = false,
					goToCollection = goToCollection, onFavorite = mediaControl
				)
			}

			MediaControls(
				song = song,
				isPlaying = isPlaying,
				shuffleState = shuffleState,
				progress = progress,
				timeElapsed = timeElapsed,
				componentColor = componentColor,
				mediaControl = mediaControl,
				showMusicQueue = openMusicQueue,
			)
		}

		MediaUtilActionsLandscape(
			song = song,
			deviceMuted = deviceMuted,
			repeatMode = repeatMode,
			componentColor = componentColor,
			mediaControl = mediaControl,
			openDetailsDialog = openDetailsDialog
		)
	}
}

@Composable
fun MediaUtilActionsLandscape(
	song: Music?,
	deviceMuted: Boolean,
	repeatMode: Int,
	componentColor: Color,
	mediaControl: (MediaControls) -> Unit,
	openDetailsDialog: (Boolean) -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxHeight()
			.padding(horizontal = 10.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.SpaceEvenly
	) {
		IconButton(
			onClick = { song?.let { mediaControl(MediaControls.Share(it)) } },
			modifier = Modifier
				.size(50.dp)
				.weight(1f)
		) {
			Icon(
				imageVector = Icons.Rounded.Share,
				contentDescription = stringResource(R.string.share),
				modifier = Modifier.fillMaxSize(0.75f),
				tint = componentColor
			)
		}
		IconButton(
			onClick = { openDetailsDialog(true) },
			modifier = Modifier
				.size(50.dp)
				.weight(1f)
		) {
			Icon(
				imageVector = Icons.Rounded.Info,
				contentDescription = stringResource(R.string.details),
				modifier = Modifier.fillMaxSize(0.75f),
				tint = componentColor
			)
		}
		IconButton(
			onClick = { mediaControl(MediaControls.Mute) },
			modifier = Modifier
				.size(50.dp)
				.weight(1f)
		) {
			Icon(
				imageVector = Icons.Rounded.VolumeOff,
				contentDescription = stringResource(if (deviceMuted) R.string.unmute else R.string.mute),
				modifier = Modifier.fillMaxSize(0.75f),
				tint = if (deviceMuted) MaterialTheme.colorScheme.primary else componentColor
			)
		}
		IconButton(
			onClick = { mediaControl(MediaControls.RepeatMode) },
			modifier = Modifier
				.size(50.dp)
				.weight(1f)
		) {
			Icon(
				imageVector = if (repeatMode == Player.REPEAT_MODE_ONE) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
				contentDescription = stringResource(R.string.repeat_mode),
				modifier = Modifier.fillMaxSize(0.75f),
				tint = if (repeatMode == Player.REPEAT_MODE_OFF) componentColor else MaterialTheme.colorScheme.primary
			)
		}
	}
}