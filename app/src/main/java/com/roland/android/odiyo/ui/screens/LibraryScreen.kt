package com.roland.android.odiyo.ui.screens

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewData
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.service.Util.toMediaItem
import com.roland.android.odiyo.ui.components.MainAppBar
import com.roland.android.odiyo.ui.components.RecentSongItem
import com.roland.android.odiyo.ui.screens.Menus.*
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun LibraryScreen(
	songs: List<Music>,
	currentSongUri: MediaItem,
	playSong: (Uri, Int) -> Unit,
	navigateToMediaScreen: () -> Unit
) {
	Scaffold(
		topBar = { MainAppBar() }
	) {
		Column(
			modifier = Modifier
				.padding(it)
				.verticalScroll(rememberScrollState())
		) {
			val menus = values()

			menus.forEach { menu ->
				val action = { when (menu) {
					Recent -> {}
					Playlist -> {}
					Favorites -> {}
					Songs -> navigateToMediaScreen()
				} }
				MenuItem(menu.icon, menu.text, action)
			}

			Spacer(Modifier.height(16.dp))
			Text(
				text = stringResource(R.string.recently_added),
				modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
				style = MaterialTheme.typography.titleLarge
			)
			LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
				itemsIndexed(
					items = songs,
					key = { _, song -> song.id }
				) { index, song ->
					RecentSongItem(index, song, currentSongUri, playSong)
				}
			}
		}
	}
}

@Composable
fun MenuItem(icon: ImageVector?, text: Int, action: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(end = 40.dp)
			.clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
			.clickable { action() }
			.padding(horizontal = 30.dp, vertical = 20.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		if (icon != null) {
			Icon(imageVector = icon, contentDescription = null)
		}
		Text(
			text = stringResource(text),
			modifier = Modifier.padding(horizontal = 20.dp),
			style = MaterialTheme.typography.headlineSmall
		)
	}
}

private enum class Menus(val icon: ImageVector?, val text: Int) {
	Recent(Icons.Rounded.History, R.string.recent),
	Playlist(Icons.Rounded.QueueMusic, R.string.playlists),
	Favorites(Icons.Rounded.Favorite, R.string.favorites),
	Songs(Icons.Rounded.LibraryMusic, R.string.songs)
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Preview
@Composable
fun LibraryScreenPreview() {
	OdiyoTheme {
		LibraryScreen(
			songs = previewData.shuffled(),
			currentSongUri = previewData[4].uri.toMediaItem,
			playSong = { _, _ -> },
			navigateToMediaScreen = {}
		)
	}
}