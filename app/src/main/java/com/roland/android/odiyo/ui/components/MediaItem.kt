package com.roland.android.odiyo.ui.components

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.roland.android.odiyo.R
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.getBitmap
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.util.WindowType
import com.roland.android.odiyo.util.rememberWindowSize

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecentSongItem(
	itemIndex: Int,
	song: Music,
	currentMediaItem: MediaItem,
	playSong: (Uri, Int) -> Unit,
	openMenuSheet: () -> Unit
) {
	val context = LocalContext.current
	val artwork by remember { mutableStateOf(song.getBitmap(context)) }
	val portraitImageSize = LocalConfiguration.current.screenWidthDp / 2.5
	val landscapeImageSize = LocalConfiguration.current.screenHeightDp / 2.2
	val windowSize = rememberWindowSize()
	val imageSize by remember(windowSize.width) {
		mutableStateOf(
			if (windowSize.width == WindowType.Landscape) landscapeImageSize else portraitImageSize
		)
	}
	val isPlaying = song.uri.toMediaItem == currentMediaItem
	val color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground

	Column(
		modifier = Modifier
			.width(imageSize.dp + 16.dp)
			.clip(MaterialTheme.shapes.large)
			.combinedClickable(
				onClick = { playSong(song.uri, itemIndex) },
				onLongClick = openMenuSheet
			)
			.padding(8.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		MediaImage(
			modifier = Modifier.size(imageSize.dp),
			artwork = artwork
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

@Composable
fun MediaItem(
	modifier: Modifier = Modifier,
	song: Music,
	currentMediaItem: MediaItem,
	inSelectionMode: Boolean,
	selected: Boolean,
	showTrailingIcon: Boolean = true,
	openMenuSheet: (Music) -> Unit
) {
	val context = LocalContext.current
	val artwork by remember { mutableStateOf(song.getBitmap(context)) }
	val isPlaying = song.uri.toMediaItem == currentMediaItem
	val textColor = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
	val itemColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.background

	Row(
		modifier = modifier
			.fillMaxWidth()
			.background(itemColor)
			.padding(
				start = 10.dp, top = 10.dp, bottom = 10.dp,
				end = if (showTrailingIcon) 0.dp else 10.dp
			),
		verticalAlignment = Alignment.CenterVertically
	) {
		MediaImage(
			modifier = Modifier
				.padding(end = 8.dp)
				.size(50.dp),
			artwork = artwork
		)
		Column(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = song.title,
				overflow = TextOverflow.Ellipsis,
				maxLines = 2,
				color = textColor
			)
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = song.artist,
					overflow = TextOverflow.Ellipsis,
					softWrap = false,
					color = textColor,
					modifier = Modifier
						.alpha(0.5f)
						.weight(1f)
				)
				if (showTrailingIcon) {
					Text(
						song.duration(),
						color = textColor,
						style = MaterialTheme.typography.bodySmall,
						modifier = Modifier
							.alpha(0.5f)
							.padding(start = 4.dp)
					)
				}
			}
		}
		if (showTrailingIcon) {
			if (inSelectionMode) {
				CheckIcon(selected)
			} else {
				IconButton(onClick = { openMenuSheet(song) }) {
					Icon(Icons.Rounded.MoreVert, stringResource(R.string.more_options))
				}
			}
		}
	}
}

@Composable
fun CheckIcon(selected: Boolean) {
	Icon(
		imageVector = if (selected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
		contentDescription = null,
		tint = MaterialTheme.colorScheme.primary,
		modifier = Modifier.padding(12.dp)
	)
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.selectSemantics(
	inSelectionMode: Boolean,
	selected: Boolean,
	onClick: () -> Unit,
	onLongClick: () -> Unit,
	toggleSelection: (Boolean) -> Unit
): Modifier = composed {
	combinedClickable(
		onClick = onClick,
		onLongClick = onLongClick
	).then(if (inSelectionMode) {
		toggleable(
			value = selected,
			interactionSource = remember { MutableInteractionSource() },
			indication = LocalIndication.current,
			onValueChange = toggleSelection
		)
	} else this)
}