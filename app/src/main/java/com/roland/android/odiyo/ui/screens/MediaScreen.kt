package com.roland.android.odiyo.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewAlbum
import com.roland.android.odiyo.mediaSource.previewArtist
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.mediaSource.previewPlaylist
import com.roland.android.odiyo.ui.components.AppBar
import com.roland.android.odiyo.ui.dialog.SortOptions
import com.roland.android.odiyo.ui.screens.MediaScreen.*
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.ui.theme.color.light_outline
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalFoundationApi::class)
@UnstableApi
@Composable
fun MediaScreen(
	songsTab: @Composable () -> Unit,
	albumsTab: @Composable () -> Unit,
	artistsTab: @Composable () -> Unit,
	inSelectMode: Boolean,
	navigateToSearch: () -> Unit,
	navigateUp: () -> Unit
) {
	Scaffold(
		topBar = {
			if (!inSelectMode) AppBar(navigateUp, navigateToSearch)
		}
	) {
		Column(
			modifier = Modifier.padding(it)
		) {
			val tabTitles = MediaScreen.values()
			val scope = rememberCoroutineScope()
			val pagerState = rememberPagerState(0)

			TabRow(selectedTabIndex = pagerState.currentPage) {
				tabTitles.forEachIndexed { index, title ->
					Tab(
						text = { Text(stringResource(title.nameRes)) },
						selected = pagerState.currentPage == index,
						onClick = {
							scope.launch { pagerState.animateScrollToPage(index) }
						},
						enabled = !inSelectMode,
						unselectedContentColor = light_outline
					)
				}
			}
			HorizontalPager(
				state = pagerState,
				pageCount = tabTitles.size,
				userScrollEnabled = !inSelectMode
			) { page ->
				when (page) {
					0 -> songsTab()
					1 -> albumsTab()
					2 -> artistsTab()
					else -> {}
				}
			}
		}
	}
}

enum class MediaScreen(val nameRes: Int) {
	Songs(R.string.songs),
	Albums(R.string.albums),
	Artists(R.string.artists)
}

@ExperimentalMaterial3Api
@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Preview
@Composable
fun MediaScreenPreview() {
	OdiyoTheme {
		MediaScreen(
			songsTab = {
				SongsScreen(
					songs = previewData,
					currentSong = previewData[5],
					playlists = previewPlaylist,
					sortOption = SortOptions.NameAZ,
					playAudio = { _, _ -> },
					goToCollection = { _, _ -> },
					menuAction = {}
				) {}
			},
			albumsTab = { AlbumsScreen(previewAlbum) {} },
			artistsTab = { ArtistsScreen(previewArtist) {} },
			inSelectMode = false,
			navigateToSearch = {}
		) {}
	}
}