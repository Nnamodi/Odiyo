package com.roland.android.odiyo.ui.components

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PauseCircleOutline
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.states.NowPlayingUiState
import com.roland.android.odiyo.ui.dialog.AddToPlaylistDialog
import com.roland.android.odiyo.ui.sheets.QueueItemsSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.ui.theme.color.CustomColors.componentColor
import com.roland.android.odiyo.ui.theme.color.CustomColors.nowPlayingBackgroundColor
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.QueueItemActions
import com.roland.android.odiyo.util.SnackbarUtils.showSnackbar
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@androidx.annotation.OptIn(UnstableApi::class)
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
	val openMusicQueue = remember { mutableStateOf(false) }
	val openAddToPlaylistDialog = remember { mutableStateOf(false) }
	var nowPlayingScreen by remember { mutableStateOf(false) }
	if (concealBottomBar) nowPlayingScreen = true

	if (!nowPlayingScreen && !inSelectionMode) {
		NowPlayingMinimizedView(
			song = uiState.currentSong,
			artwork = uiState.artwork,
			isPlaying = uiState.playingState,
			playPause = playPause,
			showMusicQueue = { openMusicQueue.value = it },
			moveToNowPlayingScreen = moveToNowPlayingScreen
		)
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
			songs = uiState.musicQueue,
			playlists = uiState.playlists,
			addSongToPlaylist = {
				menuAction(it)
				showSnackbar(it, context, scope, snackbarHostState)
			},
			openDialog = { openAddToPlaylistDialog.value = it }
		)
	}

	LaunchedEffect(!concealBottomBar) {
		if (!concealBottomBar) {
			delay(1500)
			nowPlayingScreen = false
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun NowPlayingMinimizedView(
	song: Music?,
	artwork: Bitmap?,
	isPlaying: Boolean,
	playPause: (Uri, Int?) -> Unit,
	showMusicQueue: (Boolean) -> Unit,
	moveToNowPlayingScreen: () -> Unit
) {
	val generatedColor = nowPlayingBackgroundColor(artwork)
	val componentColor = componentColor(generatedColor)

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(10.dp)
			.clip(MaterialTheme.shapes.large)
			.background(generatedColor)
			.clickable { moveToNowPlayingScreen() },
		horizontalArrangement = Arrangement.Start,
		verticalAlignment = Alignment.CenterVertically
	) {
		MediaImage(
			modifier = Modifier
				.padding(8.dp)
				.size(44.dp),
			artwork = artwork
		)
		Text(
			text = song?.title ?: stringResource(R.string.nothing_to_play),
			color = componentColor,
			overflow = TextOverflow.Ellipsis,
			modifier = Modifier.weight(1.0f),
			softWrap = false
		)
		IconButton(
			onClick = { song?.uri?.let { playPause(it, null) } },
			modifier = Modifier
				.padding(start = 24.dp)
				.size(30.dp)
		) {
			Icon(
				imageVector = if (isPlaying) Icons.Rounded.PauseCircleOutline else Icons.Rounded.PlayCircleOutline,
				contentDescription = if (isPlaying) stringResource(R.string.pause) else stringResource(R.string.play),
				tint = componentColor,
				modifier = Modifier.fillMaxSize()
			)
		}
		IconButton(
			onClick = { showMusicQueue(true) },
			modifier = Modifier
				.padding(horizontal = 12.dp)
				.size(30.dp)
		) {
			Icon(
				imageVector = Icons.Rounded.QueueMusic,
				contentDescription = stringResource(R.string.music_queue),
				tint = componentColor,
				modifier = Modifier.fillMaxSize()
			)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Preview(showBackground = true)
@Composable
fun BottomAppBarPreview() {
	OdiyoTheme {
		var uiState by remember {
			mutableStateOf(
				NowPlayingUiState(currentSong = previewData[4], musicQueue = previewData.take(8))
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