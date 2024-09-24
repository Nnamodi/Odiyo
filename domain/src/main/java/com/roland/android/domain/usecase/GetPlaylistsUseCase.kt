package com.roland.android.domain.usecase

import com.roland.android.domain.model.Playlist
import com.roland.android.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetPlaylistsUseCase : KoinComponent {
	private val playlistRepository: PlaylistRepository by inject()

	operator fun invoke(): Flow<List<Playlist>> =
		playlistRepository.getAllPlaylists()
}