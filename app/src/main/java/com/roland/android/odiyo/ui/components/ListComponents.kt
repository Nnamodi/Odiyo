package com.roland.android.odiyo.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.model.Music

@Composable
fun EmptyListText(text: String, modifier: Modifier = Modifier) {
	Column(modifier = modifier) {
		Text(
			text = text,
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 40.dp),
			textAlign = TextAlign.Center
		)
	}
}

@Composable
fun SongListHeader(
	songs: List<Music>,
	showSortAction: Boolean = false,
	songsFromSearch: Boolean = false,
	playAllSongs: (Uri, Int) -> Unit,
	openSortDialog: () -> Unit = {},
	openMenu: () -> Unit = {}
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(start = 12.dp, top = 4.dp, bottom = 4.dp),
		horizontalArrangement = Arrangement.Start,
		verticalAlignment = Alignment.CenterVertically
	) {
		if (songsFromSearch) {
			Text(stringResource(R.string.search_result_size, songs.size), Modifier.padding(vertical = 16.dp))
			Spacer(Modifier.weight(1f))
			if (songs.isNotEmpty()) {
				IconButton(onClick = openMenu) {
					Icon(Icons.Rounded.MoreVert, stringResource(R.string.more_options))
				}
			}
		} else {
			TextButton(onClick = {
				playAllSongs(songs.first().uri, 0)
			}) {
				Icon(Icons.Rounded.PlayCircleFilled, null)
				Text(stringResource(R.string.play_all_songs, songs.size), Modifier.padding(start = 4.dp))
			}
			if (showSortAction) {
				Spacer(Modifier.weight(1f))
				IconButton(onClick = openSortDialog) {
					Icon(Icons.Rounded.Sort, stringResource(R.string.sort_by))
				}
			}
		}
	}
}