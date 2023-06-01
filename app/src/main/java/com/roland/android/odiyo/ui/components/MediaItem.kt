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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.getBitmap
import com.roland.android.odiyo.service.Util.toMediaItem

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun RecentSongItem(
	itemIndex: Int,
	song: Music,
	currentSongUri: MediaItem,
	playSong: (Uri, Int) -> Unit
) {
	val context = LocalContext.current
	val imageSize = LocalConfiguration.current.screenWidthDp / 2.5
	val isPlaying = song.uri.toMediaItem == currentSongUri
	val color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground

	Column(
		modifier = Modifier
			.width(imageSize.dp + 16.dp)
			.clip(MaterialTheme.shapes.large)
			.clickable { playSong(song.uri, itemIndex) }
			.padding(8.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		MediaImage(
			artwork = song.getBitmap(context),
			modifier = Modifier.size(imageSize.dp)
		)
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 10.dp)
		) {
			Text(
				text = song.title,
				color = color,
				overflow = TextOverflow.Ellipsis,
				softWrap = false
			)
			Text(
				text = song.artist,
				color = color,
				modifier = Modifier.alpha(0.5f),
				overflow = TextOverflow.Ellipsis,
				softWrap = false
			)
		}
	}
}

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
	val color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground

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
				.size(50.dp)
		)
		Column(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = song.title,
				overflow = TextOverflow.Ellipsis,
				maxLines = 2,
				color = color
			)
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = song.artist,
					overflow = TextOverflow.Ellipsis,
					softWrap = false,
					color = color,
					modifier = Modifier
						.alpha(0.5f)
						.weight(1f)
				)
				Text(
					song.duration(),
					color = color,
					style = MaterialTheme.typography.bodySmall,
					modifier = Modifier
						.alpha(0.5f)
						.padding(start = 4.dp)
				)
			}
		}
		IconButton(onClick = { openMenuSheet(song) }) {
			Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = stringResource(R.string.more_options))
		}
	}
}