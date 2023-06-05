package com.roland.android.odiyo.ui.screens

import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddBox
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewPlaylist
import com.roland.android.odiyo.model.Playlist
import com.roland.android.odiyo.service.Util.getBitmap
import com.roland.android.odiyo.ui.components.MediaImage
import com.roland.android.odiyo.ui.components.MediaItemsAppBar
import com.roland.android.odiyo.ui.dialog.CreatePlaylistDialog
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PlaylistsScreen(
	playlists: List<Playlist>,
	prepareAndViewSongs: (Playlist) -> Unit,
	navigateUp: () -> Unit
) {
	val openPlaylistDialog = remember { mutableStateOf(false) }

	Scaffold(
		topBar = {
			MediaItemsAppBar(
				collectionName = stringResource(R.string.playlists),
				navigateUp = navigateUp,
				songsNotEmpty = false,
				openMenu = {}
			)
		}
	) {
		Column(Modifier.padding(it)) {
			LazyColumn {
				item { CreatePlaylistButton { openPlaylistDialog.value = true } }
				itemsIndexed(
					items = playlists,
					key = { _, playlist -> playlist.id }
				) { _, playlist ->
					PlaylistItem(playlist, prepareAndViewSongs)
				}
			}
		}
	}

	if (openPlaylistDialog.value) {
		CreatePlaylistDialog(
			createPlaylist = {},
			openDialog = { openPlaylistDialog.value = it }
		)
	}
}

@Composable
private fun CreatePlaylistButton(openDialog: () -> Unit) {
	TextButton(
		modifier = Modifier.padding(4.dp),
		shape = MaterialTheme.shapes.small,
		contentPadding = PaddingValues(6.dp),
		onClick = openDialog
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(
				imageVector = Icons.Rounded.AddBox,
				contentDescription = stringResource(R.string.playlist_art_desc),
				modifier = Modifier.size(50.dp)
			)
			Text(
				text = stringResource(R.string.create_playlist),
				color = MaterialTheme.colorScheme.onBackground,
				modifier = Modifier.padding(horizontal = 8.dp),
				style = MaterialTheme.typography.bodyLarge
			)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(UnstableApi::class)
@Composable
private fun PlaylistItem(playlist: Playlist, prepareAndViewSongs: (Playlist) -> Unit) {
	val context = LocalContext.current

	Row(
		modifier = Modifier
			.clickable { prepareAndViewSongs(playlist) }
			.fillMaxWidth()
			.padding(10.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		MediaImage(
			modifier = Modifier
				.padding(end = 8.dp)
				.size(50.dp),
			artwork = playlist.songs.last().getBitmap(context),
			descriptionRes = R.string.playlist_art_desc,
			placeholderRes = R.drawable.default_playlist_art
		)
		Column(
			modifier = Modifier.fillMaxWidth(),
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = playlist.name,
				overflow = TextOverflow.Ellipsis,
				maxLines = 2
			)
			Text(
				text = stringResource(R.string.number_of_songs, playlist.songs.size),
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.bodySmall,
				modifier = Modifier.alpha(0.5f)
			)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
fun PlaylistsScreenPreview() {
	OdiyoTheme {
		PlaylistsScreen(previewPlaylist, {}) {}
	}
}