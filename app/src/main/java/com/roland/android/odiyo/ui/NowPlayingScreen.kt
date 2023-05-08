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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.getArtwork
import com.roland.android.odiyo.theme.OdiyoTheme

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
						Icon(imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
					}
				},
				title = {},
				colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent)
			)
		}
	) {
		Column(
			modifier = Modifier.padding(horizontal = 20.dp, vertical = 40.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			MediaDescription(song, artwork)

			MediaControls(
				isPlaying = isPlaying,
				deviceMuted = deviceMuted,
				onShuffle = onShuffle,
				progress = if (progress < 1f) progress else 0.2f,
				totalDuration = song?.duration ?: "00:00",
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
	val screenWidth = LocalConfiguration.current.screenWidthDp

	AsyncImage(
		model = artwork,
		contentDescription = "media thumbnail",
		placeholder = painterResource(R.drawable.default_art),
		modifier = Modifier
			.size((screenWidth / 1.12).dp)
			.padding(vertical = 20.dp),
		contentScale = ContentScale.Crop
	)
	Column(Modifier.fillMaxWidth()) {
		Text(
			text = song?.title ?: "Unknown",
			modifier = Modifier.basicMarquee(),
			style = MaterialTheme.typography.headlineMedium,
			overflow = TextOverflow.Ellipsis,
			softWrap = false,
			fontWeight = FontWeight.Medium
		)
		Text(
			text = song?.artist ?: "Unknown",
			fontSize = TextUnit(18f, TextUnitType.Sp),
			overflow = TextOverflow.Ellipsis,
			softWrap = false,
		)
	}
}

@Composable
private fun MediaControls(
	isPlaying: Boolean,
	deviceMuted: Boolean,
	onShuffle: Boolean,
	progress: Float,
	timeElapsed: String = "1:41",
	totalDuration: String,
	playPause: () -> Unit,
	shuffle: () -> Unit,
	seekTo: (Boolean, Boolean) -> Unit,
	muteDevice: () -> Unit
) {
	var slideValue by remember { mutableStateOf(progress) }

	Slider(
		value = slideValue,
		onValueChange = { slideValue = it },
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 25.dp)
	)
	Row {
		Text(timeElapsed)
		Spacer(Modifier.weight(1f))
		Text(totalDuration)
	}
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 50.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		IconButton(
			onClick = { shuffle() },
			modifier = Modifier.size(50.dp)
		) {
			Icon(
				imageVector = Icons.Rounded.Shuffle,
				contentDescription = "shuffle",
				modifier = Modifier.fillMaxSize(0.75f),
				tint = if (onShuffle) Color.Blue.copy(0.65f) else Color.Black
			)
		}
		IconButton(
			onClick = { seekTo(true, false) },
			modifier = Modifier.size(70.dp)
		) {
			Icon(
				imageVector = Icons.Rounded.SkipPrevious,
				contentDescription = "seek to previous",
				modifier = Modifier.fillMaxSize(0.75f)
			)
		}
		IconButton(
			onClick = { playPause() },
			modifier = Modifier.size(70.dp)
		) {
			Icon(
				imageVector = if (isPlaying) Icons.Rounded.PauseCircleFilled else Icons.Rounded.PlayCircleFilled,
				contentDescription = if (isPlaying) "pause" else "play",
				modifier = Modifier.fillMaxSize()
			)
		}
		IconButton(
			onClick = { seekTo(false, true) },
			modifier = Modifier.size(70.dp)
		) {
			Icon(
				imageVector = Icons.Rounded.SkipNext,
				contentDescription = "seek to next",
				modifier = Modifier.fillMaxSize(0.75f)
			)
		}
		IconButton(
			onClick = { muteDevice() },
			modifier = Modifier.size(50.dp)
		) {
			Icon(
				imageVector = Icons.Rounded.VolumeOff,
				contentDescription = if(deviceMuted) "unmute" else "mute",
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
			progress = 0.25f,
			playPause = { isPlaying = !isPlaying },
			shuffle = { onShuffle = !onShuffle },
			seekTo = { _, _ -> },
			muteDevice = { deviceMuted = !deviceMuted },
			navigateUp = {}
		)
	}
}