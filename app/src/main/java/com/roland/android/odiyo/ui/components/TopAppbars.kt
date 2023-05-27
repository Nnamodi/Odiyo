package com.roland.android.odiyo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.roland.android.odiyo.R
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.ui.navigation.ALBUMS

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainAppBar(navigateToSearch: () -> Unit) {
	TopAppBar(
		title = {
			Text(
				text = stringResource(R.string.app_name),
				fontStyle = FontStyle.Italic,
				fontWeight = FontWeight.Bold
			)
		},
		actions = {
			IconButton(onClick = navigateToSearch) {
				Icon(
					imageVector = Icons.Rounded.Search,
					contentDescription = stringResource(R.string.search_icon_desc),
					tint = LocalContentColor.current.copy(alpha = 1f)
				)
			}
		}
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
	query: String,
	onTextChange: (String) -> Unit,
	clearSearchQuery: () -> Unit,
	closeSearchScreen: () -> Unit
) {
	TopAppBar(
		title = {
			OutlinedTextField(
				modifier = Modifier.fillMaxWidth(),
				value = query,
				onValueChange = onTextChange,
				placeholder = {
					Row(
						Modifier.alpha(0.6f), Arrangement.Center, Alignment.CenterVertically
					) {
						Icon(Icons.Rounded.Search, null)
						Text(stringResource(R.string.search), Modifier.padding(start = 4.dp))
					}
				},
				trailingIcon = {
					if (query.isNotEmpty()) {
						IconButton(onClick = clearSearchQuery) {
							Icon(Icons.Rounded.Clear, stringResource(R.string.clear_icon_desc))
						}
					}
				},
				singleLine = true,
				shape = MaterialTheme.shapes.large
			)
		},
		navigationIcon = {
			IconButton(onClick = closeSearchScreen) {
				Icon(Icons.Rounded.ArrowBackIosNew, stringResource(R.string.back_icon_desc))
			}
		}
	)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NowPlayingTopAppBar(
	song: Music?,
	goToCollection: (String, String) -> Unit,
	navigateUp: () -> Unit
) {
	CenterAlignedTopAppBar(
		navigationIcon = {
			IconButton(onClick = navigateUp) {
				Icon(Icons.Rounded.ArrowBackIosNew, stringResource(R.string.back_icon_desc), Modifier.rotate(-90f))
			}
		},
		title = {
			if (song != null) {
				Column(
					modifier = Modifier
						.padding(4.dp)
						.clip(MaterialTheme.shapes.small)
						.clickable { goToCollection(song.album, ALBUMS) }
						.fillMaxWidth(0.75f)
						.padding(4.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = stringResource(R.string.playing_from_album),
						modifier = Modifier.alpha(0.8f),
						style = MaterialTheme.typography.titleSmall
					)
					Text(
						text = song.album,
						overflow = TextOverflow.Ellipsis,
						softWrap = false,
						style = MaterialTheme.typography.titleMedium
					)
				}
			}
		},
		colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
	)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MediaItemsAppBar(
	collectionName: String,
	navigateUp: () -> Unit,
	songsNotEmpty: Boolean,
	openMenu: () -> Unit
) {
	TopAppBar(
		title = { Text(text = collectionName, overflow = TextOverflow.Ellipsis, softWrap = false) },
		navigationIcon = {
			IconButton(onClick = navigateUp) {
				Icon(Icons.Rounded.ArrowBackIosNew, stringResource(R.string.back_icon_desc))
			}
		},
		actions = {
			if (songsNotEmpty) {
				IconButton(onClick = openMenu) {
					Icon(Icons.Rounded.MoreVert, stringResource(R.string.more_options))
				}
			}
		}
	)
}