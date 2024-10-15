package com.roland.android.data_repository.di

import android.content.Context
import com.roland.android.data_repository.repository.MusicQueueRepositoryImpl
import com.roland.android.data_repository.repository.MusicRepositoryImpl
import com.roland.android.data_repository.repository.MusicUtilRepositoryImpl
import com.roland.android.data_repository.repository.PlayerRepositoryImpl
import com.roland.android.data_repository.repository.PlaylistRepositoryImpl
import com.roland.android.data_repository.repository.PlaylistUtilRepositoryImpl
import com.roland.android.data_repository.repository.SearchRepositoryImpl
import com.roland.android.data_repository.repository.SettingsRepositoryImpl
import com.roland.android.domain.repository.MusicQueueRepository
import com.roland.android.domain.repository.MusicRepository
import com.roland.android.domain.repository.MusicUtilRepository
import com.roland.android.domain.repository.PlayerRepository
import com.roland.android.domain.repository.PlaylistRepository
import com.roland.android.domain.repository.PlaylistUtilRepository
import com.roland.android.domain.repository.SearchRepository
import com.roland.android.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

object RepositoryModule {
	private fun providesCoroutineScope() = CoroutineScope(Dispatchers.IO)

	val repositoryModule = module {
		single { providesCoroutineScope() }
		factory<MusicQueueRepository> { MusicQueueRepositoryImpl() }
		factory<MusicRepository> { MusicRepositoryImpl(get<Context>().applicationContext) }
		factory<MusicUtilRepository> { MusicUtilRepositoryImpl() }
		factory<PlayerRepository> { PlayerRepositoryImpl() }
		factory<PlaylistRepository> { PlaylistRepositoryImpl(get<Context>().applicationContext) }
		factory<PlaylistUtilRepository> { PlaylistUtilRepositoryImpl() }
		factory<SearchRepository> { SearchRepositoryImpl(get<Context>().applicationContext) }
		factory<SettingsRepository> { SettingsRepositoryImpl() }
	}
}