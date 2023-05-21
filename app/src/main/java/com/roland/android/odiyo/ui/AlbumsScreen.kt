package com.roland.android.odiyo.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewAlbum
import com.roland.android.odiyo.model.Album
import com.roland.android.odiyo.service.Util.getBitmap
import com.roland.android.odiyo.ui.components.MediaImage
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun AlbumsScreen(
	albums: List<Album>,
	prepareAndViewSongs: (String) -> Unit,
) {
	LazyColumn {
		itemsIndexed(
			items = albums,
			key = { _, album -> album.uri }
		) { _, album ->
			AlbumItem(album, prepareAndViewSongs)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun AlbumItem(
	album: Album,
	prepareAndViewSongs: (String) -> Unit,
) {
	val context = LocalContext.current

	Row(
		modifier = Modifier
			.clickable { prepareAndViewSongs(album.album) }
			.fillMaxWidth()
			.padding(10.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		MediaImage(
			modifier = Modifier
				.padding(end = 8.dp)
				.size(70.dp),
			artwork = album.getBitmap(context),
			descriptionRes = R.string.album_art_desc,
			placeholderRes = R.drawable.default_album_art
		)
		Column(
			modifier = Modifier.fillMaxWidth(),
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = album.album,
				overflow = TextOverflow.Ellipsis,
				fontWeight = FontWeight.Bold,
				maxLines = 2
			)
			Text(
				text = album.numberOfSongs,
				overflow = TextOverflow.Ellipsis,
				fontWeight = FontWeight.Light
			)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Preview
@Composable
fun AlbumsScreenPreview() {
	OdiyoTheme {
		Surface(
			color = MaterialTheme.colorScheme.background
		) {
			AlbumsScreen(previewAlbum) {}
		}
	}
}