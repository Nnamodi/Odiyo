package com.roland.android.odiyo.ui.components

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.getBitmap
import com.roland.android.odiyo.service.Util.toMediaItem

@ExperimentalMaterial3Api
@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun MediaItem(
	itemIndex: Int,
	song: Music,
	currentSongUri: MediaItem,
	playAudio: (Uri, Int?) -> Unit,
	openMenuSheet: (Music) -> Unit
) {
	val context = LocalContext.current
	val isPlaying = song.uri.toMediaItem == currentSongUri
	val color = if (isPlaying) MaterialTheme.colorScheme.primary else Color.Black

	Row(
		modifier = Modifier
			.clickable { playAudio(song.uri, itemIndex) }
			.fillMaxWidth()
			.padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		MediaImage(
			artwork = song.getBitmap(context),
			modifier = Modifier
				.padding(end = 8.dp)
				.size(70.dp)
		)
		Column(
			modifier = Modifier.weight(1f),
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
		IconButton(onClick = { openMenuSheet(song) }) {
			Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = stringResource(R.string.more_options))
		}
	}
}