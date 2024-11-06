package com.roland.android.odiyo.ui.screens.media

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.State
import com.roland.android.odiyo.mediaSource.previewAlbum
import com.roland.android.odiyo.mediaSource.previewArtist
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.ui.components.AppBar
import com.roland.android.odiyo.ui.navigation.ALBUMS
import com.roland.android.odiyo.ui.navigation.ARTISTS
import com.roland.android.odiyo.ui.navigation.Screens
import com.roland.android.odiyo.ui.screens.media.tabs.AlbumsTab
import com.roland.android.odiyo.ui.screens.media.tabs.AllSongsTab
import com.roland.android.odiyo.ui.screens.media.tabs.ArtistsTab
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.ui.theme.color.light_outline
import com.roland.android.odiyo.util.MediaMenuActions
import kotlinx.coroutines.launch

@Composable
fun MediaScreen(
	uiState: MediaUiState,
	inSelectMode: Boolean,
	playAudio: (Uri, Int?, String, String) -> Unit,
	menuAction: (MediaMenuActions) -> Unit,
	closeSelectionMode: (Boolean) -> Unit,
	navigate: (Screens) -> Unit
) {
	Scaffold(
		topBar = {
			if (!inSelectMode) AppBar(navigate)
		}
	) {
		Column(
			modifier = Modifier.padding(it)
		) {
			val tabTitles = MediaScreen.entries.toTypedArray()
			val scope = rememberCoroutineScope()
			val pagerState = rememberPagerState(initialPage = 0) { tabTitles.size }

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
				beyondViewportPageCount = tabTitles.size,
				userScrollEnabled = !inSelectMode
			) { page ->
				when (page) {
					0 -> {
						AllSongsTab(
							uiState = uiState,
							playAudio = playAudio,
							menuAction = menuAction,
							closeSelectionMode = closeSelectionMode,
							navigate = navigate
						)
					}
					1 -> {
						AlbumsTab(
							allAlbums = uiState.allAlbums,
							prepareAndViewSongs = { collectionName ->
								navigate(Screens.ListScreen(collectionName, ALBUMS))
							}
						)
					}
					2 -> {
						ArtistsTab(
							allArtists = uiState.allArtists,
							prepareAndViewSongs = { collectionName ->
								navigate(Screens.ListScreen(collectionName, ARTISTS))
							}
						)
					}
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

@Preview
@Composable
fun MediaScreenPreview() {
	OdiyoTheme {
		MediaScreen(
			uiState = MediaUiState(
				allSongs = State.Success(previewData),
				allAlbums = State.Success(previewAlbum),
				allArtists = State.Success(previewArtist)
			),
			inSelectMode = false,
			playAudio = { _, _, _, _ -> },
			menuAction = {},
			closeSelectionMode = {}
		) {}
	}
}