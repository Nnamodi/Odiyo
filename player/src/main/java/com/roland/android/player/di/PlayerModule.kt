package com.roland.android.player.di

import android.app.PendingIntent
import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.roland.android.data_repository.data_util.player.MusicQueueUtil
import com.roland.android.data_repository.data_util.player.SystemPlayerUtil
import com.roland.android.player.data_util.MusicQueueUtilImpl
import com.roland.android.player.data_util.SystemPlayerUtilImpl
import com.roland.android.player.notification.PlayerNotification
import com.roland.android.player.player_utils.MusicQueueUtils
import com.roland.android.player.player_utils.PlayerUtils
import org.koin.dsl.module

object PlayerModule {
	private fun provideAudioAttributes() = AudioAttributes.Builder()
		.setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
		.setUsage(C.USAGE_MEDIA)
		.build()

	private fun provideExoPlayer(
		context: Context,
		audioAttributes: AudioAttributes
	) = ExoPlayer.Builder(context)
		.setAudioAttributes(audioAttributes, true)
		.setHandleAudioBecomingNoisy(true)
		.build()

	private fun provideMediaSession(
		context: Context,
		player: ExoPlayer,
		pendingIntent: PendingIntent
	) = MediaSession.Builder(context, player)
		.setSessionActivity(pendingIntent)
		.build()

	val playerModule = module {
		single { provideAudioAttributes() }
		single { provideExoPlayer(get<Context>().applicationContext, get()) }
		single { provideMediaSession(get<Context>().applicationContext, get(), get()) }
		single { PlayerNotification(get<Context>().applicationContext) }
		single { MusicQueueUtils() }
		single { PlayerUtils(get<Context>().applicationContext) }
		factory<MusicQueueUtil> { MusicQueueUtilImpl() }
		factory<SystemPlayerUtil> { SystemPlayerUtilImpl() }
	}
}