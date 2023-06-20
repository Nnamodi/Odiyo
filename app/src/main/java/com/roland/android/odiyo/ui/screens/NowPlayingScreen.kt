package com.roland.android.odiyo.ui.screens

import android.annotation.SuppressLint
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.media3.common.util.UnstableApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.getBitmap
import com.roland.android.odiyo.ui.components.MediaImage
import com.roland.android.odiyo.ui.components.NowPlayingTopAppBar
import com.roland.android.odiyo.ui.dialog.SongDetailsDialog
import com.roland.android.odiyo.ui.navigation.ARTISTS
import com.roland.android.odiyo.ui.sheets.QueueItemsSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.ui.theme.color.CustomColors.componentColor
import com.roland.android.odiyo.ui.theme.color.CustomColors.nowPlayingBackgroundColor
import com.roland.android.odiyo.ui.theme.color.CustomColors.sliderColor
import com.roland.android.odiyo.util.MediaControls
import com.roland.android.odiyo.util.QueueItemActions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
	goToCollection: (String, String) -> Unit,
	navigateUp: () -> Unit
) {
	val scaffoldState = rememberModalBottomSheetState(true)
	val openMusicQueue = remember { mutableStateOf(false) }
	val openDetailsDialog = remember { mutableStateOf(false) }
	val screenLaunched = remember { mutableStateOf(false) }
	val generatedColor = nowPlayingBackgroundColor(artwork)
	val componentColor = componentColor(generatedColor)
	val scope = rememberCoroutineScope()

	Scaffold(
		modifier = Modifier.fillMaxSize(),
		topBar = {
			NowPlayingTopAppBar(song, componentColor, goToCollection, navigateUp)
		}
	) {
		Column(
			modifier = Modifier
				.background(generatedColor)
				.padding(start = 30.dp, top = 40.dp, end = 30.dp, bottom = 10.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			MediaDescription(
				song = song,
				artwork = artwork,
				componentColor = componentColor,
				onFavorite = mediaControl,
				goToCollection = goToCollection
			)

			MediaControls(
				song = song,
				isPlaying = isPlaying,
				shuffleState = shuffleState,
				progress = progress,
				timeElapsed = timeElapsed,
				componentColor = componentColor,
				mediaControl = mediaControl,
				showMusicQueue = { openMusicQueue.value = it },
			)

			Spacer(Modifier.weight(1f))

			MediaUtilActions(
				song = song,
				deviceMuted = deviceMuted,
				componentColor = componentColor,
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
			containerColor = generatedColor,
			openBottomSheet = { openMusicQueue.value = it },
			queueAction = queueAction
		)
	}

	if (openDetailsDialog.value && song != null) {
		SongDetailsDialog(song, artwork) { openDetailsDialog.value = it }
	}

	val systemUiController = rememberSystemUiController()
	val useDarkIcons = !isSystemInDarkTheme()
	val color = MaterialTheme.colorScheme.background

	DisposableEffect(systemUiController, useDarkIcons, generatedColor) {
		val isLight = ColorUtils.calculateLuminance(generatedColor.hashCode()) > 0.1
		scope.launch {
			if (!screenLaunched.value) delay(700)
			systemUiController.setSystemBarsColor(
				color = generatedColor,
				darkIcons = isLight
			)
			screenLaunched.value = true
		}
		onDispose {
			systemUiController.setSystemBarsColor(
				color = color,
				darkIcons = useDarkIcons
			)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalFoundationApi::class)
@UnstableApi
@Composable
private fun MediaDescription(
	song: Music?,
	artwork: Any?,
	componentColor: Color,
	onFavorite: (MediaControls) -> Unit,
	goToCollection: (String, String) -> Unit
) {
	val screenWidth = LocalConfiguration.current.screenWidthDp - (30 * 2)
	var songIsFavorite by remember { mutableStateOf(song?.favorite == true) }
	songIsFavorite = song?.favorite == true

	MediaImage(
		modifier = Modifier
			.size(screenWidth.dp, (screenWidth * 1.12).dp)
			.padding(top = 20.dp, bottom = 10.dp),
		artwork = artwork
	)
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically
	) {
		Column(Modifier.weight(1f)) {
			Text(
				text = song?.title ?: stringResource(R.string.unknown),
				color = componentColor,
				modifier = Modifier.basicMarquee(),
				style = MaterialTheme.typography.headlineMedium,
				overflow = TextOverflow.Ellipsis,
				softWrap = false
			)
			Text(
				text = song?.artist ?: stringResource(R.string.unknown),
				color = componentColor,
				modifier = Modifier
					.clip(MaterialTheme.shapes.small)
					.clickable(song != null) { goToCollection(song!!.artist, ARTISTS) }
					.padding(4.dp),
				style = MaterialTheme.typography.titleMedium,
				overflow = TextOverflow.Ellipsis,
				softWrap = false
			)
		}
		if (song != null) {
			IconButton(
				onClick = { onFavorite(MediaControls.Favorite(song)); songIsFavorite = !songIsFavorite },
				modifier = Modifier
					.size(50.dp)
					.padding(start = 4.dp)
			) {
				Icon(
					imageVector = if (songIsFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
					contentDescription = if (songIsFavorite) stringResource(R.string.remove_from_favorite) else stringResource(R.string.add_to_favorite),
					modifier = Modifier.fillMaxSize(0.75f),
					tint = if (songIsFavorite) MaterialTheme.colorScheme.primary else componentColor
				)
			}
		}
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
	componentColor: Color,
	mediaControl: (MediaControls) -> Unit,
	showMusicQueue: (Boolean) -> Unit
) {
	val maxSeekValue = song?.time?.toFloat() ?: 1f
	var seekValue by remember { mutableStateOf(progress) }
	var valueBeingChanged by remember { mutableStateOf(false) }

	Slider(
		value = if (valueBeingChanged) seekValue else progress,
		onValueChange = { valueBeingChanged = true; seekValue = it },
		onValueChangeFinished = {
			mediaControl(MediaControls.SeekToPosition(seekValue.toLong()))
			valueBeingChanged = false
		},
		valueRange = 0f..maxSeekValue,
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 20.dp),
		colors = sliderColor(componentColor)
	)
	Row {
		Text(timeElapsed, color = componentColor)
		Spacer(Modifier.weight(1f))
		Text(song?.duration() ?: "00:00", color = componentColor)
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
				tint = if (shuffleState) MaterialTheme.colorScheme.primary else componentColor
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
				modifier = Modifier.fillMaxSize(0.75f),
				tint = componentColor,
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
				modifier = Modifier.fillMaxSize(),
				tint = componentColor
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
				modifier = Modifier.fillMaxSize(0.75f),
				tint = componentColor
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
				modifier = Modifier.fillMaxSize(0.75f),
				tint = componentColor
			)
		}
	}
}

@Composable
fun MediaUtilActions(
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

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Preview
@Composable
fun NowPlayingPreview() {
	OdiyoTheme {
		val context = LocalContext.current
		var isPlaying by remember { mutableStateOf(false) }
		var deviceMuted by remember { mutableStateOf(false) }
		var shuffleState by remember { mutableStateOf(false) }

		NowPlayingScreen(
			song = previewData[4],
			artwork = previewData[4].getBitmap(context),
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
			goToCollection = { _, _ -> },
			navigateUp = {}
		)
	}
}