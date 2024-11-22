package com.roland.android.odiyo.di

import android.app.PendingIntent
import android.content.Context
import com.roland.android.odiyo.ui.MainActivity
import com.roland.android.odiyo.ui.screens.home.HomeViewModel
import com.roland.android.odiyo.ui.screens.list.ListViewModel
import com.roland.android.odiyo.ui.screens.media.MediaViewModel
import com.roland.android.odiyo.ui.screens.nowPlayingScreens.NowPlayingViewModel
import com.roland.android.odiyo.ui.screens.playlists.PlaylistViewModel
import com.roland.android.odiyo.ui.screens.search.SearchViewModel
import com.roland.android.odiyo.ui.screens.settings.SettingsViewModel
import com.roland.android.odiyo.util.Haptic
import com.roland.android.odiyo.util.actions.MediaMenuActionsImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModule {

	// an intent to launch app from player notification.
	private fun providePendingIntent(context: Context) = PendingIntent.getActivity(
		/* context = */ context,
		/* requestCode = */ 0,
		/* intent = */ MainActivity.newInstance(context),
		/* flags = */ PendingIntent.FLAG_IMMUTABLE
	)

	val appModule = module {
		single { providePendingIntent(get<Context>().applicationContext) }
		single { Haptic(get<Context>().applicationContext) }
		single { MediaMenuActionsImpl() }
		viewModel { HomeViewModel() }
		viewModel { ListViewModel() }
		viewModel { MediaViewModel() }
		viewModel { NowPlayingViewModel() }
		viewModel { PlaylistViewModel() }
		viewModel { SearchViewModel() }
		viewModel { SettingsViewModel() }
	}

}