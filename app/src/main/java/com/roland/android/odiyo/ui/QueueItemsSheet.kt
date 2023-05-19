package com.roland.android.odiyo.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType.Companion.Sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.ui.MenuItems.*
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.QueueItemActions
import com.roland.android.odiyo.util.QueueMediaItem
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalMaterial3Api
@UnstableApi
@Composable
fun QueueItemsSheet(
	songs: List<Music>,
	currentSong: Music?,
	scaffoldState: SheetState,
	openBottomSheet: (Boolean) -> Unit,
	queueAction: (QueueItemActions) -> Unit
) {
	val scope = rememberCoroutineScope()
	val sheetHeight = (LocalConfiguration.current.screenHeightDp / 2).dp
	val currentSongPosition = songs.indexOf(currentSong)
	val scrollState = rememberLazyListState()

	ModalBottomSheet(
		onDismissRequest = { openBottomSheet(false) },
		sheetState = scaffoldState,
	) {
		Column(Modifier.height(sheetHeight)) {
			Text(
				text = stringResource(R.string.queue_sheet_title, songs.size),
				modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
				fontSize = TextUnit(22f, Sp),
				fontWeight = FontWeight.Bold
			)
			Divider()
			LazyColumn(
				contentPadding = PaddingValues(bottom = 14.dp),
				state = scrollState
			) {
				itemsIndexed(
					items = songs,
					key = { index, song -> "$index-${song.id}" }
				) { index, song ->
					QueueItem(
						itemIndex = index,
						song = song,
						currentSong = currentSong?.uri?.toMediaItem ?: Util.NOTHING_PLAYING,
						itemIsLast = song == songs.last(),
						action = queueAction
					)
				}
			}
		}
	}
	if (currentSongPosition > 0) {
		LaunchedEffect(key1 = true) {
			scope.launch { scrollState.scrollToItem(currentSongPosition) }
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun QueueItem(
	itemIndex: Int,
	song: Music,
	currentSong: MediaItem,
	itemIsLast: Boolean,
	action: (QueueItemActions) -> Unit
) {
	val mediaItem = song.uri.toMediaItem
	val isPlaying by remember { mutableStateOf(mediaItem == currentSong) }
	val color by remember { mutableStateOf(if (isPlaying) Color.Blue else Color.Black) }

	Column(
		modifier = Modifier.fillMaxWidth()
	) {
		Row(
			modifier = Modifier
				.clickable {
					action(
						QueueItemActions.Play(
							QueueMediaItem(itemIndex, song.uri)
						)
					)
				}
				.padding(start = 16.dp, top = 2.dp, end = 4.dp, bottom = 2.dp),
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.CenterVertically
		) {
			Row(
				modifier = Modifier.weight(1f),
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = song.title,
					fontSize = 18.sp,
					overflow = TextOverflow.Ellipsis,
					softWrap = false,
					color = color
				)
				Text(
					text = " - ${song.artist}",
					fontSize = 15.sp,
					fontWeight = FontWeight.Light,
					modifier = Modifier.alpha(0.7f),
					overflow = TextOverflow.Ellipsis,
					softWrap = false,
					color = color
				)
			}
			IconButton(
				onClick = {
					action(
						QueueItemActions.RemoveSong(
							QueueMediaItem(itemIndex, song.uri)
						)
					)
				}
			) {
				Icon(
					imageVector = Icons.Rounded.Clear,
					contentDescription = stringResource(R.string.clear_icon_desc),
					tint = LocalContentColor.current.copy(alpha = 0.7f)
				)
			}
		}
		if (!itemIsLast) Divider()
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Preview(showBackground = true)
@Composable
fun QueueItemsPreview() {
	OdiyoTheme {
		val sheetState = rememberModalBottomSheetState(true)
		val openBottomSheet = remember { mutableStateOf(true) }

		Column(
			modifier = Modifier
				.clickable { openBottomSheet.value = true }
				.fillMaxSize()
		) {
			if (openBottomSheet.value) {
				QueueItemsSheet(
					songs = previewData.shuffled(),
					currentSong = previewData[3],
					scaffoldState = sheetState,
					openBottomSheet = { openBottomSheet.value = it },
					queueAction = {}
				)
			}
		}
	}
}