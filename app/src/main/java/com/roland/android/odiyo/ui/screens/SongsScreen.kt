package com.roland.android.odiyo.ui.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.NOTHING_PLAYING
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.ui.components.MediaItem
import com.roland.android.odiyo.ui.sheets.MediaItemSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions
import com.roland.android.odiyo.util.SnackbarUtils.showSnackbar

@ExperimentalMaterial3Api
@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun SongsScreen(
	songs: List<Music>,
	currentSong: Music?,
	playAudio: (Uri, Int?) -> Unit,
	goToCollection: (String, String) -> Unit,
	menuAction: (MediaMenuActions) -> Unit
) {
	val sheetState = rememberModalBottomSheetState(true)
	val openBottomSheet = rememberSaveable { mutableStateOf(false) }
	var songClicked by remember { mutableStateOf<Music?>(null) }
	val context = LocalContext.current
	val snackbarHostState = remember { SnackbarHostState() }
	val scope = rememberCoroutineScope()

	Scaffold(
		snackbarHost = {
			SnackbarHost(snackbarHostState) {
				Snackbar(Modifier.padding(horizontal = 16.dp)) {
					Text(it.visuals.message)
				}
			}
		}
	) {
		LazyColumn {
			itemsIndexed(
				items = songs,
				key = { _, song -> song.id }
			) { index, song ->
				MediaItem(
					itemIndex = index,
					song = song,
					currentSongUri = currentSong?.uri?.toMediaItem ?: NOTHING_PLAYING,
					playAudio = playAudio,
					openMenuSheet = { songClicked = it; openBottomSheet.value = true }
				)
			}
		}
		if (openBottomSheet.value && songClicked != null) {
			MediaItemSheet(
				song = songClicked!!,
				scaffoldState = sheetState,
				goToCollection = goToCollection,
				openBottomSheet = { openBottomSheet.value = it },
				menuAction = {
					menuAction(it)
					showSnackbar(it, context, scope, snackbarHostState, songClicked!!)
				}
			)
		}
	}
}

@ExperimentalMaterial3Api
@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Preview
@Composable
fun SongsScreenPreview() {
	OdiyoTheme {
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = MaterialTheme.colorScheme.background
		) {
			val currentSong = previewData[2]
			SongsScreen(
				songs = previewData,
				currentSong = currentSong,
				playAudio = { _, _ -> },
				goToCollection = { _, _ -> },
				menuAction = {}
			)
		}
	}
}