package com.roland.android.odiyo.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@RequiresApi(Build.VERSION_CODES.Q)
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun SelectionModeBottomBar(
	inSelectionMode: Boolean,
	onClick: (SelectionModeItems) -> Unit
) {
	val items = SelectionModeItems.values()

	AnimatedVisibility(
		visible = inSelectionMode,
		enter = slideInVertically(initialOffsetY = { it }),
		exit = slideOutVertically(targetOffsetY = { it })
	) {
		NavigationBar {
			items.forEach { item ->
				NavigationBarItem(
					icon = { Icon(item.icon, null) },
					label = { Text(stringResource(item.titleRes)) },
					selected = false,
					onClick = { onClick(item) }
				)
			}
		}
	}
}

enum class SelectionModeItems(val titleRes: Int, val icon: ImageVector) {
	PlayNext(R.string.play_next, Icons.Rounded.Queue),
	AddToQueue(R.string.queue_up, Icons.Rounded.AddToQueue),
	AddToPlaylist(R.string.add, Icons.Rounded.PlaylistAdd),
	Share(R.string.share, Icons.Rounded.Share),
	Delete(R.string.delete, Icons.Rounded.Delete)
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun SelectionModeBottomBarPreview() {
	OdiyoTheme {
		Column(
			modifier = Modifier.fillMaxSize(),
			verticalArrangement = Arrangement.Bottom
		) {
			SelectionModeBottomBar(true) {}
		}
	}
}