package com.roland.android.odiyo.ui

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.data.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util
import com.roland.android.odiyo.service.Util.getArtwork
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.theme.OdiyoTheme

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun MediaItemsScreen(
	songs: List<Music>,
	albumName: String,
	currentSong: Music?,
	playAudio: (Uri, Int?) -> Unit,
	navigateUp: () -> Unit,
	artwork: Any?,
	isPlaying: Boolean,
	playPause: (Uri, Int?) -> Unit,
	moveToNowPlayingScreen: () -> Unit
) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(text = albumName, overflow = TextOverflow.Ellipsis, softWrap = false) },
				navigationIcon = {
					IconButton(onClick = navigateUp) {
						Icon(imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
					}
				}
			)
		}
	) {
		Box(
			contentAlignment = Alignment.BottomStart
		) {
			LazyColumn(
				modifier = Modifier
					.fillMaxHeight(1.0f)
					.padding(it)
					.padding(bottom = 75.dp)
			) {
				itemsIndexed(
					items = songs,
					key = { _, song -> song.uri }
				) { index, song ->
					MediaItem(
						itemIndex = index,
						song = song,
						currentSongUri = currentSong?.uri?.toMediaItem ?: Util.NOTHING_PLAYING,
						playAudio = playAudio
					)
				}
			}
			NowPlayingMinimizedView(
				song = currentSong,
				artwork = artwork,
				isPlaying = isPlaying,
				playPause = playPause,
				moveToNowPlayingScreen = moveToNowPlayingScreen
			)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Preview
@Composable
fun MediaItemsScreenPreview() {
	OdiyoTheme {
		val currentSong = previewData[5]
		MediaItemsScreen(
			songs = previewData.takeLast(5),
			albumName = "Does it have to be me?",
			currentSong = currentSong,
			playAudio = { _, _ -> },
			navigateUp = {},
			artwork = currentSong.getArtwork(),
			isPlaying = false,
			playPause = { _, _ -> },
			moveToNowPlayingScreen = {}
		)
	}
}