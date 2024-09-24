package com.roland.android.domain.usecase

import com.roland.android.domain.model.Music
import com.roland.android.domain.repository.MusicQueueRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetSongsOnQueueUseCase : KoinComponent {
	private val musicQueueRepository: MusicQueueRepository by inject()

	operator fun invoke(): Flow<List<Music>> =
		musicQueueRepository.getSongsOnQueue()
}