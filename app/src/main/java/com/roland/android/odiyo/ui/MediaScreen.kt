package com.roland.android.odiyo.ui

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import com.roland.android.odiyo.data.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.theme.OdiyoTheme

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MediaScreen(
	songs: List<Music>,
	currentSongUri: MediaItem,
	playAudio: (Uri) -> Unit,
) {
	LazyColumn(
		modifier = Modifier.fillMaxSize()
	) {
		itemsIndexed(
			items = songs,
			key = { _, song -> song.uri }
		) { _, song ->
			MediaItem(song, currentSongUri, playAudio)
		}
	}
}

@Composable
fun MediaItem(
	song: Music,
	currentSongUri: MediaItem,
	playAudio: (Uri) -> Unit,
) {
	val mediaItem = MediaItem.Builder().setUri(song.uri).build()
	val isPlaying by remember { mutableStateOf(mediaItem == currentSongUri) }
	val color by remember { mutableStateOf(if (isPlaying) Color.Blue else Color.Black) }

	Row(
		Modifier
			.fillMaxWidth()
			.clickable { playAudio(song.uri) }
	) {
		song.thumbnail?.let { Image(bitmap = it.asImageBitmap(), contentDescription = "song thumbnail") }
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(20.dp),
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Text(song.name, color = color)
			Text(song.title, color = color)
			Text(song.artist, color = color)
			Text(song.duration.toString(), color = color)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
fun MediaPreview() {
	OdiyoTheme {
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = MaterialTheme.colorScheme.background
		) {
			val mediaItem = MediaItem.Builder().setUri("3".toUri()).build()
			MediaScreen(previewData, mediaItem) {}
		}
	}
}