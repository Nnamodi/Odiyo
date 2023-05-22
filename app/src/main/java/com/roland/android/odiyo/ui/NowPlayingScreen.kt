package com.roland.android.odiyo.ui

import android.annotation.SuppressLint
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.getArtwork
import com.roland.android.odiyo.ui.components.MediaImage
import com.roland.android.odiyo.ui.dialog.SongDetailsDialog
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaControls
import com.roland.android.odiyo.util.QueueItemActions

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
	shuffleState: Boolean,
	progress: Float,
	timeElapsed: String,
	currentSongIndex: Int,
	musicQueue: List<Music>,
	mediaControl: (MediaControls) -> Unit,
	queueAction: (QueueItemActions) -> Unit,
	navigateUp: () -> Unit
) {
	val scaffoldState = rememberModalBottomSheetState(true)
	val openMusicQueue = remember { mutableStateOf(false) }
	val openDetailsDialog = remember { mutableStateOf(false) }

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
			modifier = Modifier.padding(start = 30.dp, top = 40.dp, end = 30.dp, bottom = 10.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			MediaDescription(song, artwork)

			MediaControls(
				song = song,
				isPlaying = isPlaying,
				shuffleState = shuffleState,
				progress = progress,
				timeElapsed = timeElapsed,
				mediaControl = mediaControl,
				showMusicQueue = { openMusicQueue.value = it },
			)

			Spacer(Modifier.weight(1f))

			MediaUtilActions(
				song = song,
				deviceMuted = deviceMuted,
				mediaControl = mediaControl,
				openDetailsDialog = { openDetailsDialog.value = it }
			)
		}
	}

	if (openMusicQueue.value) {
		QueueItemsSheet(
			songs = musicQueue,
			currentSongIndex = currentSongIndex,
			scaffoldState = scaffoldState,
			openBottomSheet = { openMusicQueue.value = it },
			queueAction = queueAction
		)
	}

	if (openDetailsDialog.value && song != null) {
		SongDetailsDialog(song, artwork) { openDetailsDialog.value = it }
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
			softWrap = false
		)
		Text(
			text = song?.artist ?: stringResource(R.string.unknown),
			style = MaterialTheme.typography.titleMedium,
			overflow = TextOverflow.Ellipsis,
			softWrap = false
		)
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun MediaControls(
	song: Music?,
	isPlaying: Boolean,
	shuffleState: Boolean,
	progress: Float,
	timeElapsed: String,
	mediaControl: (MediaControls) -> Unit,
	showMusicQueue: (Boolean) -> Unit
) {
	val maxSeekValue = song?.time?.toFloat() ?: 1f

	Slider(
		value = progress,
		onValueChange = { mediaControl(MediaControls.SeekToPosition(it.toLong())) },
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
			onClick = { mediaControl(MediaControls.Shuffle) },
			modifier = Modifier
				.size(50.dp)
				.weight(0.9f)
		) {
			Icon(
				imageVector = Icons.Rounded.Shuffle,
				contentDescription = stringResource(R.string.shuffle),
				modifier = Modifier.fillMaxSize(0.75f),
				tint = if (shuffleState) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
			)
		}
		IconButton(
			onClick = { mediaControl(MediaControls.Seek(previous = true, next = false)) },
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
			onClick = { mediaControl(MediaControls.PlayPause) },
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
			onClick = { mediaControl(MediaControls.Seek(previous = false, next = true)) },
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
			onClick = { showMusicQueue(true) },
			modifier = Modifier
				.size(50.dp)
				.weight(0.9f)
		) {
			Icon(
				imageVector = Icons.Rounded.QueueMusic,
				contentDescription = stringResource(R.string.music_queue),
				modifier = Modifier.fillMaxSize(0.75f)
			)
		}
	}
}

@Composable
fun MediaUtilActions(
	song: Music?,
	deviceMuted: Boolean,
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
				modifier = Modifier.fillMaxSize(0.75f)
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
				modifier = Modifier.fillMaxSize(0.75f)
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
				contentDescription = stringResource(R.string.mute),
				modifier = Modifier.fillMaxSize(0.75f),
				tint = if (deviceMuted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
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
		var shuffleState by remember { mutableStateOf(false) }

		NowPlayingScreen(
			song = previewData[2],
			artwork = previewData[2].getArtwork(),
			isPlaying = isPlaying,
			deviceMuted = deviceMuted,
			shuffleState = shuffleState,
			progress = 0f,
			timeElapsed = "00.00",
			currentSongIndex = 5,
			musicQueue = previewData,
			mediaControl = {
				when (it) {
					MediaControls.Mute -> deviceMuted = !deviceMuted
					MediaControls.PlayPause -> isPlaying = !isPlaying
					MediaControls.Shuffle -> shuffleState = !shuffleState
					else -> {}
				}
			},
			queueAction = {},
			navigateUp = {}
		)
	}
}