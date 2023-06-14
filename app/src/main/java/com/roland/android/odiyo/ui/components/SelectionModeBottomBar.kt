package com.roland.android.odiyo.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.NavigationBarDefaults.containerColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

	if (inSelectionMode) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.background(containerColor)
				.padding(vertical = 10.dp)
				.horizontalScroll(rememberScrollState()),
			horizontalArrangement = Arrangement.SpaceAround
		) {
			items.forEach { item ->
				SelectionItems(
					nameRes = item.titleRes,
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