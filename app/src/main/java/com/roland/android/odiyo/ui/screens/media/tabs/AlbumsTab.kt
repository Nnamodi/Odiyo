package com.roland.android.odiyo.ui.screens.media.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roland.android.domain.model.Album
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.State
import com.roland.android.odiyo.mediaSource.previewAlbum
import com.roland.android.odiyo.ui.components.EmptyListScreen
import com.roland.android.odiyo.ui.components.MediaImage
import com.roland.android.odiyo.ui.screens.CommonScreen
import com.roland.android.odiyo.ui.screens.LoadingListUi
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@Composable
fun AlbumsTab(
	allAlbums: State<List<Album>>,
	prepareAndViewSongs: (String) -> Unit,
) {
	CommonScreen(
		state = allAlbums,
		loadingScreen = { LoadingListUi(isSongList = false) }
	) { albums ->
		LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
			itemsIndexed(
				items = albums,
				key = { _, album -> album.uri }
			) { _, album ->
				AlbumItem(album, prepareAndViewSongs)
			}
		}

		if (albums.isEmpty()) {
			EmptyListScreen(
				text = stringResource(R.string.no_songs_text),
				isSongsScreen = true
			)
		}
	}
}

@Composable
fun AlbumItem(
	album: Album,
	prepareAndViewSongs: (String) -> Unit,
) {

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
				.size(50.dp),
			artwork = album.artwork,
			placeholderRes = R.drawable.default_album_art
		)
		Column(
			modifier = Modifier.fillMaxWidth(),
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = album.album,
				overflow = TextOverflow.Ellipsis,
				maxLines = 2
			)
			Text(
				text = pluralStringResource(R.plurals.number_of_songs, album.numberOfSongs, album.numberOfSongs),
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.bodySmall,
				modifier = Modifier.alpha(0.5f)
			)
		}
	}
}

@Preview
@Composable
fun AlbumsTabPreview() {
	OdiyoTheme {
		Surface(
			color = MaterialTheme.colorScheme.background
		) {
			AlbumsTab(State.Success(previewAlbum)) {}
		}
	}
}