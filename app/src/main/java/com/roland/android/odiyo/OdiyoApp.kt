package com.roland.android.odiyo

import android.app.Application
import androidx.media3.session.MediaSession
import com.roland.android.data_local.di.PersistenceModule.persistenceModule
import com.roland.android.data_repository.di.RepositoryModule.repositoryModule
import com.roland.android.data_system.di.SystemDatabaseModule.systemDatabaseModule
import com.roland.android.domain.di.DomainModule.domainModule
import com.roland.android.odiyo.di.AppModule.appModule
import com.roland.android.player.di.PlayerModule.playerModule
import com.roland.android.player.notification.PlayerNotification
import com.roland.android.player.player_utils.PlayerListener
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class OdiyoApp : Application() {
	private val mediaSession by inject<MediaSession>()
	private val notificationManager by inject<PlayerNotification>()

	override fun onCreate() {
		super.onCreate()
		startKoin {
			androidContext(this@OdiyoApp)
			androidLogger(Level.INFO)
			modules(
				appModule,
				domainModule,
				repositoryModule,
				persistenceModule,
				systemDatabaseModule,
				playerModule
			)
		}
		// mediaSession and notificationManager will be initialized and managed in the Service class for background media playback
		mediaSession.player.addListener(PlayerListener(this))
		notificationManager.showNotification(mediaSession.player)
	}

	override fun onTerminate() {
		super.onTerminate()
		notificationManager.hideNotification()
		mediaSession.apply {
			player.apply {
				removeListener(PlayerListener(this@OdiyoApp))
				release()
			}
			release()
		}
	}
}