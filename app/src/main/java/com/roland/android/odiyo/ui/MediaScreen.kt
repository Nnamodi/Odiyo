package com.roland.android.odiyo.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.theme.OdiyoTheme
import com.roland.android.odiyo.viewmodel.OdiyoViewModel
import com.roland.android.odiyo.viewmodel.ViewModelFactory

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MediaScreen(viewModel: OdiyoViewModel = viewModel(factory = ViewModelFactory())) {
	val songs = viewModel.songs

	LazyColumn(
		modifier = Modifier.fillMaxSize()
	) {
		itemsIndexed(
			items = songs,
			key = { _, song -> song.uri }
		) { _, song ->
			MediaItem(song)
		}
	}
}

@Composable
fun MediaItem(song: Music) {
	Row(
		Modifier.fillMaxWidth()
	) {
		song.thumbnail?.let { Image(bitmap = it.asImageBitmap(), contentDescription = "song thumbnail") }
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(20.dp),
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Text(song.name)
			Text(song.title)
			Text(song.artist)
			Text(song.duration.toString())
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
fun MediaPreview() {
	OdiyoTheme {
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = MaterialTheme.colorScheme.background
		) {
			MediaScreen()
		}
	}
}