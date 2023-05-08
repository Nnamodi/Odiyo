package com.roland.android.odiyo.ui

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.data.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.getArtwork
import com.roland.android.odiyo.theme.OdiyoTheme

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun BottomAppBar(
	concealBottomBar: Boolean,
	song: Music?,
	artwork: Any?,
	isPlaying: Boolean,
	playPause: (Uri, Int?) -> Unit,
	moveToNowPlayingScreen: () -> Unit
) {
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
			moveToNowPlayingScreen = moveToNowPlayingScreen
		)
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
				concealBottomBar = concealBottomBar.value,
				song = currentSong,
				artwork = currentSong.getArtwork(),
				isPlaying = playPause.value,
				playPause = { _, _ -> playPause.value = !playPause.value },
				moveToNowPlayingScreen = {}
			)
		}
	}
}