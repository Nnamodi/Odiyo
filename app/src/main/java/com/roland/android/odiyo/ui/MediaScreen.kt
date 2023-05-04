package com.roland.android.odiyo.ui

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.data.previewAlbum
import com.roland.android.odiyo.data.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.getArtwork
import com.roland.android.odiyo.theme.OdiyoTheme
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun MediaScreen(
	libraryTab: @Composable () -> Unit,
	albumsTab: @Composable () -> Unit,
	artistsTab: @Composable () -> Unit,
	song: Music?,
	artwork: Any?,
	isPlaying: Boolean,
	playPause: (Uri, Int?) -> Unit,
	moveToNowPlayingScreen: () -> Unit
) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Odiyo") }//stringResource(R.string.app_name)) }
			)
		}
	) {
		Column(
			modifier = Modifier.padding(it)
		) {
			val tabTitles = listOf("Library", "Albums", "Artists")
			val scope = rememberCoroutineScope()
			val pagerState = rememberPagerState(0)

			TabRow(selectedTabIndex = pagerState.currentPage) {
				tabTitles.forEachIndexed { index, title ->
					Tab(
						text = { Text(title) },
						selected = pagerState.currentPage == index,
						onClick = {
							scope.launch { pagerState.animateScrollToPage(index) }
						}
					)
				}
			}
			Box(
				contentAlignment = Alignment.BottomStart
			) {
				HorizontalPager(
					modifier = Modifier
						.fillMaxHeight(1.0f)
						.padding(bottom = 75.dp),
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
				NowPlayingMinimizedView(
					song = song,
					artwork = artwork,
					isPlaying = isPlaying,
					playPause = playPause,
					moveToNowPlayingScreen = moveToNowPlayingScreen
				)
			}
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Preview
@Composable
fun MediaScreenPreview() {
	OdiyoTheme {
		val currentSong = previewData[2]

		MediaScreen(
			libraryTab = {
				LibraryScreen(
					songs = previewData,
					currentSong = currentSong,
					playAudio = { _, _ -> }
				)
			},
			albumsTab = { AlbumsScreen(previewAlbum) {} },
			artistsTab = { ArtistsScreen() },
			song = currentSong,
			artwork = currentSong.getArtwork(),
			isPlaying = false,
			playPause = { _, _ -> },
			moveToNowPlayingScreen = {}
		)
	}
}