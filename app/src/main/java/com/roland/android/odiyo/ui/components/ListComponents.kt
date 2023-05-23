package com.roland.android.odiyo.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material3.Icon
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
import com.roland.android.odiyo.util.MediaMenuActions

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
	songsFromSearch: Boolean = false,
	playAllSongs: (Uri, Int) -> Unit,
	addSongsToQueue: (MediaMenuActions) -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 12.dp, vertical = 4.dp),
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.CenterVertically
	) {
		if (songsFromSearch) {
			Text(stringResource(R.string.search_result_size, songs.size))
		} else {
			TextButton(onClick = {
				playAllSongs(songs.first().uri, 0)
			}) {
				Icon(Icons.Rounded.PlayCircleFilled, null)
				Text(stringResource(R.string.play_all_songs, songs.size), Modifier.padding(start = 4.dp))
			}
		}
		Spacer(Modifier.weight(1f))
		TextButton(
			onClick = {
				addSongsToQueue(MediaMenuActions.PlayNext(songs))
			}
		) {
			Text(stringResource(R.string.add_to_queue))
		}
	}
}