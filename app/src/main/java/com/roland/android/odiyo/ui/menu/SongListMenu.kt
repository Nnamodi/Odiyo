package com.roland.android.odiyo.ui.menu

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Queue
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
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.ui.menu.SongListMenu.AddToQueue
import com.roland.android.odiyo.ui.menu.SongListMenu.PlayNext
import com.roland.android.odiyo.util.MediaMenuActions

@Composable
fun SongListMenu(
	songs: List<Music>,
	menuAction: (MediaMenuActions) -> Unit,
	yOffset: Int = 0,
	openMenu: (Boolean) -> Unit
) {
	val positionX = LocalConfiguration.current.screenWidthDp
	val positionY = LocalConfiguration.current.screenHeightDp

	DropdownMenu(
		expanded = true,
		offset = DpOffset(positionX.dp, (yOffset - positionY).dp),
		onDismissRequest = { openMenu(false) },
		modifier = Modifier.fillMaxWidth(0.5f)
	) {
		val menuItems = SongListMenu.values()

		menuItems.forEach { menu ->
			val action = when (menu) {
				PlayNext -> MediaMenuActions.PlayNext(songs)
				AddToQueue -> MediaMenuActions.AddToQueue(songs)
			}

			DropdownMenuItem(
				text = { Text(stringResource(menu.menuText)) },
				onClick = { menuAction(action); openMenu(false) },
				leadingIcon = { Icon(menu.icon, null) }
			)
		}
	}
}

enum class SongListMenu(val icon: ImageVector, val menuText: Int) {
	PlayNext(Icons.Rounded.Queue, R.string.play_next),
	AddToQueue(Icons.Rounded.PlaylistAdd, R.string.add_to_queue)
}