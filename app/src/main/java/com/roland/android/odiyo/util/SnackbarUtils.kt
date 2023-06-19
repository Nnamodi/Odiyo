package com.roland.android.odiyo.util

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import com.roland.android.odiyo.R
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.ui.components.SelectionModeItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object SnackbarUtils {

	fun showSnackbar(
		menuAction: MediaMenuActions,
		context: Context,
		scope: CoroutineScope,
		snackbarHostState: SnackbarHostState,
		song: Music? = null
	) {
		val text: Int? = when (menuAction) {
			is MediaMenuActions.AddToQueue -> R.string.added_to_queue
			is MediaMenuActions.DeleteSongs -> R.string.deleted
			is MediaMenuActions.Favorite -> if (song?.favorite == true) R.string.added_to_favorites else R.string.removed_from_favorites
			is MediaMenuActions.AddToPlaylist -> R.string.added_to_playlist
			is MediaMenuActions.RemoveFromPlaylist -> R.string.removed
			is MediaMenuActions.PlayNext -> R.string.added_to_queue
			is MediaMenuActions.RenameSong -> R.string.renamed
			else -> null
		}
		text?.let { scope.launch { snackbarHostState.showSnackbar(context.getString(it)) } }
	}

	fun showSnackbar(
		selection: SelectionModeItems,
		context: Context,
		scope: CoroutineScope,
		snackbarHostState: SnackbarHostState,
		collectionIsPlaylist: Boolean = false
	) {
		val text: Int? = when (selection) {
			SelectionModeItems.AddToQueue -> R.string.added_to_queue
			SelectionModeItems.Delete -> if (collectionIsPlaylist) R.string.removed else R.string.deleted
			SelectionModeItems.PlayNext -> R.string.added_to_queue
			else -> null
		}
		text?.let { scope.launch { snackbarHostState.showSnackbar(context.getString(it)) } }
	}

	fun showSnackbar(
		playlistAction: PlaylistMenuActions,
		context: Context,
		scope: CoroutineScope,
		snackbarHostState: SnackbarHostState
	) {
		val text: Int? = when (playlistAction) {
			is PlaylistMenuActions.AddToQueue -> R.string.added_to_queue
			is PlaylistMenuActions.DeletePlaylist -> R.string.deleted
			is PlaylistMenuActions.PlayNext -> R.string.added_to_queue
			is PlaylistMenuActions.RenamePlaylist -> R.string.renamed
			else -> null
		}
		text?.let { scope.launch { snackbarHostState.showSnackbar(context.getString(it)) } }
	}
}