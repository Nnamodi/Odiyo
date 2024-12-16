package com.roland.android.data_local.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.roland.android.data_local.data_source.LocalMusicSourceImpl
import com.roland.android.data_local.data_source.LocalPlaylistSourceImpl
import com.roland.android.data_local.data_util.LocalMusicUtilImpl
import com.roland.android.data_local.data_util.LocalPlayerUtilImpl
import com.roland.android.data_local.data_util.LocalPlaylistUtilImpl
import com.roland.android.data_local.data_util.LocalSettingsUtilImpl
import com.roland.android.data_local.database.AppDatabase
import com.roland.android.data_local.database.MusicDao
import com.roland.android.data_local.database.PlaylistDao
import com.roland.android.data_local.database.SearchDao
import com.roland.android.data_local.datastore.MusicQueueStore
import com.roland.android.data_local.datastore.MusicUtilStore
import com.roland.android.data_local.datastore.PlayerUtilStore
import com.roland.android.data_local.datastore.SettingsStore
import com.roland.android.data_repository.data_source.local.LocalMusicSource
import com.roland.android.data_repository.data_source.local.LocalPlaylistSource
import com.roland.android.data_repository.data_util.local.LocalMusicUtil
import com.roland.android.data_repository.data_util.local.LocalPlayerUtil
import com.roland.android.data_repository.data_util.local.LocalPlaylistUtil
import com.roland.android.data_repository.data_util.local.LocalSettingsUtil
import org.koin.dsl.module

private val Context.datastore: DataStore<Preferences> by preferencesDataStore("app_preferences")

object PersistenceModule {

	private fun provideDataStore(context: Context): DataStore<Preferences> = context.datastore

	private fun provideDatabase(context: Context): AppDatabase =
		Room.databaseBuilder(
			context,
			AppDatabase::class.java,
			"music_database"
		).build()

	private fun provideMusicDao(appDatabase: AppDatabase): MusicDao = appDatabase.musicDao()

	private fun providePlaylistDao(appDatabase: AppDatabase): PlaylistDao = appDatabase.playlistDao()

	private fun provideSearchDao(appDatabase: AppDatabase): SearchDao = appDatabase.searchDao()

	val persistenceModule = module {
		single { provideDataStore(get<Context>().applicationContext) }
		single { provideDatabase(get<Context>().applicationContext) }
		single { provideMusicDao(get()) }
		single { providePlaylistDao(get()) }
		single { provideSearchDao(get()) }
		single { MusicQueueStore() }
		single { MusicUtilStore() }
		single { PlayerUtilStore() }
		single { SettingsStore() }
		factory<LocalMusicSource> { LocalMusicSourceImpl() }
		factory<LocalMusicUtil> { LocalMusicUtilImpl() }
		factory<LocalPlayerUtil> { LocalPlayerUtilImpl() }
		factory<LocalPlaylistSource> { LocalPlaylistSourceImpl() }
		factory<LocalPlaylistUtil> { LocalPlaylistUtilImpl() }
		factory<LocalSettingsUtil> { LocalSettingsUtilImpl(get<Context>().applicationContext) }
	}
}