package com.roland.android.odiyo.ui.components.appbars

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.PauseCircleOutline
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.roland.android.domain.model.Music
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.previewData
import com.roland.android.odiyo.ui.components.MediaImage
import com.roland.android.odiyo.ui.components.NowPlayingIconButton
import com.roland.android.odiyo.ui.dialog.AddToPlaylistDialog
import com.roland.android.odiyo.ui.screens.nowPlayingScreens.NowPlayingUiState
import com.roland.android.odiyo.ui.sheets.QueueItemsSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.ui.theme.color.CustomColors
import com.roland.android.odiyo.ui.theme.color.CustomColors.nowPlayingBackgroundColor
import com.roland.android.odiyo.ui.theme.color.CustomColors.sliderColor
import com.roland.android.odiyo.util.SnackbarUtils.showSnackbar
import com.roland.android.odiyo.util.actions.MediaMenuActions
import com.roland.android.odiyo.util.actions.QueueItemActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomAppBar(
	uiState: NowPlayingUiState,
	playPause: (Uri) -> Unit,
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
	val openMusicQueue = remember { mutableStateOf(false) }
	val openAddToPlaylistDialog = remember { mutableStateOf(false) }
	val currentSong = uiState.musicQueue.getOrNull(uiState.currentSongIndex)
	val generatedColor = nowPlayingBackgroundColor(currentSong?.artwork)
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
				artwork = currentSong?.artwork,
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
private fun NowPlayingMinimizedView(
	uiState: NowPlayingUiState,
	currentSong: Music?,
	artwork: Bitmap?,
	generatedColor: Color,
	playPause: (Uri) -> Unit,
	showMusicQueue: (Boolean) -> Unit,
	moveToNowPlayingScreen: () -> Unit
) {
	val context = LocalContext.current
	val defaultMediaArt = BitmapFactory.decodeResource(context.resources, R.drawable.default_art)
	val maxSeekValue = currentSong?.duration?.toFloat() ?: 1f
	val indication = ripple(color = CustomColors.rippleColor(generatedColor))
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
				onClick = { currentSong?.uri?.let(playPause) },
				modifier = Modifier
					.padding(start = 24.dp)
					.size(30.dp),
				color = generatedColor
			) {
				Icon(
					imageVector = if (uiState.isPlaying) Icons.Rounded.PauseCircleOutline else Icons.Rounded.PlayCircleOutline,
					contentDescription = if (uiState.isPlaying) stringResource(R.string.pause) else stringResource(R.string.play),
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
					imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
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
				playPause = { uiState = uiState.copy(isPlaying = !uiState.isPlaying) },
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