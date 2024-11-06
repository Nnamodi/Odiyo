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
import com.roland.android.domain.model.Artist
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.State
import com.roland.android.odiyo.mediaSource.previewArtist
import com.roland.android.odiyo.ui.components.EmptyListScreen
import com.roland.android.odiyo.ui.components.MediaImage
import com.roland.android.odiyo.ui.screens.CommonScreen
import com.roland.android.odiyo.ui.screens.LoadingListUi
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@Composable
fun ArtistsTab(
	allArtists: State<List<Artist>>,
	prepareAndViewSongs: (String) -> Unit
) {
	CommonScreen(
		state = allArtists,
		loadingScreen = { LoadingListUi(isSongList = false) }
	) { artists ->
		LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
			itemsIndexed(
				items = artists,
				key = { _, artist -> artist.uri }
			) { _, artist ->
				ArtistItem(artist, prepareAndViewSongs)
			}
		}

		if (artists.isEmpty()) {
			EmptyListScreen(
				text = stringResource(R.string.no_songs_text),
				isSongsScreen = true
			)
		}
	}
}

@Composable
fun ArtistItem(
	artist: Artist,
	prepareAndViewSongs: (String) -> Unit,
) {
	Row(
		modifier = Modifier
			.clickable { prepareAndViewSongs(artist.artist) }
			.fillMaxWidth()
			.padding(10.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		MediaImage(
			modifier = Modifier
				.padding(end = 8.dp)
				.size(50.dp),
			artwork = artist.artwork,
			placeholderRes = R.drawable.default_artist_art
		)
		Column(
			modifier = Modifier.fillMaxWidth(),
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = artist.artist,
				overflow = TextOverflow.Ellipsis,
				maxLines = 2
			)
			Text(
				text = pluralStringResource(R.plurals.number_of_songs, artist.numberOfTracks, artist.numberOfTracks),
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.bodySmall,
				modifier = Modifier.alpha(0.5f)
			)
		}
	}
}

@Preview
@Composable
fun ArtistsTabPreview() {
	OdiyoTheme {
		Surface(
			color = MaterialTheme.colorScheme.background
		) {
			ArtistsTab(State.Success(previewArtist)) {}
		}
	}
}