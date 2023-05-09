package com.roland.android.odiyo.ui

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PauseCircleOutline
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.previewAlbum
import com.roland.android.odiyo.data.previewArtist
import com.roland.android.odiyo.data.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.getArtwork
import com.roland.android.odiyo.theme.OdiyoTheme
import com.roland.android.odiyo.ui.MediaScreen.*
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun MediaScreen(
	libraryTab: @Composable () -> Unit,
	albumsTab: @Composable () -> Unit,
	artistsTab: @Composable () -> Unit,
	navigateToSearch: () -> Unit
) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = stringResource(R.string.app_name),
						fontStyle = FontStyle.Italic,
						fontWeight = FontWeight.Bold
					)
				},
				actions = {
					IconButton(onClick = navigateToSearch) {
						Icon(imageVector = Icons.Rounded.Search, contentDescription = "Search icon")
					}
				}
			)
		}
	) {
		Column(
			modifier = Modifier.padding(it)
		) {
			val tabTitles = listOf(Library, Albums, Artists)
			val scope = rememberCoroutineScope()
			val pagerState = rememberPagerState(0)

			TabRow(selectedTabIndex = pagerState.currentPage) {
				tabTitles.forEachIndexed { index, title ->
					Tab(
						text = { Text(title.name) },
						selected = pagerState.currentPage == index,
						onClick = {
							scope.launch { pagerState.animateScrollToPage(index) }
						}
					)
				}
			}
			HorizontalPager(
				state = pagerState,
				pageCount = tabTitles.size
			) { page ->
				when (page) {
					0 -> libraryTab()
					1 -> albumsTab()
					2 -> artistsTab()
					else -> {}
				}
			}
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun NowPlayingMinimizedView(
	song: Music?,
	artwork: Any?,
	isPlaying: Boolean,
	playPause: (Uri, Int?) -> Unit,
	moveToNowPlayingScreen: () -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(10.dp)
			.background(Color.LightGray)
			.clickable { moveToNowPlayingScreen() },
		horizontalArrangement = Arrangement.Start,
		verticalAlignment = Alignment.CenterVertically
	) {
		AsyncImage(
			model = artwork,
			contentDescription = "media thumbnail",
			placeholder = painterResource(R.drawable.default_art),
			contentScale = ContentScale.Crop,
			modifier = Modifier
				.padding(8.dp)
				.size(44.dp)
		)
		Text(
			text = song?.title ?: "Nothing is playing",
			overflow = TextOverflow.Ellipsis,
			modifier = Modifier.weight(1.0f),
			softWrap = false
		)
		IconButton(
			onClick = { song?.uri?.let { playPause(it, null) } },
			modifier = Modifier
				.padding(start = 24.dp, end = 12.dp)
				.size(30.dp)
		) {
			Icon(
				imageVector = if (isPlaying) Icons.Rounded.PauseCircleOutline else Icons.Rounded.PlayCircleOutline,
				contentDescription = if (isPlaying) "pause" else "play",
				modifier = Modifier.fillMaxSize()
			)
		}
	}
}

enum class MediaScreen { Library, Albums, Artists }

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Preview
@Composable
fun MediaScreenPreview() {
	OdiyoTheme {
		MediaScreen(
			libraryTab = {
				LibraryScreen(
					songs = previewData,
					currentSong = previewData[5],
					playAudio = { _, _ -> }
				)
			},
			albumsTab = { AlbumsScreen(previewAlbum) {} },
			artistsTab = { ArtistsScreen(previewArtist) {} },
			navigateToSearch = {}
		)
	}
}