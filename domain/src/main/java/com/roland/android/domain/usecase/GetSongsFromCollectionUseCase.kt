package com.roland.android.domain.usecase

import com.roland.android.domain.model.Music
import com.roland.android.domain.repository.MusicRepository
import com.roland.android.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetSongsFromCollectionUseCase : KoinComponent {
	private val musicRepository: MusicRepository by inject()
	private val playlistRepository: PlaylistRepository by inject()

	operator fun invoke(collectionType: CollectionType): Flow<List<Music>> {
		return when (collectionType) {
			CollectionType.LastPlayed -> musicRepository.getLastPlayedSongs()
			CollectionType.Favorites -> musicRepository.getFavoriteSongs()
			is CollectionType.FromAlbum -> musicRepository.getSongsFromAlbum(collectionType.selectionArgs)
			is CollectionType.FromArtist -> musicRepository.getSongsFromArtist(collectionType.selectionArgs)
			is CollectionType.FromPlaylist -> playlistRepository.getSongsFromPlaylist(collectionType.playlistName)
		}
	}
}

sealed class CollectionType {
	data object LastPlayed : CollectionType()
	data object Favorites : CollectionType()
	data class FromAlbum(val selectionArgs: Array<String>) : CollectionType() {
		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (javaClass != other?.javaClass) return false

			other as FromAlbum

			return selectionArgs.contentEquals(other.selectionArgs)
		}

		override fun hashCode(): Int {
			return selectionArgs.contentHashCode()
		}
	}
	data class FromArtist(val selectionArgs: Array<String>) : CollectionType() {
		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (javaClass != other?.javaClass) return false

			other as FromArtist

			return selectionArgs.contentEquals(other.selectionArgs)
		}

		override fun hashCode(): Int {
			return selectionArgs.contentHashCode()
		}
	}
	data class FromPlaylist(val playlistName: String) : CollectionType()
}