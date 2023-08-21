package com.roland.android.odiyo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults.containerColor
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.ui.components.SelectionModeItems.Delete
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@Composable
fun SelectionModeBottomBar(
	inSelectionMode: Boolean,
	isSongsScreen: Boolean = false,
	collectionIsPlaylist: Boolean = false,
	onClick: (SelectionModeItems) -> Unit
) {
	val items = SelectionModeItems.values()
	val navigationBarHeight = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 10.dp
	val bottomPadding = if (inSelectionMode && isSongsScreen) 10.dp else navigationBarHeight

	if (inSelectionMode) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.background(containerColor)
				.padding(top = 10.dp, bottom = bottomPadding)
				.horizontalScroll(rememberScrollState()),
			horizontalArrangement = Arrangement.SpaceAround
		) {
			items.forEach { item ->
				val title = if (item == Delete && collectionIsPlaylist) R.string.remove else item.titleRes

				SelectionItems(
					nameRes = title,
					icon = item.icon,
					onClick = { onClick(item) }
				)
			}
		}
	}
}

@Composable
fun SelectionItems(nameRes: Int, icon: ImageVector, onClick: () -> Unit) {
	val contentColor = MaterialTheme.colorScheme.contentColorFor(containerColor)

	Column(
		modifier = Modifier
			.clip(MaterialTheme.shapes.small)
			.clickable { onClick() }
			.padding(vertical = 6.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Icon(
			imageVector = icon,
			contentDescription = null,
			modifier = Modifier.padding(horizontal = 20.dp),
			tint = contentColor
		)
		Text(
			text = stringResource(nameRes),
			color = contentColor,
			modifier = Modifier.padding(top = 6.dp),
			overflow = TextOverflow.Ellipsis,
			style = MaterialTheme.typography.labelMedium
		)
	}
}

enum class SelectionModeItems(val titleRes: Int, val icon: ImageVector) {
	PlayNext(R.string.play_next, Icons.Rounded.Queue),
	AddToQueue(R.string.queue_up, Icons.Rounded.AddToQueue),
	AddToPlaylist(R.string.add, Icons.Rounded.PlaylistAdd),
	Share(R.string.share, Icons.Rounded.Share),
	Delete(R.string.delete, Icons.Rounded.Delete)
}

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