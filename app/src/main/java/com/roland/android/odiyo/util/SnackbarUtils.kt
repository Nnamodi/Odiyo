package com.roland.android.odiyo.util

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import com.roland.android.odiyo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object SnackbarUtils {

	fun showSnackbar(
		menuAction: MediaMenuActions,
		context: Context,
		scope: CoroutineScope,
		snackbarHostState: SnackbarHostState,
	) {
		val text: String = when (menuAction) {
			is MediaMenuActions.AddToQueue -> context.getString(R.string.added_to_queue)
			is MediaMenuActions.DeleteSong -> context.getString(R.string.deleted)
			is MediaMenuActions.PlayNext -> context.getString(R.string.added_to_queue)
			is MediaMenuActions.RenameSong -> context.getString(R.string.renamed)
			else -> ""
		}
		if (text.isNotBlank()) { scope.launch { snackbarHostState.showSnackbar(text) } }
	}
}