package com.roland.android.odiyo.ui.screens.nowPlayingScreens

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PauseCircleFilled
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.roland.android.domain.model.Music
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.ui.components.MediaImage
import com.roland.android.odiyo.ui.components.NowPlayingIconButton
import com.roland.android.odiyo.ui.components.NowPlayingTopAppBar
import com.roland.android.odiyo.ui.dialog.AddToPlaylistDialog
import com.roland.android.odiyo.ui.navigation.ARTISTS
import com.roland.android.odiyo.ui.navigation.Screens
import com.roland.android.odiyo.ui.screens.nowPlayingScreens.orientations.NowPlayingLandscapeView
import com.roland.android.odiyo.ui.screens.nowPlayingScreens.orientations.NowPlayingPortraitView
import com.roland.android.odiyo.ui.sheets.NowPlayingScreenSheet
import com.roland.android.odiyo.ui.sheets.QueueItemsSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.ui.theme.color.CustomColors
import com.roland.android.odiyo.ui.theme.color.CustomColors.nowPlayingBackgroundColor
import com.roland.android.odiyo.ui.theme.color.CustomColors.sliderColor
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.QueueItemActions
import com.roland.android.odiyo.util.SnackbarUtils.showSnackbar
import com.roland.android.odiyo.util.WindowType
import com.roland.android.odiyo.util.rememberWindowSize
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
	navigate: (Screens) -> Unit
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
	val generatedColor = nowPlayingBackgroundColor(currentSong?.artwork)
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
					navigate = navigate
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
					generatedColor, mediaControl, navigate
				) { openMusicQueue.value = it }
			} else {
				NowPlayingPortraitView(
					paddingValues, uiState, componentColor,
					generatedColor, mediaControl, navigate
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
		if (uiState.musicQueue.isEmpty()) navigate(Screens.Back)
	}
}

@Composable
fun MediaDescription(
	currentSong: Music?,
	componentColor: Color,
	backgroundColor: Color,
	portraitView: Boolean,
	inMultiWindowMode: Boolean = false,
	onFavorite: (MediaControls) -> Unit,
	goToCollection: (Screens) -> Unit
) {
	val interactionSource = remember { MutableInteractionSource() }
	val ripple = ripple(color = CustomColors.rippleColor(backgroundColor))
	val maxSize = LocalConfiguration.current.screenWidthDp - (30 * 2)
	val minSize = LocalConfiguration.current.screenHeightDp / 2.3
	val imageSize = min(minSize, maxSize.toDouble())
	val songIsValid = !(currentSong == null || currentSong.uri == "".toUri())
	var songIsFavorite by remember { mutableStateOf(false, neverEqualPolicy()) }
	songIsFavorite = currentSong?.favorite == true

	if (portraitView) {
		MediaImage(
			modifier = Modifier
				.size(imageSize.dp)
				.padding(bottom = 10.dp),
			artwork = currentSong?.artwork
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
					) {
						goToCollection(Screens.ListScreen(currentSong!!.artist, ARTISTS))
					}
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
	val maxSeekValue = currentSong?.duration?.toFloat() ?: 1f
	var seekValue by remember { mutableFloatStateOf(uiState.seekProgress) }
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
		Text(currentSong?.duration?.time ?: "00:00", color = componentColor)
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
				imageVector = if (uiState.isPlaying) Icons.Rounded.PauseCircleFilled else Icons.Rounded.PlayCircleFilled,
				contentDescription = if (uiState.isPlaying) stringResource(R.string.pause) else stringResource(R.string.play),
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
				imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
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
					MediaControls.PlayPause -> uiState = uiState.copy(isPlaying = !uiState.isPlaying)
					is MediaControls.Seek -> uiState = uiState.copy(currentSongIndex = if (it.next) uiState.currentSongIndex.inc() else uiState.currentSongIndex.dec())
					MediaControls.Shuffle -> uiState = uiState.copy(shuffleState = !uiState.shuffleState)
					else -> {}
				}
			},
			menuAction = {}, queueAction = {}
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