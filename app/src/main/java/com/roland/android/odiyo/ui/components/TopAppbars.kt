package com.roland.android.odiyo.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.core.net.toUri
import com.roland.android.odiyo.R
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.states.MediaItemsUiState
import com.roland.android.odiyo.ui.navigation.ALBUMS
import com.roland.android.odiyo.ui.theme.color.CustomColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainAppBar(navigateToSettings: () -> Unit) {
	TopAppBar(
		title = {
			Text(
				text = stringResource(R.string.app_name),
				fontStyle = FontStyle.Italic,
				fontWeight = FontWeight.Bold
			)
		},
		actions = {
			IconButton(onClick = navigateToSettings) {
				Icon(
					imageVector = Icons.Rounded.Settings,
					contentDescription = stringResource(id = R.string.settings),
					tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 1f)
				)
			}
		}
	)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppBar(
	navigateUp: () -> Unit,
	navigateToSearch: () -> Unit = {},
	title: String? = null
) {
	TopAppBar(
		title = { title?.let { Text(it) } },
		navigationIcon = {
			IconButton(onClick = navigateUp) {
				Icon(Icons.Rounded.ArrowBackIosNew, stringResource(R.string.back_icon_desc))
			}
		},
		actions = {
			if (title == null) {
				IconButton(onClick = navigateToSearch) {
					Icon(
						imageVector = Icons.Rounded.Search,
						contentDescription = stringResource(R.string.search_icon_desc),
						tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 1f)
					)
				}
			}
		}
	)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NowPlayingTopAppBar(
	song: Music?,
	backgroundColor: Color,
	componentColor: Color,
	goToCollection: (String, String) -> Unit,
	navigateUp: () -> Unit,
	openMoreOptions: () -> Unit
) {
	val interactionSource = remember { MutableInteractionSource() }
	val ripple = rememberRipple(color = CustomColors.rippleColor(backgroundColor))

	CenterAlignedTopAppBar(
		navigationIcon = {
			NowPlayingIconButton(onClick = navigateUp, color = backgroundColor) {
				Icon(
					imageVector = Icons.Rounded.ArrowBackIosNew,
					contentDescription = stringResource(R.string.back_icon_desc),
					modifier = Modifier.rotate(-90f)
				)
			}
		},
		title = {
			if (song != null) {
				Column(
					modifier = Modifier
						.padding(4.dp)
						.clip(MaterialTheme.shapes.small)
						.clickable(
							interactionSource = interactionSource,
							indication = ripple,
							enabled = song.uri != "".toUri()
						) { goToCollection(song.album, ALBUMS) }
						.fillMaxWidth(0.75f)
						.padding(4.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = stringResource(R.string.playing_from_album),
						color = componentColor, modifier = Modifier.alpha(0.8f),
						style = MaterialTheme.typography.titleSmall
					)
					Text(
						text = song.album, color = componentColor,
						overflow = TextOverflow.Ellipsis, softWrap = false,
						style = MaterialTheme.typography.titleMedium
					)
				}
			}
		},
		colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
		actions = {
			if (!(song?.uri == "".toUri() || song == null)) {
				NowPlayingIconButton(onClick = openMoreOptions, color = backgroundColor) {
					Icon(Icons.Rounded.MoreVert, stringResource(R.string.more_options))
				}
			}
		}
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
	isSongsScreen: Boolean = false,
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
		},
		windowInsets = if (isSongsScreen) WindowInsets(0, 0, 0, 0) else TopAppBarDefaults.windowInsets
	)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchBar(
	uiState: MediaItemsUiState,
	onSearch: (String) -> Unit,
	closeSearchScreen: () -> Unit,
	openMenu: () -> Unit
) {
	val (_, _, _, searchQuery, history, searchResult, allSongs) = uiState
	var active by rememberSaveable { mutableStateOf(false) }
	var query by rememberSaveable { mutableStateOf(searchQuery) }
	val paddingValue by animateDpAsState(if (active) 0.dp else 10.dp, label = "padding value")
	val scope = rememberCoroutineScope()
	val search: (String) -> Job = {
		scope.launch {
			query = it.trim(); active = false
			onSearch(query)
		}
	}

	SearchBar(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = paddingValue),
		query = query,
		onQueryChange = { query = it },
		onSearch = { search(it) },
		active = active,
		onActiveChange = { active = it; if (!it && query.isEmpty()) query = searchQuery },
		placeholder = {
			Row(
				Modifier
					.alpha(0.6f)
					.basicMarquee(), Arrangement.Center, Alignment.CenterVertically
			) {
				Icon(Icons.Rounded.Search, null)
				Text(stringResource(R.string.search), Modifier.padding(start = 4.dp), softWrap = false)
			}
		},
		leadingIcon = {
			IconButton(
				onClick = { if (active) {
					active = false; if (query.isEmpty()) query = searchQuery
				} else closeSearchScreen() }
			) {
				Icon(Icons.Rounded.ArrowBackIosNew, stringResource(R.string.back_icon_desc))
			}
		},
		trailingIcon = {
			if (query.isNotEmpty() && active) {
				IconButton(onClick = { query = "" }) {
					Icon(Icons.Rounded.Clear, stringResource(R.string.clear_icon_desc))
				}
			}
			if (!active && searchResult.isNotEmpty()) {
				IconButton(onClick = openMenu) {
					Icon(Icons.Rounded.MoreVert, stringResource(R.string.more_options), tint = MaterialTheme.colorScheme.onSurface)
				}
			}
		}
	) {
		val (searchHistory, suggestions) = searchSuggestions(query, history, allSongs)
		val bottomPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

		Column(
			modifier = Modifier
				.padding(bottom = bottomPadding)
				.verticalScroll(rememberScrollState())
		) {
			searchHistory.forEach {
				if (it.isNotBlank()) {
					ListItem(
						headlineContent = { Text(it) },
						modifier = Modifier
							.fillMaxWidth()
							.clickable { search(it) },
						leadingContent = { Icon(Icons.Rounded.History, null) }
					)
				}
			}
			if (query.isNotEmpty()) {
				suggestions.take(15).forEach {
					ListItem(
						headlineContent = { Text(it) },
						modifier = Modifier
							.fillMaxWidth()
							.clickable { search(it) },
						leadingContent = { Icon(Icons.Rounded.Search, null) }
					)
				}
			}
			Spacer(Modifier.height(100.dp))
		}
	}
}

private fun searchSuggestions(
	query: String,
	history: List<String>,
	allSongs: List<Music>
): Pair<List<String>, List<String>> {
	val trimmedQuery = query.trim()
	val searchHistory = history.filter {
		it.contains(trimmedQuery, ignoreCase = true)
	}.take(15)

	val suggestions = allSongs.filter {
		it.name.contains(trimmedQuery, true)
				|| it.title.contains(trimmedQuery, true)
				|| it.artist.contains(trimmedQuery, true)
	}
		.map {
			if (it.artist.contains(trimmedQuery, true)) it.artist else it.title
		}
		.toSet().filterNot { it in searchHistory }
	return Pair(searchHistory, suggestions)
}