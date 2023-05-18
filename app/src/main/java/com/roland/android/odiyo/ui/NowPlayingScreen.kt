package com.roland.android.odiyo.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.getArtwork
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun NowPlayingScreen(
	song: Music?,
	artwork: Any?,
	isPlaying: Boolean,
	deviceMuted: Boolean,
	onShuffle: Boolean,
	progress: Float,
	timeElapsed: String,
	onSeekToPosition: (Long) -> Unit,
	playPause: (Uri) -> Unit,
	shuffle: () -> Unit,
	seekTo: (Boolean, Boolean) -> Unit,
	muteDevice: () -> Unit,
	navigateUp: () -> Unit
) {
	Scaffold(
		modifier = Modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				navigationIcon = {
					IconButton(onClick = navigateUp) {
						Icon(imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = stringResource(R.string.back_icon_desc))
					}
				},
				title = {},
				colors = topAppBarColors(containerColor = Color.Transparent)
			)
		}
	) {
		Column(
			modifier = Modifier.padding(horizontal = 30.dp, vertical = 40.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			MediaDescription(song, artwork)

			MediaControls(
				song = song,
				isPlaying = isPlaying,
				deviceMuted = deviceMuted,
				onShuffle = onShuffle,
				progress = progress,
				timeElapsed = timeElapsed,
				onSeekToPosition = onSeekToPosition,
				playPause = { song?.uri?.let { playPause(it) } },
				shuffle = shuffle,
				seekTo = seekTo,
				muteDevice = muteDevice
			)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalFoundationApi::class)
@UnstableApi
@Composable
private fun MediaDescription(song: Music?, artwork: Any?) {
	val screenWidth = LocalConfiguration.current.screenWidthDp - (30 * 2)

	MediaImage(
		modifier = Modifier
			.size(screenWidth.dp, (screenWidth * 1.12).dp)
			.padding(top = 20.dp, bottom = 10.dp),
		artwork = artwork
	)
	Column(Modifier.fillMaxWidth()) {
		Text(
			text = song?.title ?: stringResource(R.string.unknown),
			modifier = Modifier.basicMarquee(),
			style = MaterialTheme.typography.headlineMedium,
			overflow = TextOverflow.Ellipsis,
			softWrap = false,
			fontWeight = FontWeight.Medium
		)
		Text(
			text = song?.artist ?: stringResource(R.string.unknown),
			fontSize = TextUnit(18f, TextUnitType.Sp),
			overflow = TextOverflow.Ellipsis,
			softWrap = false,
		)
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun MediaControls(
	song: Music?,
	isPlaying: Boolean,
	deviceMuted: Boolean,
	onShuffle: Boolean,
	progress: Float,
	timeElapsed: String,
	onSeekToPosition: (Long) -> Unit,
	playPause: () -> Unit,
	shuffle: () -> Unit,
	seekTo: (Boolean, Boolean) -> Unit,
	muteDevice: () -> Unit
) {
	val maxSeekValue = song?.time?.toFloat() ?: 1f

	Slider(
		value = progress,
		onValueChange = { onSeekToPosition(it.toLong()) },
		valueRange = 0f..maxSeekValue,
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 20.dp)
	)
	Row {
		Text(timeElapsed)
		Spacer(Modifier.weight(1f))
		Text(song?.duration ?: "00:00")
	}
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 35.dp),
		horizontalArrangement = Arrangement.SpaceEvenly,
		verticalAlignment = Alignment.CenterVertically
	) {
		IconButton(
			onClick = { shuffle() },
			modifier = Modifier
				.size(50.dp)
				.weight(0.9f)
		) {
			Icon(
				imageVector = Icons.Rounded.Shuffle,
				contentDescription = stringResource(R.string.shuffle),
				modifier = Modifier.fillMaxSize(0.75f),
				tint = if (onShuffle) Color.Blue.copy(0.65f) else Color.Black
			)
		}
		IconButton(
			onClick = { seekTo(true, false) },
			modifier = Modifier
				.size(70.dp)
				.weight(1f)
		) {
			Icon(
				imageVector = Icons.Rounded.SkipPrevious,
				contentDescription = stringResource(R.string.seek_to_previous),
				modifier = Modifier.fillMaxSize(0.75f)
			)
		}
		IconButton(
			onClick = { playPause() },
			modifier = Modifier
				.size(70.dp)
				.weight(1.2f)
		) {
			Icon(
				imageVector = if (isPlaying) Icons.Rounded.PauseCircleFilled else Icons.Rounded.PlayCircleFilled,
				contentDescription = if (isPlaying) stringResource(R.string.pause) else stringResource(R.string.play),
				modifier = Modifier.fillMaxSize()
			)
		}
		IconButton(
			onClick = { seekTo(false, true) },
			modifier = Modifier
				.size(70.dp)
				.weight(1f)
		) {
			Icon(
				imageVector = Icons.Rounded.SkipNext,
				contentDescription = stringResource(R.string.seek_to_next),
				modifier = Modifier.fillMaxSize(0.75f)
			)
		}
		IconButton(
			onClick = { muteDevice() },
			modifier = Modifier
				.size(50.dp)
				.weight(0.9f)
		) {
			Icon(
				imageVector = Icons.Rounded.VolumeOff,
				contentDescription = if(deviceMuted) stringResource(R.string.unmute) else stringResource(R.string.mute),
				modifier = Modifier.fillMaxSize(0.75f),
				tint = if (deviceMuted) Color.Blue.copy(0.65f) else Color.Black
			)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Preview
@Composable
fun NowPlayingPreview() {
	OdiyoTheme {
		var isPlaying by remember { mutableStateOf(false) }
		var deviceMuted by remember { mutableStateOf(false) }
		var onShuffle by remember { mutableStateOf(false) }

		NowPlayingScreen(
			song = previewData[2],
			artwork = previewData[2].getArtwork(),
			isPlaying = isPlaying,
			deviceMuted = deviceMuted,
			onShuffle = onShuffle,
			progress = 0f,
			timeElapsed = "00.00",
			onSeekToPosition = {},
			playPause = { isPlaying = !isPlaying },
			shuffle = { onShuffle = !onShuffle },
			seekTo = { _, _ -> },
			muteDevice = { deviceMuted = !deviceMuted },
			navigateUp = {}
		)
	}
}