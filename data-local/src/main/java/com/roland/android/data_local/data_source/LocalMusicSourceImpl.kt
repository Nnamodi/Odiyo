package com.roland.android.data_local.data_source

import android.net.Uri
import androidx.core.net.toUri
import com.roland.android.data_local.database.MusicDao
import com.roland.android.data_local.database.SearchDao
import com.roland.android.data_local.datastore.MusicQueueStore
import com.roland.android.data_local.entity.SearchQueryEntity
import com.roland.android.data_local.util.Converters.convertToMusic
import com.roland.android.data_local.util.Converters.toDate
import com.roland.android.data_repository.data_source.local.LocalMusicSource
import com.roland.android.domain.model.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Date

class LocalMusicSourceImpl : LocalMusicSource, KoinComponent {
	private val musicDao by inject<MusicDao>()
	private val searchDao by inject<SearchDao>()
	private val musicQueueStore by inject<MusicQueueStore>()
	private val coroutineScope by inject<CoroutineScope>()

	override fun getAllSongs(): Flow<List<Music>> {
		return musicDao.getAllSongs()
			.map { musicEntityList ->
				musicEntityList
					.filter { it.name.endsWith(".mp3") }
					.map { it.convertToMusic() }
			}
	}

	override fun getLastPlayedSongs(): Flow<List<Music>> {
		return musicDao.getAllSongs()
			.map { musicEntityList ->
				musicEntityList
					.filter { it.lastPlayed != Date(0) }
					.sortedByDescending { it.lastPlayed }
					.take(100)
					.map { it.convertToMusic() }
			}
	}

	override fun getFavoriteSongs(): Flow<List<Music>> {
		return musicDao.getAllSongs()
			.map { musicEntityList ->
				musicEntityList
					.filter { it.favorite }
					.map { it.convertToMusic() }
			}
	}

	override fun getRecentlyAddedSongs(): Flow<List<Music>> {
		return musicDao.getAllSongs()
			.map { musicEntityList ->
				musicEntityList
					.sortedByDescending { it.addedOn.toDate() }
					.take(45)
					.map { it.convertToMusic() }
			}
	}

	override fun getSongsOnQueue(): Flow<List<Music>> {
		var urisOnQueue = emptyList<Uri>()
		coroutineScope.launch {
			urisOnQueue = musicQueueStore.getCurrentPlaylist().last()
				.playlist.map { it.toUri() }
		}
		return musicDao.getAllSongs()
			.filter { allSongs ->
				allSongs.map { it.uri }.containsAll(urisOnQueue)
			}
			.map { songsOnQueue ->
				songsOnQueue.map { it.convertToMusic() }
			}
	}

	override fun getSongsFromSearch(query: String): Flow<List<Music>> {
		coroutineScope.launch {
			val queryEntity = SearchQueryEntity(query = query)
			searchDao.addSearchQuery(queryEntity)
		}
		return searchDao.getSongsFromSearch(query)
			.map { musicEntityList ->
				musicEntityList.map { it.convertToMusic() }
			}
	}

	override fun getSearchHistory(): Flow<List<String>> {
		return searchDao.getSearchHistory()
			.map { searchQueryEntity ->
				searchQueryEntity.map { it.query }
			}
	}
}