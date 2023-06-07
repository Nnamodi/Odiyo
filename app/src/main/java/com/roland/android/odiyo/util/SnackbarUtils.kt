package com.roland.android.odiyo.util

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import com.roland.android.odiyo.R
import com.roland.android.odiyo.model.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object SnackbarUtils {

	fun showSnackbar(
		menuAction: MediaMenuActions,
		context: Context,
		scope: CoroutineScope,
		snackbarHostState: SnackbarHostState,
		song: Music
	) {
		val text: Int? = when (menuAction) {
			is MediaMenuActions.AddToQueue -> R.string.added_to_queue
			is MediaMenuActions.DeleteSong -> R.string.deleted
			is MediaMenuActions.Favorite -> if (song.favorite) R.string.added_to_favorites else R.string.removed_from_favorites
			is MediaMenuActions.PlayNext -> R.string.added_to_queue
			is MediaMenuActions.RenameSong -> R.string.renamed
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