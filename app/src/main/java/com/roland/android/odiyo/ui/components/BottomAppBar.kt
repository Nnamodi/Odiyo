package com.roland.android.odiyo.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PauseCircleOutline
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.getBitmap
import com.roland.android.odiyo.states.NowPlayingUiState
import com.roland.android.odiyo.ui.dialog.AddToPlaylistDialog
import com.roland.android.odiyo.ui.sheets.QueueItemsSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.ui.theme.color.CustomColors
import com.roland.android.odiyo.ui.theme.color.CustomColors.nowPlayingBackgroundColor
import com.roland.android.odiyo.ui.theme.color.CustomColors.sliderColor
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.QueueItemActions
import com.roland.android.odiyo.util.SnackbarUtils.showSnackbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomAppBar(
	uiState: NowPlayingUiState,
	playPause: (Uri, Int?) -> Unit,
	queueAction: (QueueItemActions) -> Unit,
	menuAction: (MediaMenuActions) -> Unit,
	moveToNowPlayingScreen: () -> Unit,
	snackbarHostState: SnackbarHostState,
	concealBottomBar: Boolean,
	inSelectionMode: Boolean
) {
	val scaffoldState = rememberModalBottomSheetState(true)
	val context = LocalContext.current
	val scope = rememberCoroutineScope()
	val currentSong = if (uiState.musicQueue.isNotEmpty()) {
		uiState.musicQueue[uiState.currentSongIndex]
	} else null
	val artwork by remember(currentSong?.id) { mutableStateOf(currentSong?.getBitmap(context)) }
	val openMusicQueue = remember { mutableStateOf(false) }
	val openAddToPlaylistDialog = remember { mutableStateOf(false) }
	val currentSong = uiState.musicQueue.getOrNull(uiState.currentSongIndex)
	val artwork by remember(currentSong?.id) { mutableStateOf(currentSong?.getBitmap(context)) }
	val generatedColor = nowPlayingBackgroundColor(artwork)
	val generatedColorIsDark = generatedColor.luminance() < 0.1
	val queueIsNotEmpty by remember(uiState.musicQueue) {
		derivedStateOf { uiState.musicQueue.isNotEmpty() }
	}

	AnimatedVisibility(
		visible = !concealBottomBar && !inSelectionMode && queueIsNotEmpty,
		enter = slideInVertically(
			initialOffsetY = { it },
			animationSpec = tween(durationMillis = 700, delayMillis = 1000)
		),
		exit = ExitTransition.None
	) {
		OdiyoTheme(!generatedColorIsDark) {
			NowPlayingMinimizedView(
				uiState = uiState,
				currentSong = currentSong,
				artwork = artwork,
				generatedColor = generatedColor,
				playPause = playPause,
				showMusicQueue = { openMusicQueue.value = it },
				moveToNowPlayingScreen = moveToNowPlayingScreen
			)
		}
	}

	if (openMusicQueue.value) {
		QueueItemsSheet(
			songs = uiState.musicQueue,
			currentSongIndex = uiState.currentSongIndex,
			scaffoldState = scaffoldState,
			saveQueue = { openAddToPlaylistDialog.value = true },
			openBottomSheet = { openMusicQueue.value = it },
			queueAction = queueAction
		)
	}

	if (openAddToPlaylistDialog.value) {
		AddToPlaylistDialog(
			songs = uiState.musicQueue, playlists = uiState.playlists,
			addSongToPlaylist = {
				menuAction(it)
				showSnackbar(it, context, scope, snackbarHostState)
			},
			openDialog = { openAddToPlaylistDialog.value = it }
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingMinimizedView(
	uiState: NowPlayingUiState,
	currentSong: Music?,
	artwork: Bitmap?,
	generatedColor: Color,
	playPause: (Uri, Int?) -> Unit,
	showMusicQueue: (Boolean) -> Unit,
	moveToNowPlayingScreen: () -> Unit
) {
	val context = LocalContext.current
	val defaultMediaArt = BitmapFactory.decodeResource(context.resources, R.drawable.default_art)
	val maxSeekValue = currentSong?.time?.toFloat() ?: 1f
	val indication = rememberRipple(color = CustomColors.rippleColor(generatedColor))
	val interactionSource = remember { MutableInteractionSource() }

	Box(
		modifier = Modifier
			.safeDrawingPadding()
			.fillMaxWidth()
			.padding(10.dp)
			.clip(MaterialTheme.shapes.large)
			.background(generatedColor)
			.clickable(interactionSource, indication) { moveToNowPlayingScreen() },
		contentAlignment = Alignment.BottomCenter
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 4.dp),
			horizontalArrangement = Arrangement.Start,
			verticalAlignment = Alignment.CenterVertically
		) {
			MediaImage(
				modifier = Modifier
					.padding(8.dp)
					.size(44.dp),
				artwork = artwork ?: defaultMediaArt
			)
			Row(Modifier.weight(1f)) {
				Text(
					text = currentSong?.title ?: stringResource(R.string.nothing_to_play),
					color = MaterialTheme.colorScheme.background,
					overflow = TextOverflow.Ellipsis,
					softWrap = false
				)
				currentSong?.let {
					Text(
						text = " - ${currentSong.artist}",
						color = MaterialTheme.colorScheme.background,
						fontSize = 15.sp,
						fontWeight = FontWeight.Light,
						modifier = Modifier.alpha(0.7f),
						overflow = TextOverflow.Ellipsis,
						softWrap = false,
					)
				}
			}
			NowPlayingIconButton(
				onClick = { currentSong?.uri?.let { playPause(it, null) } },
				modifier = Modifier
					.padding(start = 24.dp)
					.size(30.dp),
				color = generatedColor
			) {
				Icon(
					imageVector = if (uiState.playingState) Icons.Rounded.PauseCircleOutline else Icons.Rounded.PlayCircleOutline,
					contentDescription = if (uiState.playingState) stringResource(R.string.pause) else stringResource(R.string.play),
					modifier = Modifier.fillMaxSize()
				)
			}
			NowPlayingIconButton(
				onClick = { showMusicQueue(true) },
				modifier = Modifier
					.padding(horizontal = 12.dp)
					.size(30.dp),
				color = generatedColor
			) {
				Icon(
					imageVector = Icons.Rounded.QueueMusic,
					contentDescription = stringResource(R.string.music_queue),
					modifier = Modifier.fillMaxSize()
				)
			}
		}
		Slider(
			value = uiState.seekProgress,
			onValueChange = {},
			enabled = false,
			valueRange = 0f..maxSeekValue,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 8.dp)
				.offset(y = 20.dp),
			colors = sliderColor(),
			thumb = {}
		)
	}
}

@Preview(showBackground = true)
@Composable
fun BottomAppBarPreview() {
	OdiyoTheme {
		var uiState by remember {
			mutableStateOf(
				NowPlayingUiState(currentSongIndex = 4, musicQueue = previewData.take(8))
			)
		}
		val concealBottomBar = remember { mutableStateOf(true) }
		val snackbarHostState = remember { SnackbarHostState() }

		Column(
			modifier = Modifier
				.fillMaxSize()
				.clickable { concealBottomBar.value = !concealBottomBar.value },
			verticalArrangement = Arrangement.Bottom
		) {
			BottomAppBar(
				uiState = uiState,
				playPause = { _, _ -> uiState = uiState.copy(playingState = !uiState.playingState) },
				queueAction = {},
				menuAction = {},
				moveToNowPlayingScreen = {},
				snackbarHostState = snackbarHostState,
				concealBottomBar = concealBottomBar.value,
				inSelectionMode = false
			)
		}
	}
}