package com.roland.android.odiyo.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.mediaSource.previewArtist
import com.roland.android.odiyo.model.Artist
import com.roland.android.odiyo.theme.OdiyoTheme

@Composable
fun ArtistsScreen(
	artists: List<Artist>,
	prepareAndViewSongs: (String) -> Unit,
) {
	LazyColumn {
		itemsIndexed(
			items = artists,
			key = { _, artist -> artist.id }
		) { _, artist ->
			ArtistItem(artist, prepareAndViewSongs)
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
		Image(
			painter = painterResource(R.drawable.default_artist_art),
			contentDescription = "artist thumbnail",
			contentScale = ContentScale.Crop,
			modifier = Modifier
				.padding(end = 8.dp)
				.size(70.dp)
		)
		Column(
			modifier = Modifier.fillMaxWidth(),
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = artist.artist,
				overflow = TextOverflow.Ellipsis,
				fontWeight = FontWeight.Bold,
				maxLines = 2
			)
			Text(
				text = artist.numberOfTracks,
				overflow = TextOverflow.Ellipsis,
				fontWeight = FontWeight.Light
			)
		}
	}
}

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