package com.roland.android.odiyo.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.roland.android.odiyo.data.AppDataStore
import com.roland.android.odiyo.database.AppDatabase
import com.roland.android.odiyo.database.MusicDao
import com.roland.android.odiyo.database.PlaylistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.datastore: DataStore<Preferences> by preferencesDataStore("app_preferences")

@Module
@InstallIn(SingletonComponent::class)
@RequiresApi(Build.VERSION_CODES.Q)
object PersistenceModule {

	@Provides
	@Singleton
	fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
		Room.databaseBuilder(
			context,
			AppDatabase::class.java,
			"music_database"
		).build()

	@Provides
	@Singleton
	fun provideMusicDao(appDatabase: AppDatabase): MusicDao = appDatabase.musicDao()

	@Provides
	@Singleton
	fun providePlaylistDao(appDatabase: AppDatabase): PlaylistDao = appDatabase.playlistDao()

	@Provides
	@Singleton
	fun provideAppDataStore(@ApplicationContext context: Context) = AppDataStore(context.datastore)
}