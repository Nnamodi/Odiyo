package com.roland.android.odiyo.ui.components

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.NowPlayingFrom
import com.roland.android.odiyo.R
import com.roland.android.odiyo.data.State
import com.roland.android.odiyo.ui.navigation.ALBUMS
import com.roland.android.odiyo.ui.navigation.ALL_SONGS
import com.roland.android.odiyo.ui.navigation.ARTISTS
import com.roland.android.odiyo.ui.navigation.FAVORITES
import com.roland.android.odiyo.ui.navigation.LAST_PLAYED
import com.roland.android.odiyo.ui.navigation.RECENTLY_ADDED
import com.roland.android.odiyo.ui.navigation.SEARCH
import com.roland.android.odiyo.ui.navigation.Screens
import com.roland.android.odiyo.ui.screens.search.SearchUiState
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
	navigate: (Screens) -> Unit,
	title: String? = null
) {
	TopAppBar(
		title = { title?.let { Text(it) } },
		navigationIcon = {
			IconButton(onClick = { navigate(Screens.Back) }) {
				Icon(Icons.Rounded.ArrowBackIosNew, stringResource(R.string.back_icon_desc))
			}
		},
		actions = {
			if (title == null) {
				IconButton(onClick = { navigate(Screens.SearchScreen) }) {
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
	nowPlayingFrom: NowPlayingFrom,
	backgroundColor: Color,
	componentColor: Color,
	navigate: (Screens) -> Unit,
	openMoreOptions: () -> Unit
) {
	val interactionSource = remember { MutableInteractionSource() }
	val ripple = ripple(color = CustomColors.rippleColor(backgroundColor))

	CenterAlignedTopAppBar(
		navigationIcon = {
			NowPlayingIconButton(
				onClick = { navigate(Screens.Back) },
				color = backgroundColor
			) {
				Icon(
					imageVector = Icons.Rounded.ArrowBackIosNew,
					contentDescription = stringResource(R.string.back_icon_desc),
					modifier = Modifier.rotate(-90f)
				)
			}
		},
		title = {
			val collectionDetails = getCollectionDetails(
				context = LocalContext.current,
				collectionType = nowPlayingFrom.collectionType,
				collectionName = nowPlayingFrom.collectionName
			)

			if (song != null) {
				Column(
					modifier = Modifier
						.padding(4.dp)
						.clip(MaterialTheme.shapes.small)
						.clickable(
							interactionSource = interactionSource,
							indication = ripple,
							enabled = song.uri != "".toUri() && nowPlayingFrom.collectionType.isNotEmpty()
						) {
							navigate(Screens.ListScreen(
								nowPlayingFrom.collectionName,
								nowPlayingFrom.collectionType
							))
						}
						.fillMaxWidth(0.75f)
						.padding(4.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = collectionDetails.first,
						color = componentColor, modifier = Modifier.alpha(0.8f),
						style = MaterialTheme.typography.titleSmall
					)
					Text(
						text = collectionDetails.second, color = componentColor,
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
	collectionType: String = "",
	collectionIsPlaylist: Boolean = false,
	songsNotEmpty: Boolean,
	navigate: (Screens) -> Unit,
	openMenu: () -> Unit
) {
	TopAppBar(
		title = {
			val collectionDetails = getCollectionDetails(
				context = LocalContext.current,
				collectionType = collectionType,
				collectionName = collectionName
			)
			Text(
				text = collectionDetails.second,
				overflow = TextOverflow.Ellipsis,
				softWrap = false
			)
		},
		navigationIcon = {
			IconButton(onClick = { navigate(Screens.Back) }) {
				Icon(Icons.Rounded.ArrowBackIosNew, stringResource(R.string.back_icon_desc))
			}
		},
		actions = {
			if (songsNotEmpty) {
				if (collectionIsPlaylist) {
					IconButton(onClick = { navigate(Screens.AddSongsScreen(collectionName)) }) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
	uiState: SearchUiState,
	onSearch: (String) -> Unit,
	navigate: (Screens) -> Unit,
	openMenu: () -> Unit
) {
	val (searchQuery, searchResult, history, allSongs) = uiState
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
				onClick = {
					if (active) {
						active = false
						if (query.isEmpty()) query = searchQuery
					} else navigate(Screens.Back)
				}
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
			if (!active && searchResult is State.Success && searchResult.data.isNotEmpty()) {
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

private fun getCollectionDetails(
	context: Context,
	collectionType: String,
	collectionName: String
): Pair<String, String> {
	val type = when (collectionType) {
		"" -> collectionType
		ALBUMS -> context.getString(R.string.playing_from_album)
		ARTISTS -> context.getString(R.string.playing_from_artist)
		SEARCH -> context.getString(R.string.playing_from_search)
		else -> context.getString(R.string.playing_from_playlist)
	}
	val name = when {
		collectionType == SEARCH -> context.getString(R.string.from_search, collectionName)
		collectionName == FAVORITES -> context.getString(R.string.favorites)
		collectionName == LAST_PLAYED -> context.getString(R.string.last_played)
		collectionName == RECENTLY_ADDED -> context.getString(R.string.recently_added)
		collectionName == ALL_SONGS -> context.getString(R.string.all_songs)
		else -> collectionName
	}
	return Pair(type, name)
}