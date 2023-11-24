package com.roland.android.odiyo.ui.menu

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddToQueue
import androidx.compose.material.icons.rounded.Queue
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.states.MediaItemsUiState
import com.roland.android.odiyo.ui.menu.SongListMenu.AddToQueue
import com.roland.android.odiyo.ui.menu.SongListMenu.PlayNext
import com.roland.android.odiyo.ui.menu.SongListMenu.SortBy
import com.roland.android.odiyo.ui.menu.SongListMenu.values
import com.roland.android.odiyo.util.MediaMenuActions

@Composable
fun SongListMenu(
	uiState: MediaItemsUiState,
	menuAction: (MediaMenuActions) -> Unit,
	showSortAction: Boolean = true,
	openSortDialog: (Boolean) -> Unit,
	openMenu: (Boolean) -> Unit
) {
	val (_, collectionName, collectionType, _, _, songs) = uiState
	val positionX = LocalConfiguration.current.screenWidthDp
	val positionY = LocalConfiguration.current.screenHeightDp

	DropdownMenu(
		expanded = true,
		offset = DpOffset(positionX.dp, (-positionY).dp),
		onDismissRequest = { openMenu(false) },
		modifier = Modifier.fillMaxWidth(0.5f)
	) {
		val menuItems = values().toMutableList()
		if (!showSortAction || songs.size < 2) menuItems.remove(SortBy)

		menuItems.forEach { menu ->
			val action = { when (menu) {
				PlayNext -> menuAction(MediaMenuActions.PlayNext(songs, collectionType, collectionName))
				AddToQueue -> menuAction(MediaMenuActions.AddToQueue(songs, collectionType, collectionName))
				SortBy -> openSortDialog(true)
			} }

			DropdownMenuItem(
				text = { Text(stringResource(menu.menuText)) },
				onClick = { action(); openMenu(false) },
				leadingIcon = { Icon(menu.icon, null) }
			)
		}
	}
}

enum class SongListMenu(val icon: ImageVector, val menuText: Int) {
	PlayNext(Icons.Rounded.Queue, R.string.play_next),
	AddToQueue(Icons.Rounded.AddToQueue, R.string.add_to_queue),
	SortBy(Icons.Rounded.Sort, R.string.sort_by)
}