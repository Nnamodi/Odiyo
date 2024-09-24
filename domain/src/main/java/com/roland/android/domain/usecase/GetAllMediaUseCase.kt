package com.roland.android.domain.usecase

import com.roland.android.domain.model.AllMedia
import com.roland.android.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetAllMediaUseCase : KoinComponent {
	private val musicRepository: MusicRepository by inject()

	operator fun invoke(): Flow<AllMedia> = combine(
		musicRepository.getAllSongs(),
		musicRepository.getAlbums(),
		musicRepository.getArtists()
	) { allSongs, albums, artists ->
		AllMedia(
			allSongs = allSongs,
			albums = albums,
			artists = artists
		)
	}
}