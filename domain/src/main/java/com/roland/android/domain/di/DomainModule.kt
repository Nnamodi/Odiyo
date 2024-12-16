package com.roland.android.domain.di

import com.roland.android.domain.usecase.GetAllMediaUseCase
import com.roland.android.domain.usecase.GetCurrentSongUseCase
import com.roland.android.domain.usecase.GetPlaylistsUseCase
import com.roland.android.domain.usecase.GetPreferenceUseCase
import com.roland.android.domain.usecase.GetRecentlyAddedSongsUseCase
import com.roland.android.domain.usecase.GetSearchDataUseCase
import com.roland.android.domain.usecase.GetSongsFromCollectionUseCase
import com.roland.android.domain.usecase.GetSongsOnQueueUseCase
import org.koin.dsl.module

object DomainModule {
	val domainModule = module {
		single { GetAllMediaUseCase() }
		single { GetCurrentSongUseCase() }
		single { GetPlaylistsUseCase() }
		single { GetPreferenceUseCase() }
		single { GetRecentlyAddedSongsUseCase() }
		single { GetSearchDataUseCase() }
		single { GetSongsFromCollectionUseCase() }
		single { GetSongsOnQueueUseCase() }
	}
}