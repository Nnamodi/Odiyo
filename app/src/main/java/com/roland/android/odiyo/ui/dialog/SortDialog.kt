package com.roland.android.odiyo.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.ui.components.DialogButtonText
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@Composable
fun SortDialog(
	selectedOption: SortOptions,
	onSortPicked: (SortOptions) -> Unit,
	openDialog: (Boolean) -> Unit
) {
	val sortOptions = SortOptions.values()

	AlertDialog(
		onDismissRequest = { openDialog(false) },
		title = {
			Text(text = stringResource(R.string.sort_by))
		},
		text = {
			Column {
				sortOptions.forEach { option ->
					Option(
						option = stringResource(option.title),
						selected = selectedOption == option
					) {
						onSortPicked(option)
						openDialog(false)
					}
				}
			}
		},
		confirmButton = {
			TextButton(onClick = { openDialog(false) }) {
				DialogButtonText(stringResource(R.string.close))
			}
		}
	)
}

@Composable
fun Option(
	option: String,
	selected: Boolean = false,
	action: () -> Unit
) {
	val color = if (selected) MaterialTheme.colorScheme.primary else AlertDialogDefaults.textContentColor

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clip(MaterialTheme.shapes.medium)
			.clickable { action() }
			.padding(horizontal = 8.dp, vertical = 10.dp),
		horizontalArrangement = Arrangement.Start,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = option,
			modifier = Modifier
				.weight(1f)
				.padding(vertical = 2.dp),
			color = color,
			style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Normal)
		)
		if (selected) {
			Icon(
				imageVector = Icons.Rounded.Done,
				contentDescription = stringResource(R.string.sort_option_selected, option),
				tint = color
			)
		}
	}
}

enum class SortOptions(val title: Int) {
	NameAZ(R.string.name_a_z),
	NameZA(R.string.name_z_a),
	NewestFirst(R.string.newest_first),
	OldestFirst(R.string.oldest_first)
}

@Preview
@Composable
fun SortDialogPreview() {
	OdiyoTheme {
		Column(Modifier.fillMaxSize()) {
			SortDialog(SortOptions.NameAZ, {}) {}
		}
	}
}