package com.roland.android.data_system.di

import android.content.Context
import com.roland.android.data_repository.data_source.system.SystemMusicSource
import com.roland.android.data_repository.data_util.system.SystemMusicUtil
import com.roland.android.data_system.data_source.SystemMusicSourceImpl
import com.roland.android.data_system.data_util.SystemMusicUtilImpl
import com.roland.android.data_system.database.AlbumsSource
import com.roland.android.data_system.database.ArtistsSource
import com.roland.android.data_system.database.MusicSource
import com.roland.android.data_system.database.MusicUtil
import org.koin.dsl.module

object SystemDatabaseModule {
	private fun provideContentResolver(context: Context) = context.contentResolver

	val systemDatabaseModule = module {
		single { provideContentResolver(get<Context>().applicationContext) }
		single { MusicSource() }
		single { AlbumsSource() }
		single { ArtistsSource() }
		single { MusicUtil() }
		factory<SystemMusicSource> { SystemMusicSourceImpl() }
		factory<SystemMusicUtil> { SystemMusicUtilImpl(get<Context>().applicationContext) }
	}
}