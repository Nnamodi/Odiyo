package com.roland.android.odiyo.ui.screens.nowPlayingScreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.ui.screens.MediaControls
import com.roland.android.odiyo.ui.screens.MediaDescription
import com.roland.android.odiyo.util.MediaControls

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun NowPlayingPortraitView(
	paddingValues: PaddingValues,
	song: Music?,
	artwork: Any?,
	componentColor: Color,
	isPlaying: Boolean,
	shuffleState: Boolean,
	progress: Float,
	timeElapsed: String,
	deviceMuted: Boolean,
	mediaControl: (MediaControls) -> Unit,
	goToCollection: (String, String) -> Unit,
	openMusicQueue: (Boolean) -> Unit,
	openDetailsDialog: (Boolean) -> Unit
) {
	Column(
		modifier = Modifier
			.padding(paddingValues)
			.padding(start = 30.dp, end = 30.dp, bottom = 10.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.SpaceBetween
	) {
		MediaDescription(
			song = song,
			artwork = artwork,
			componentColor = componentColor,
			portraitView = true,
			onFavorite = mediaControl,
			goToCollection = goToCollection
		)

		Spacer(Modifier.weight(1f))

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

		Spacer(Modifier.weight(1f))

		MediaUtilActionsPortrait(
			song = song,
			deviceMuted = deviceMuted,
			componentColor = componentColor,
			mediaControl = mediaControl,
			openDetailsDialog = openDetailsDialog
		)
	}
}

@Composable
fun MediaUtilActionsPortrait(
	song: Music?,
	deviceMuted: Boolean,
	componentColor: Color,
	mediaControl: (MediaControls) -> Unit,
	openDetailsDialog: (Boolean) -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceEvenly,
		verticalAlignment = Alignment.CenterVertically
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
	}
}