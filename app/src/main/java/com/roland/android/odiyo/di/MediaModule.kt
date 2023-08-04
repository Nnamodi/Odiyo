package com.roland.android.odiyo.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

	@Provides
	@Singleton
	fun provideAudioAttributes() = AudioAttributes.Builder()
		.setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
		.setUsage(C.USAGE_MEDIA)
		.build()

	@Provides
	@Singleton
	fun provideExoPlayer(
		@ApplicationContext context: Context,
		audioAttributes: AudioAttributes
	) = ExoPlayer.Builder(context)
		.setAudioAttributes(audioAttributes, true)
//		.setHandleAudioBecomingNoisy(true)
		.build()
}