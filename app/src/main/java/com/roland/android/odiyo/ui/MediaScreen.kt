package com.roland.android.odiyo.ui

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PauseCircleOutline
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util
import com.roland.android.odiyo.service.Util.getArtwork
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.theme.OdiyoTheme

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun MediaScreen(
	songs: List<Music>,
	currentSong: Music?,
	isPlaying: Boolean,
	playAudio: (Uri, Boolean) -> Unit,
	moveToNowPlayingScreen: () -> Unit
) {
	Box(
		contentAlignment = Alignment.BottomStart
	) {
		LazyColumn(
			modifier = Modifier.padding(bottom = 75.dp)
		) {
			itemsIndexed(
				items = songs,
				key = { _, song -> song.uri }
			) { _, song ->
				MediaItem(
					song = song,
					currentSongUri = currentSong?.uri?.toMediaItem ?: Util.NOTHING_PLAYING,
					playAudio = playAudio
				)
			}
		}
		NowPlayingMinimizedView(
			song = currentSong,
			isPlaying = isPlaying,
			playPause = playAudio,
			moveToNowPlayingScreen = moveToNowPlayingScreen
		)
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun MediaItem(
	song: Music,
	currentSongUri: MediaItem,
	playAudio: (Uri, Boolean) -> Unit,
) {
	val mediaItem = song.uri.toMediaItem
	val isPlaying by remember { mutableStateOf(mediaItem == currentSongUri) }
	val color by remember { mutableStateOf(if (isPlaying) Color.Blue else Color.Black) }

	Row(
		modifier = Modifier
			.clickable { playAudio(song.uri, true) }
			.fillMaxWidth()
			.padding(10.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		AsyncImage(
			model = song.getArtwork(),
			contentDescription = "media thumbnail",
			placeholder = painterResource(R.drawable.default_art),
			modifier = Modifier
				.padding(end = 8.dp)
				.size(70.dp)
		)
		Column(
			modifier = Modifier.fillMaxWidth(),
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = song.title,
				overflow = TextOverflow.Ellipsis,
				fontWeight = FontWeight.Bold,
				maxLines = 2,
				color = color
			)
			Text(
				text = song.artist,
				overflow = TextOverflow.Ellipsis,
				fontWeight = FontWeight.Light,
				softWrap = false,
				color = color
			)
			Text(song.duration, color = color)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun NowPlayingMinimizedView(
	song: Music?,
	isPlaying: Boolean,
	playPause: (Uri, Boolean) -> Unit,
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
		AsyncImage(
			model = song?.getArtwork(),
			contentDescription = "media thumbnail",
			placeholder = painterResource(R.drawable.default_art),
			modifier = Modifier
				.padding(8.dp)
				.size(44.dp)
		)
		Text(
			text = song?.title ?: "",
			overflow = TextOverflow.Ellipsis,
			modifier = Modifier.weight(1.0f),
			softWrap = false
		)
		IconButton(
			onClick = { song?.uri?.let { playPause(it, false) } },
			modifier = Modifier
				.padding(start = 24.dp, end = 12.dp)
				.size(30.dp)
		) {
			Icon(
				imageVector = if (isPlaying) Icons.Rounded.PauseCircleOutline else Icons.Rounded.PlayCircleOutline,
				contentDescription = if (isPlaying) "pause" else "play",
				modifier = Modifier.fillMaxSize()
			)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Preview
@Composable
fun MediaPreview() {
	OdiyoTheme {
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = MaterialTheme.colorScheme.background
		) {
			val currentSong = previewData[2]
			MediaScreen(
				songs = previewData,
				currentSong = currentSong,
				isPlaying = false,
				playAudio = { _, _ -> },
				moveToNowPlayingScreen = {}
			)
		}
	}
}