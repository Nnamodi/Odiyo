package com.roland.android.odiyo.ui

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.theme.OdiyoTheme

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun LibraryScreen(
	songs: List<Music>,
	currentSong: Music?,
	playAudio: (Uri, Int?) -> Unit
) {
	LazyColumn {
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
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun MediaItem(
	itemIndex: Int,
	song: Music,
	currentSongUri: MediaItem,
	playAudio: (Uri, Int?) -> Unit,
) {
	val mediaItem = song.uri.toMediaItem
	val isPlaying by remember { mutableStateOf(mediaItem == currentSongUri) }
	val color by remember { mutableStateOf(if (isPlaying) Color.Blue else Color.Black) }

	Row(
		modifier = Modifier
			.clickable { playAudio(song.uri, itemIndex) }
			.fillMaxWidth()
			.padding(10.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		AsyncImage(
			model = song.thumbnail,
			contentDescription = "media thumbnail",
			placeholder = painterResource(R.drawable.default_art),
			contentScale = ContentScale.Crop,
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
@Preview
@Composable
fun LibraryPreview() {
	OdiyoTheme {
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = MaterialTheme.colorScheme.background
		) {
			val currentSong = previewData[2]
			LibraryScreen(
				songs = previewData,
				currentSong = currentSong,
				playAudio = { _, _ -> }
			)
		}
	}
}