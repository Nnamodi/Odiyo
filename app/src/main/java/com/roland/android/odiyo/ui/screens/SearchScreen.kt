package com.roland.android.odiyo.ui.screens

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.NOTHING_PLAYING
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.ui.components.EmptyListText
import com.roland.android.odiyo.ui.components.MediaItem
import com.roland.android.odiyo.ui.components.SongListHeader
import com.roland.android.odiyo.ui.sheets.MediaItemSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.MediaMenuActions

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun SearchScreen(
	searchQuery: String,
	searchResult: List<Music>,
	onTextChange: (String) -> Unit,
	currentSong: Music?,
	playAudio: (Uri, Int?) -> Unit,
	menuAction: (MediaMenuActions) -> Unit,
	clearSearchQuery: () -> Unit,
	closeSearchScreen: () -> Unit
) {
	Scaffold(
		topBar = {
			SearchBar(
				query = searchQuery,
				onTextChange = onTextChange,
				clearSearchQuery = clearSearchQuery,
				closeSearchScreen = closeSearchScreen
			)
		}
	) { paddingValues ->
		val sheetState = rememberModalBottomSheetState(true)
		val openBottomSheet = rememberSaveable { mutableStateOf(false) }
		var songClicked by remember { mutableStateOf<Music?>(null) }

		if (searchQuery.isEmpty()) {
			EmptyListText(
				text = stringResource(R.string.type_to_search),
				modifier = Modifier.padding(paddingValues)
			)
		} else {
			LazyColumn(Modifier.padding(paddingValues)) {
				item {
					SongListHeader(
						songs = searchResult,
						songsFromSearch = true,
						playAllSongs = { _, _ -> },
						addSongsToQueue = menuAction
					)
				}
				itemsIndexed(
					items = searchResult,
					key = { _, song -> song.uri }
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
			if (openBottomSheet.value) {
				MediaItemSheet(
					song = songClicked!!,
					scaffoldState = sheetState,
					openBottomSheet = { openBottomSheet.value = it },
					menuAction = menuAction
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
	query: String,
	onTextChange: (String) -> Unit,
	clearSearchQuery: () -> Unit,
	closeSearchScreen: () -> Unit
) {
	TopAppBar(
		title = {
			OutlinedTextField(
				modifier = Modifier.fillMaxWidth(),
				value = query,
				onValueChange = onTextChange,
				placeholder = {
					Row(
						Modifier.alpha(0.6f), Arrangement.Center, Alignment.CenterVertically
					) {
						Icon(Icons.Rounded.Search, null)
						Text(stringResource(R.string.search), Modifier.padding(start = 4.dp))
					}
				},
				trailingIcon = {
					if (query.isNotEmpty()) {
						IconButton(onClick = clearSearchQuery) {
							Icon(Icons.Rounded.Clear, stringResource(R.string.clear_icon_desc))
						}
					}
				},
				singleLine = true,
				shape = MaterialTheme.shapes.large
			)
		},
		navigationIcon = {
			IconButton(onClick = closeSearchScreen) {
				Icon(Icons.Rounded.ArrowBackIosNew, stringResource(R.string.back_icon_desc))
			}
		}
	)
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Preview
@Composable
fun SearchScreenPreview() {
	OdiyoTheme {
		SearchScreen(
			searchQuery = "a",
			searchResult = previewData.shuffled(),
			onTextChange = {},
			currentSong = previewData[3],
			playAudio = { _, _ -> },
			menuAction = {},
			clearSearchQuery = {},
			closeSearchScreen = {}
		)
	}
}