package com.roland.android.odiyo.ui.screens

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewArtist
import com.roland.android.odiyo.model.Artist
import com.roland.android.odiyo.service.Util.getBitmap
import com.roland.android.odiyo.ui.components.EmptyListScreen
import com.roland.android.odiyo.ui.components.MediaImage
import com.roland.android.odiyo.ui.theme.OdiyoTheme

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun ArtistsScreen(
	artists: List<Artist>,
	prepareAndViewSongs: (String) -> Unit,
) {
	if (artists.isEmpty()) {
		EmptyListScreen(text = stringResource(R.string.no_songs_text), isSongsScreen = true)
	} else {
		LazyColumn {
			itemsIndexed(
				items = artists,
				key = { _, artist -> artist.uri }
			) { _, artist ->
				ArtistItem(artist, prepareAndViewSongs)
			}
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Composable
fun ArtistItem(
	artist: Artist,
	prepareAndViewSongs: (String) -> Unit,
) {
	val context = LocalContext.current

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
			artwork = artist.getBitmap(context),
			descriptionRes = R.string.artist_art_desc,
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
				text = artist.numberOfTracks,
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.bodySmall,
				modifier = Modifier.alpha(0.5f)
			)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
@Preview
@Composable
fun ArtistsScreenPreview() {
	OdiyoTheme {
		Surface(
			color = MaterialTheme.colorScheme.background
		) {
			ArtistsScreen(previewArtist) {}
		}
	}
}