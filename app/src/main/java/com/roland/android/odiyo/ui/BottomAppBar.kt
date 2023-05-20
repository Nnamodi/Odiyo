package com.roland.android.odiyo.ui

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PauseCircleOutline
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.getArtwork
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.QueueItemActions

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun BottomAppBar(
	song: Music?,
	artwork: Any?,
	isPlaying: Boolean,
	currentSongIndex:Int,
	musicQueue: List<Music>,
	playPause: (Uri, Int?) -> Unit,
	queueAction: (QueueItemActions) -> Unit,
	moveToNowPlayingScreen: () -> Unit,
	concealBottomBar: Boolean
) {
	val scaffoldState = rememberModalBottomSheetState(true)
	val openMusicQueue = remember { mutableStateOf(false) }
	
	AnimatedVisibility(
		visible = concealBottomBar,
		enter = slideInVertically(initialOffsetY = { it }),
		exit = slideOutVertically(targetOffsetY = { it })
	) {
		NowPlayingMinimizedView(
			song = song,
			artwork = artwork,
			isPlaying = isPlaying,
			playPause = playPause,
			showMusicQueue = { openMusicQueue.value = it },
			moveToNowPlayingScreen = moveToNowPlayingScreen
		)
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
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun NowPlayingMinimizedView(
	song: Music?,
	artwork: Any?,
	isPlaying: Boolean,
	playPause: (Uri, Int?) -> Unit,
	showMusicQueue: (Boolean) -> Unit,
	moveToNowPlayingScreen: () -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(10.dp)
			.background(Color.LightGray)
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
		val currentSong = previewData[3]
		val concealBottomBar = remember { mutableStateOf(true) }
		val playPause = remember { mutableStateOf(false) }

		Column(
			modifier = Modifier
				.fillMaxSize()
				.clickable { concealBottomBar.value = !concealBottomBar.value },
			verticalArrangement = Arrangement.Bottom
		) {
			BottomAppBar(
				song = currentSong,
				artwork = currentSong.getArtwork(),
				isPlaying = playPause.value,
				musicQueue = previewData,
				currentSongIndex = 3,
				playPause = { _, _ -> playPause.value = !playPause.value },
				queueAction = {},
				moveToNowPlayingScreen = {},
				concealBottomBar = concealBottomBar.value
			)
		}
	}
}