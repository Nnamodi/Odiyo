package com.roland.android.odiyo.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.getBitmap
import com.roland.android.odiyo.states.NowPlayingUiState
import com.roland.android.odiyo.ui.components.MediaImage
import com.roland.android.odiyo.ui.components.NowPlayingIconButton
import com.roland.android.odiyo.ui.components.NowPlayingTopAppBar
import com.roland.android.odiyo.ui.dialog.AddToPlaylistDialog
import com.roland.android.odiyo.ui.navigation.ARTISTS
import com.roland.android.odiyo.ui.screens.nowPlayingScreens.NowPlayingLandscapeView
import com.roland.android.odiyo.ui.screens.nowPlayingScreens.NowPlayingPortraitView
import com.roland.android.odiyo.ui.sheets.NowPlayingScreenSheet
import com.roland.android.odiyo.ui.sheets.QueueItemsSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.ui.theme.color.CustomColors
import com.roland.android.odiyo.ui.theme.color.CustomColors.nowPlayingBackgroundColor
import com.roland.android.odiyo.ui.theme.color.CustomColors.sliderColor
import com.roland.android.odiyo.util.*
import com.roland.android.odiyo.util.SnackbarUtils.showSnackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
	uiState: NowPlayingUiState,
	isDarkTheme: Boolean,
	mediaControl: (MediaControls) -> Unit,
	menuAction: (MediaMenuActions) -> Unit,
	queueAction: (QueueItemActions) -> Unit,
	goToCollection: (String, String) -> Unit,
	navigateUp: () -> Unit
) {
	val scaffoldState = rememberModalBottomSheetState(true)
	val openMoreOptions = remember { mutableStateOf(false) }
	val openMusicQueue = remember { mutableStateOf(false) }
	val openAddToPlaylistDialog = remember { mutableStateOf(false) }
	val screenLaunched = remember { mutableStateOf(false) }
	val windowSize = rememberWindowSize()
	val scope = rememberCoroutineScope()
	val context = LocalContext.current
	val currentSong = uiState.musicQueue.getOrNull(uiState.currentSongIndex)
	val artwork by remember(currentSong) { mutableStateOf(currentSong?.getBitmap(context)) }
	val generatedColor = nowPlayingBackgroundColor(artwork)
	val generatedColorIsDark = generatedColor.luminance() < 0.1
	val snackbarHostState = remember { SnackbarHostState() }
	val snackbarOffset = LocalConfiguration.current.screenHeightDp - 116
	var songsToAddToPlaylist by remember { mutableStateOf<List<Music>>(emptyList()) }

	OdiyoTheme(!generatedColorIsDark) {
		val componentColor = MaterialTheme.colorScheme.background

		Scaffold(
			modifier = Modifier.fillMaxSize(),
			topBar = {
				NowPlayingTopAppBar(
					song = currentSong,
					nowPlayingFrom = uiState.nowPlayingFrom,
					backgroundColor = generatedColor,
					componentColor = componentColor,
					goToCollection = goToCollection,
					navigateUp = navigateUp
				) {
					openMoreOptions.value = true
				}
			},
			snackbarHost = {
				SnackbarHost(snackbarHostState, Modifier.absoluteOffset(y = (-snackbarOffset).dp)) {
					Snackbar(Modifier.padding(horizontal = 30.dp)) { Text(it.visuals.message) }
				}
			},
			containerColor = generatedColor
		) { paddingValues ->
			if (windowSize.width == WindowType.Landscape || windowSize.height == WindowType.Portrait) {
				NowPlayingLandscapeView(
					paddingValues, uiState, componentColor,
					generatedColor, mediaControl, goToCollection
				) { openMusicQueue.value = it }
			} else {
				NowPlayingPortraitView(
					paddingValues, uiState, componentColor,
					generatedColor, mediaControl, goToCollection
				) { openMusicQueue.value = it }
			}
		}

		if (openMusicQueue.value) {
			QueueItemsSheet(
				songs = uiState.musicQueue, currentSongIndex = uiState.currentSongIndex,
				scaffoldState = scaffoldState, containerColor = generatedColor,
				saveQueue = {
					songsToAddToPlaylist = uiState.musicQueue; openAddToPlaylistDialog.value = true
				},
				openBottomSheet = { openMusicQueue.value = it },
				queueAction = queueAction
			)
		}

		if (openMoreOptions.value && currentSong != null) {
			NowPlayingScreenSheet(
				currentSong = currentSong, scaffoldState = scaffoldState,
				componentColor = componentColor, containerColor = generatedColor,
				openBottomSheet = { openMoreOptions.value = it },
				openAddToPlaylistDialog = {
					songsToAddToPlaylist = it; openAddToPlaylistDialog.value = true
				}
			) {
				menuAction(it); showSnackbar(it, context, scope, snackbarHostState)
			}
		}
	}

	if (openAddToPlaylistDialog.value) {
		AddToPlaylistDialog(
			songs = songsToAddToPlaylist, playlists = uiState.playlists,
			songsFromMusicQueue = true,
			saveQueueToPlaylist = {
				queueAction(it)
				scope.launch {
					snackbarHostState.showSnackbar(context.getString(R.string.added_to_playlist))
				}
			},
			openDialog = { openAddToPlaylistDialog.value = it }
		)
	}

	val systemUiController = rememberSystemUiController()
	val useDarkIcons = !isDarkTheme

	DisposableEffect(systemUiController, useDarkIcons, generatedColor) {
		val isLight = generatedColor.luminance() > 0.1
		scope.launch {
			if (!screenLaunched.value) delay(700)
			systemUiController.setSystemBarsColor(
				color = Color.Transparent,
				darkIcons = isLight
			)
			screenLaunched.value = true
		}
		onDispose {
			systemUiController.setSystemBarsColor(
				color = Color.Transparent,
				darkIcons = useDarkIcons
			)
		}
	}

	LaunchedEffect(uiState.musicQueue) {
		if (uiState.musicQueue.isEmpty()) navigateUp()
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaDescription(
	currentSong: Music?,
	componentColor: Color,
	backgroundColor: Color,
	portraitView: Boolean,
	inMultiWindowMode: Boolean = false,
	onFavorite: (MediaControls) -> Unit,
	goToCollection: (String, String) -> Unit
) {
	val interactionSource = remember { MutableInteractionSource() }
	val ripple = rememberRipple(color = CustomColors.rippleColor(backgroundColor))
	val maxSize = LocalConfiguration.current.screenWidthDp - (30 * 2)
	val minSize = LocalConfiguration.current.screenHeightDp / 2.3
	val imageSize = min(minSize, maxSize.toDouble())
	val context = LocalContext.current
	val artwork by remember(currentSong?.id) { mutableStateOf(currentSong?.getBitmap(context)) }
	val defaultMediaArt = BitmapFactory.decodeResource(context.resources, R.drawable.default_art)
	val songIsValid = !(currentSong == null || currentSong.uri == "".toUri())
	var songIsFavorite by remember { mutableStateOf(false, neverEqualPolicy()) }
	songIsFavorite = currentSong?.favorite == true

	if (portraitView) {
		MediaImage(
			modifier = Modifier
				.size(imageSize.dp)
				.padding(bottom = 10.dp),
			artwork = artwork ?: defaultMediaArt
		)
	}
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically
	) {
		Column(Modifier.weight(1f)) {
			Text(
				text = currentSong?.title ?: stringResource(R.string.unknown),
				color = componentColor,
				modifier = Modifier.basicMarquee(),
				style = MaterialTheme.typography.headlineMedium,
				overflow = TextOverflow.Ellipsis,
				softWrap = false
			)
			Text(
				text = currentSong?.artist ?: stringResource(R.string.unknown),
				color = componentColor,
				modifier = Modifier
					.clip(MaterialTheme.shapes.small)
					.clickable(
						interactionSource = interactionSource,
						indication = ripple,
						enabled = songIsValid
					) { goToCollection(currentSong!!.artist, ARTISTS) }
					.padding(4.dp),
				style = MaterialTheme.typography.titleMedium,
				overflow = TextOverflow.Ellipsis,
				softWrap = false
			)
		}
		if (songIsValid && !inMultiWindowMode) {
			NowPlayingIconButton(
				onClick = { onFavorite(MediaControls.Favorite(currentSong!!)) },
				modifier = Modifier
					.size(50.dp)
					.padding(start = 4.dp),
				toggled = songIsFavorite, color = backgroundColor
			) {
				Icon(
					imageVector = if (songIsFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
					contentDescription = stringResource(if (songIsFavorite) R.string.remove_from_favorite else R.string.add_to_favorite),
					modifier = Modifier.fillMaxSize(0.75f)
				)
			}
		}
	}
}

@Composable
fun MediaControls(
	uiState: NowPlayingUiState,
	currentSong: Music?,
	backgroundColor: Color,
	mediaControl: (MediaControls) -> Unit,
	showMusicQueue: (Boolean) -> Unit
) {
	val maxSeekValue = currentSong?.time?.toFloat() ?: 1f
	var seekValue by remember { mutableStateOf(uiState.seekProgress) }
	var valueBeingChanged by remember { mutableStateOf(false) }
	val componentColor = MaterialTheme.colorScheme.background

	Slider(
		value = if (valueBeingChanged) seekValue else uiState.seekProgress,
		onValueChange = { valueBeingChanged = true; seekValue = it },
		onValueChangeFinished = {
			mediaControl(MediaControls.SeekToPosition(seekValue.toLong()))
			valueBeingChanged = false
		},
		valueRange = 0f..maxSeekValue,
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 8.dp),
		colors = sliderColor()
	)
	Row {
		Text(uiState.currentDuration, color = componentColor)
		Spacer(Modifier.weight(1f))
		Text(currentSong?.duration() ?: "00:00", color = componentColor)
	}
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 8.dp),
		horizontalArrangement = Arrangement.SpaceEvenly,
		verticalAlignment = Alignment.CenterVertically
	) {
		NowPlayingIconButton(
			onClick = { mediaControl(MediaControls.Shuffle) },
			modifier = Modifier
				.size(50.dp)
				.weight(0.9f),
			toggled = uiState.shuffleState, color = backgroundColor
		) {
			Icon(
				imageVector = Icons.Rounded.Shuffle,
				contentDescription = stringResource(R.string.shuffle),
				modifier = Modifier.fillMaxSize(0.75f)
			)
		}
		NowPlayingIconButton(
			onClick = { mediaControl(MediaControls.Seek(previous = true, next = false)) },
			modifier = Modifier
				.size(70.dp)
				.weight(1f),
			color = backgroundColor
		) {
			Icon(
				imageVector = Icons.Rounded.SkipPrevious,
				contentDescription = stringResource(R.string.seek_to_previous),
				modifier = Modifier.fillMaxSize(0.75f)
			)
		}
		NowPlayingIconButton(
			onClick = { mediaControl(MediaControls.PlayPause) },
			modifier = Modifier
				.size(70.dp)
				.weight(1.2f),
			color = backgroundColor
		) {
			Icon(
				imageVector = if (uiState.playingState) Icons.Rounded.PauseCircleFilled else Icons.Rounded.PlayCircleFilled,
				contentDescription = if (uiState.playingState) stringResource(R.string.pause) else stringResource(R.string.play),
				modifier = Modifier.fillMaxSize()
			)
		}
		NowPlayingIconButton(
			onClick = { mediaControl(MediaControls.Seek(previous = false, next = true)) },
			modifier = Modifier
				.size(70.dp)
				.weight(1f),
			color = backgroundColor
		) {
			Icon(
				imageVector = Icons.Rounded.SkipNext,
				contentDescription = stringResource(R.string.seek_to_next),
				modifier = Modifier.fillMaxSize(0.75f)
			)
		}
		NowPlayingIconButton(
			onClick = { showMusicQueue(true) },
			modifier = Modifier
				.size(50.dp)
				.weight(0.9f),
			color = backgroundColor
		) {
			Icon(
				imageVector = Icons.Rounded.QueueMusic,
				contentDescription = stringResource(R.string.music_queue),
				modifier = Modifier.fillMaxSize(0.75f)
			)
		}
	}
}

@Preview
@Composable
private fun NowPlayingPreview() {
	OdiyoTheme {
		var uiState by remember {
			mutableStateOf(
				NowPlayingUiState(currentSongIndex = 4, musicQueue = previewData.take(8))
			)
		}

		NowPlayingScreen(
			uiState = uiState,
			isDarkTheme = false,
			mediaControl = {
				when (it) {
					MediaControls.Mute -> uiState = uiState.copy(deviceMuted = !uiState.deviceMuted)
					MediaControls.PlayPause -> uiState = uiState.copy(playingState = !uiState.playingState)
					is MediaControls.Seek -> uiState = uiState.copy(currentSongIndex = if (it.next) uiState.currentSongIndex.inc() else uiState.currentSongIndex.dec())
					MediaControls.Shuffle -> uiState = uiState.copy(shuffleState = !uiState.shuffleState)
					else -> {}
				}
			},
			menuAction = {}, queueAction = {},
			goToCollection = { _, _ -> }
		) {}
	}
}

@Preview(widthDp = 340, heightDp = 280)
@Composable
private fun NowPlayingMultiWindowPreview() {
	NowPlayingPreview()
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun NowPlayingLandscapePreview() {
	NowPlayingPreview()
}