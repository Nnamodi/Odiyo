package com.roland.android.odiyo.ui.sheets

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.material3.BottomSheetDefaults.ContainerColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.ui.components.SwipeableItem
import com.roland.android.odiyo.ui.components.rememberSwipeToDismissState
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.ui.theme.color.CustomColors
import com.roland.android.odiyo.ui.theme.color.CustomColors.componentColor
import com.roland.android.odiyo.util.QueueItemActions
import com.roland.android.odiyo.util.QueueMediaItem
import com.roland.android.odiyo.util.sheetHeight
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun QueueItemsSheet(
	songs: List<Music>,
	currentSongIndex: Int,
	scaffoldState: SheetState,
	containerColor: Color = ContainerColor,
	saveQueue: () -> Unit,
	openBottomSheet: (Boolean) -> Unit,
	queueAction: (QueueItemActions) -> Unit
) {
	val scope = rememberCoroutineScope()
	val scrollState = rememberLazyListState()
	val componentColor = if (containerColor != ContainerColor) componentColor(containerColor) else MaterialTheme.colorScheme.onBackground
	val containerColorBlend = ColorUtils.blendARGB(Color.White.toArgb(), containerColor.toArgb(), 0.95f)
	val customContainerColor = if (containerColor == ContainerColor) containerColor else Color(containerColorBlend)

	ModalBottomSheet(
		onDismissRequest = { openBottomSheet(false) },
		sheetState = scaffoldState,
		containerColor = customContainerColor
	) {
		Column(Modifier.heightIn(min = 10.dp, max = sheetHeight().dp)) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = pluralStringResource(R.plurals.queue_sheet_title, songs.size, songs.size),
					style = MaterialTheme.typography.titleLarge,
					color = componentColor,
					modifier = Modifier.padding(vertical = 12.dp)
				)
				Spacer(Modifier.weight(1f))
				if (songs.isNotEmpty()) {
					TextButton(
						onClick = { saveQueue(); openBottomSheet(false) },
						colors = ButtonDefaults.textButtonColors(
							contentColor = componentColor(containerColor, toggled = true)
						)
					) {
						Text(stringResource(R.string.save_queue))
					}
				}
			}
			Divider(color = Color.White.copy(alpha = 0.5f))
			LazyColumn(
				modifier = Modifier.wrapContentHeight(),
				contentPadding = PaddingValues(bottom = 14.dp),
				state = scrollState
			) {
				itemsIndexed(
					items = songs,
					key = { index, song -> "$index-${song.id}" }
				) { index, song ->
					val dismissState = rememberSwipeToDismissState(
						index = index,
						songUri = song.uri,
						queueAction = queueAction
					)
					val elevation by animateDpAsState(targetValue = if (dismissState.dismissDirection != null) 4.dp else 0.dp)
					val colorBlend = ColorUtils.blendARGB(Color.Black.toArgb(), containerColor.toArgb(), 0.75f)
					val backgroundColor = if (containerColor == ContainerColor) containerColor else Color(colorBlend)

					SwipeableItem(
						componentColor = componentColor,
						dismissState = dismissState,
						defaultBackgroundColor = backgroundColor
					) {
						QueueItem(
							itemIndex = index,
							song = song,
							currentSongIndex = currentSongIndex,
							itemIsLast = index == songs.size,
							elevation = elevation,
							cardColor = customContainerColor,
							componentColor = componentColor,
							containerColor = containerColor
						) { queueAction(it); if (songs.size == 1) openBottomSheet(false) }
					}
				}
			}
		}
	}
	if (currentSongIndex > 0) {
		LaunchedEffect(key1 = true) {
			scope.launch { scrollState.scrollToItem(currentSongIndex) }
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun QueueItem(
	itemIndex: Int,
	song: Music,
	currentSongIndex: Int,
	itemIsLast: Boolean,
	elevation: Dp,
	cardColor: Color,
	componentColor: Color,
	containerColor: Color,
	action: (QueueItemActions) -> Unit
) {
	val interactionSource = remember { MutableInteractionSource() }
	val ripple = rememberRipple(color = CustomColors.rippleColor(cardColor))
	val isPlaying = itemIndex == currentSongIndex
	val color = if (isPlaying) componentColor(containerColor, true) else componentColor

	Card(
		colors = CardDefaults.cardColors(cardColor),
		elevation = CardDefaults.cardElevation(elevation),
		shape = RectangleShape
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.clickable(interactionSource, ripple) {
					action(
						QueueItemActions.Play(
							QueueMediaItem(itemIndex, song.uri)
						)
					)
				}
				.padding(horizontal = 16.dp, vertical = 16.dp),
			horizontalArrangement = Arrangement.Start,
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
		if (!itemIsLast) Divider(color = containerColor)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
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
					currentSongIndex = 4,
					scaffoldState = sheetState,
					saveQueue = {},
					openBottomSheet = { openBottomSheet.value = it }
				) {}
			}
		}
	}
}