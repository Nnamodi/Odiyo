package com.roland.android.odiyo.ui.screens

import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddBox
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
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
import com.roland.android.odiyo.ui.dialog.CreateOrRenamePlaylistDialog
import com.roland.android.odiyo.ui.navigation.PLAYLISTS
import com.roland.android.odiyo.ui.sheets.PlaylistItemSheet
import com.roland.android.odiyo.ui.theme.OdiyoTheme
import com.roland.android.odiyo.util.PlaylistMenuActions
import com.roland.android.odiyo.util.SnackbarUtils

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PlaylistsScreen(
	playlists: List<Playlist>,
	playlistAction: (PlaylistMenuActions) -> Unit,
	prepareAndViewSongs: (String, String) -> Unit,
	navigateUp: () -> Unit
) {
	val context = LocalContext.current
	val snackbarHostState = remember { SnackbarHostState() }
	val scope = rememberCoroutineScope()
	val openPlaylistDialog = remember { mutableStateOf(false) }
	val openMenuSheet = remember { mutableStateOf(false) }
	var clickedPlaylist by remember { mutableStateOf<Playlist?>(null) }

	Scaffold(
		topBar = {
			MediaItemsAppBar(
				collectionName = stringResource(R.string.playlists),
				songsNotEmpty = false,
				navigateUp = navigateUp
			) {}
		},
		snackbarHost = {
			SnackbarHost(snackbarHostState, Modifier.absoluteOffset(y = (-80).dp)) {
				Snackbar(Modifier.padding(horizontal = 16.dp)) {
					Text(it.visuals.message)
				}
			}
		}
	) { paddingValues ->
		Column(Modifier.padding(paddingValues)) {
			LazyColumn {
				item { CreatePlaylistButton { openPlaylistDialog.value = true } }
				itemsIndexed(
					items = playlists,
					key = { _, playlist -> playlist.id }
				) { _, playlist ->
					PlaylistItem(
						playlist = playlist,
						onItemClick = prepareAndViewSongs,
						openMenuSheet = {
							clickedPlaylist = it
							openMenuSheet.value = true
						}
					)
				}
			}
		}
	}

	if (openPlaylistDialog.value) {
		CreateOrRenamePlaylistDialog(
			playlist = null,
			listOfPlaylists = playlists,
			openPlaylist = prepareAndViewSongs,
			dialogAction = { action, _ ->
				playlistAction(action)
				SnackbarUtils.showSnackbar(action, context, scope, snackbarHostState)
			}
		) { openPlaylistDialog.value = it }
	}

	if (openMenuSheet.value && clickedPlaylist != null) {
		PlaylistItemSheet(
			playlist = clickedPlaylist!!,
			listOfPlaylists = playlists,
			scaffoldState = rememberModalBottomSheetState(true),
			openBottomSheet = { openMenuSheet.value = it },
			openPlaylist = prepareAndViewSongs,
			playlistMenuAction = {
				playlistAction(it)
				SnackbarUtils.showSnackbar(it, context, scope, snackbarHostState)
			}
		)
	}
}

@Composable
fun CreatePlaylistButton(openDialog: () -> Unit) {
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
fun PlaylistItem(
	modifier: Modifier = Modifier,
	playlist: Playlist,
	onItemClick: (String, String) -> Unit,
	parentContentIsDialog: Boolean = false,
	openMenuSheet: (Playlist) -> Unit
) {
	val context = LocalContext.current

	Row(
		modifier = modifier
			.clickable { onItemClick(playlist.name, PLAYLISTS) }
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.background)
			.padding(start = 10.dp, top = 10.dp, bottom = 10.dp, end = if (parentContentIsDialog) 10.dp else 0.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		MediaImage(
			modifier = Modifier
				.padding(end = 8.dp)
				.size(50.dp),
			artwork = playlist.getBitmap(context),
			placeholderRes = R.drawable.default_playlist_art
		)
		Column(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = playlist.name,
				overflow = TextOverflow.Ellipsis,
				maxLines = 2
			)
			Text(
				text = pluralStringResource(R.plurals.number_of_songs, playlist.numOfSongs(), playlist.numOfSongs()),
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.bodySmall,
				modifier = Modifier.alpha(0.5f)
			)
		}
		if (!parentContentIsDialog) {
			IconButton(onClick = { openMenuSheet(playlist) }) {
				Icon(Icons.Rounded.MoreVert, stringResource(R.string.more_options))
			}
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
fun PlaylistsScreenPreview() {
	OdiyoTheme {
		PlaylistsScreen(
			playlists = previewPlaylist,
			playlistAction = {},
			prepareAndViewSongs = { _, _ -> }
		) {}
	}
}