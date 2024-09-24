package com.roland.android.domain.usecase

import com.roland.android.domain.model.Music
import com.roland.android.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetCurrentSongUseCase : KoinComponent {
	private val musicRepository: MusicRepository by inject()

	operator fun invoke(): Flow<Music> =
		musicRepository.getCurrentSong()
}