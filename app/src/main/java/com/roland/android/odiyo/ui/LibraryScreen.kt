package com.roland.android.odiyo.ui

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.ui.components.MediaItem
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions

@ExperimentalMaterial3Api
@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun LibraryScreen(
	songs: List<Music>,
	currentSong: Music?,
	playAudio: (Uri, Int?) -> Unit,
	menuAction: (MediaMenuActions) -> Unit
) {
	val sheetState = rememberModalBottomSheetState(true)
	val openBottomSheet = rememberSaveable { mutableStateOf(false) }
	var songClicked by remember { mutableStateOf<Music?>(null) }

	LazyColumn {
		itemsIndexed(
			items = songs,
			key = { _, song -> song.uri }
		) { index, song ->
			MediaItem(
				itemIndex = index,
				song = song,
				currentSongUri = currentSong?.uri?.toMediaItem ?: Util.NOTHING_PLAYING,
				playAudio = playAudio,
				openMenuSheet = { songClicked = it; openBottomSheet.value = true }
			)
		}
	}
	if (openBottomSheet.value) {
		MediaItemSheet(
			song = songClicked!!,
			scaffoldState = sheetState,
			openBottomSheet = { openBottomSheet.value = it },
			menuAction = menuAction
		)
	}
}

@ExperimentalMaterial3Api
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
				playAudio = { _, _ -> },
				menuAction = {}
			)
		}
	}
}