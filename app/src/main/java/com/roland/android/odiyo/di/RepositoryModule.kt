package com.roland.android.odiyo.di

import android.content.Context
import com.roland.android.odiyo.database.MusicDao
import com.roland.android.odiyo.database.PlaylistDao
import com.roland.android.odiyo.mediaSource.AlbumsSource
import com.roland.android.odiyo.mediaSource.ArtistsSource
import com.roland.android.odiyo.mediaSource.MediaAccessingObject
import com.roland.android.odiyo.mediaSource.MediaSource
import com.roland.android.odiyo.repository.MediaRepository
import com.roland.android.odiyo.repository.MusicRepository
import com.roland.android.odiyo.repository.PlaylistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

	@Provides
	@Singleton
	fun provideMediaSource(@ApplicationContext context: Context) =
		MediaSource(context.contentResolver)

	@Provides
	@Singleton
	fun provideAlbumSource(@ApplicationContext context: Context) =
		AlbumsSource(context.contentResolver)

	@Provides
	@Singleton
	fun provideArtistsSource(@ApplicationContext context: Context) =
		ArtistsSource(context.contentResolver)

	@Provides
	@Singleton
	fun provideMediaAccessingObject(@ApplicationContext context: Context) =
		MediaAccessingObject(context.contentResolver)

	@Provides
	@Singleton
	fun provideMediaRepository(
		mediaSource: MediaSource,
		albumsSource: AlbumsSource,
		artistsSource: ArtistsSource,
		mediaAccessingObject: MediaAccessingObject
	) = MediaRepository(mediaSource, albumsSource, artistsSource, mediaAccessingObject)

	@Provides
	@Singleton
	fun provideMusicRepository(musicDao: MusicDao) = MusicRepository(musicDao)

	@Provides
	@Singleton
	fun providePlaylistRepository(playlistDao: PlaylistDao) = PlaylistRepository(playlistDao)
}