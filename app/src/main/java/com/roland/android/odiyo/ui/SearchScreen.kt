package com.roland.android.odiyo.ui

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.theme.OdiyoTheme

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
	) {
		Column(
			modifier = Modifier.padding(it)
		) {
			if (searchQuery.isEmpty()) {
				Text(
					text = "Type in the box to search",
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 40.dp),
					textAlign = TextAlign.Center
				)
			} else {
				LazyColumn {
					item {
						Text(
							text = "Search matched ${searchResult.size} songs",
							modifier = Modifier.padding(12.dp)
						)
					}
					itemsIndexed(
						items = searchResult,
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
					Text(text = "Search", modifier = Modifier.alpha(0.6f))
				},
				trailingIcon = {
					if (query.isNotEmpty()) {
						IconButton(onClick = clearSearchQuery) {
							Icon(imageVector = Icons.Rounded.Clear, contentDescription = "Clear search icon")
						}
					}
				},
				singleLine = true
			)
		},
		navigationIcon = {
			IconButton(onClick = closeSearchScreen) {
				Icon(imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
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
			clearSearchQuery = {},
			closeSearchScreen = {}
		)
	}
}