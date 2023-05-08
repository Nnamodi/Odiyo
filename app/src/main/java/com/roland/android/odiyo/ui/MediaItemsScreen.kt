package com.roland.android.odiyo.ui

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.data.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.theme.OdiyoTheme

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun MediaItemsScreen(
	songs: List<Music>,
	collectionName: String,
	currentSong: Music?,
	playAudio: (Uri, Int?) -> Unit,
	navigateUp: () -> Unit
) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(text = collectionName, overflow = TextOverflow.Ellipsis, softWrap = false) },
				navigationIcon = {
					IconButton(onClick = navigateUp) {
						Icon(imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
					}
				}
			)
		}
	) { innerPadding ->
		LazyColumn(
			modifier = Modifier.padding(innerPadding)
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
			collectionName = "Does it have to be me?",
			currentSong = currentSong,
			playAudio = { _, _ -> },
			navigateUp = {}
		)
	}
}