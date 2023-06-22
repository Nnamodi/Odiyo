package com.roland.android.odiyo.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
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
fun MainAppBar() {
	TopAppBar(
		title = {
			Text(
				text = stringResource(R.string.app_name),
				fontStyle = FontStyle.Italic,
				fontWeight = FontWeight.Bold
			)
		}
	)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppBar(navigateUp: () -> Unit, navigateToSearch: () -> Unit) {
	TopAppBar(
		title = {},
		navigationIcon = {
			IconButton(onClick = navigateUp) {
				Icon(Icons.Rounded.ArrowBackIosNew, stringResource(R.string.back_icon_desc))
			}
		},
		actions = {
			IconButton(onClick = navigateToSearch) {
				Icon(
					imageVector = Icons.Rounded.Search,
					contentDescription = stringResource(R.string.search_icon_desc),
					tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 1f)
				)
			}
		}
	)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
						Modifier.alpha(0.6f).basicMarquee(), Arrangement.Center, Alignment.CenterVertically
					) {
						Icon(Icons.Rounded.Search, null)
						Text(stringResource(R.string.search), Modifier.padding(start = 4.dp), softWrap = false)
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
	componentColor: Color,
	goToCollection: (String, String) -> Unit,
	navigateUp: () -> Unit
) {
	CenterAlignedTopAppBar(
		navigationIcon = {
			IconButton(onClick = navigateUp) {
				Icon(
					imageVector = Icons.Rounded.ArrowBackIosNew,
					contentDescription = stringResource(R.string.back_icon_desc),
					modifier = Modifier.rotate(-90f),
					tint = componentColor
				)
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
						color = componentColor,
						modifier = Modifier.alpha(0.8f),
						style = MaterialTheme.typography.titleSmall
					)
					Text(
						text = song.album,
						color = componentColor,
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
	collectionIsPlaylist: Boolean = false,
	songsNotEmpty: Boolean,
	addSongs: (String) -> Unit = {},
	navigateUp: () -> Unit,
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
				if (collectionIsPlaylist) {
					IconButton(onClick = { addSongs(collectionName) }) {
						Icon(
							imageVector = Icons.Rounded.Add,
							contentDescription = stringResource(R.string.add_songs),
							tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 1f)
						)
					}
				}
				IconButton(onClick = openMenu) {
					Icon(
						imageVector = Icons.Rounded.MoreVert,
						contentDescription = stringResource(R.string.more_options),
						tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 1f)
					)
				}
			}
		}
	)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SelectionModeTopBar(
	numOfSelectedSongs: Int,
	showAddButton: Boolean = false,
	addSongs: () -> Unit = {},
	closeSelectionMode: () -> Unit
) {
	TopAppBar(
		title = { Text(
			text = pluralStringResource(R.plurals.number_of_songs, numOfSelectedSongs, numOfSelectedSongs),
			overflow = TextOverflow.Ellipsis, softWrap = false
		) },
		navigationIcon = {
			IconButton(onClick = closeSelectionMode) {
				Icon(Icons.Rounded.Close, stringResource(R.string.close))
			}
		},
		actions = {
			if (showAddButton) {
				Button(
					onClick = addSongs, enabled = numOfSelectedSongs > 0,
					modifier = Modifier.padding(end = 4.dp),
					contentPadding = PaddingValues(12.dp, 0.dp)
				) { Text(stringResource(R.string.add)) }
			}
		}
	)
}