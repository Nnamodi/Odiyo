package com.roland.android.odiyo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType.Companion.Sp
import androidx.compose.ui.unit.dp
import com.roland.android.odiyo.theme.OdiyoTheme
import com.roland.android.odiyo.ui.MenuItems.*

@ExperimentalMaterial3Api
@Composable
fun MediaItemSheet(
	scaffoldState: SheetState,
	openBottomSheet: (Boolean) -> Unit,
	menuAction: (Int) -> Unit,
) {
	ModalBottomSheet(
		onDismissRequest = { openBottomSheet(false) },
		sheetState = scaffoldState,
	) {
		val menuItems = listOf(PlayNext, Rename, Share, Details, Delete)

		Column(Modifier.padding(bottom = 20.dp)) {
			menuItems.forEachIndexed { index, menu ->
				SheetItem(menu.icon, menu.menuText) { menuAction(index) }
			}
		}
	}

	BackHandler { openBottomSheet(false) }
}

@Composable
fun SheetItem(icon: ImageVector, menuText: String, action: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { action() }
			.padding(20.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(modifier = Modifier.padding(start = 14.dp), imageVector = icon, contentDescription = null)
		Spacer(Modifier.width(20.dp))
		Text(
			text = menuText,
			fontSize = TextUnit(20f, Sp)
		)
	}
}

enum class MenuItems(
	val icon: ImageVector,
	val menuText: String
) {
	PlayNext(Icons.Rounded.PlaylistAdd, "Play next"),
	Rename(Icons.Rounded.Edit, "Rename"),
	Share(Icons.Rounded.Share, "Share"),
	Details(Icons.Rounded.Info, "Details"),
	Delete(Icons.Rounded.Delete, "Delete")
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SheetPreview() {
	OdiyoTheme {
		val sheetState = rememberModalBottomSheetState()
		val openBottomSheet = remember { mutableStateOf(true) }

		Column(
			modifier = Modifier
				.clickable { openBottomSheet.value = true }
				.fillMaxSize()
		) {
			if (openBottomSheet.value) {
				MediaItemSheet(
					scaffoldState = sheetState,
					openBottomSheet = { openBottomSheet.value = it },
					menuAction = {}
				)
			}
		}
	}
}