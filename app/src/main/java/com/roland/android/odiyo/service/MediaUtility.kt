package com.roland.android.odiyo.service

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import com.roland.android.odiyo.R
import com.roland.android.odiyo.service.Util.deviceMuteState
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.nowPlaying
import com.roland.android.odiyo.service.Util.nowPlayingMetadata
import com.roland.android.odiyo.service.Util.playingState
import com.roland.android.odiyo.service.Util.shuffleModeState

@RequiresApi(Build.VERSION_CODES.Q)
@UnstableApi
class PlayerListener : Player.Listener {
	override fun onEvents(player: Player, events: Player.Events) {
		super.onEvents(player, events)
		playingState.value = player.isPlaying
		deviceMuteState.value = player.isDeviceMuted
		shuffleModeState.value = player.shuffleModeEnabled
	}

	override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
		super.onMediaMetadataChanged(mediaMetadata)
		nowPlaying.value = mediaSession?.player?.currentMediaItem
		nowPlayingMetadata.value = mediaMetadata
		Log.i("MediaMetaData", "onMediaMetadataChanged: New song[$mediaMetadata]")
		Log.i("MediaMetaData", "artworkUri: ${mediaMetadata.artworkUri}\nmediaType: ${mediaMetadata.mediaType}\n" +
				"composer: ${mediaMetadata.composer}\nstation: ${mediaMetadata.station}\n" +
				"displayTitle: ${mediaMetadata.displayTitle}\ntotalDiscCount: ${mediaMetadata.totalDiscCount}\n" +
				"folderType: ${mediaMetadata.folderType}\ndiscNumber: ${mediaMetadata.discNumber}\n" +
				"description: ${mediaMetadata.description}\nwriter: ${mediaMetadata.writer}\n" +
				"extras: ${mediaMetadata.extras}\ntitle: ${mediaMetadata.title}\n" +
				"artworkData: ${mediaMetadata.artworkData}\nalbumArtist: ${mediaMetadata.albumArtist}\n" +
				"isPlayable: ${mediaMetadata.isPlayable}\nartist: ${mediaMetadata.artist}\n" +
				"albumTitle: ${mediaMetadata.albumTitle}\nartworkDataType: ${mediaMetadata.artworkDataType}\n" +
				"compilation: ${mediaMetadata.compilation}\nconductor: ${mediaMetadata.conductor}\n" +
				"genre: ${mediaMetadata.genre}\nisBrowsable: ${mediaMetadata.isBrowsable}\n" +
				"overallRating: ${mediaMetadata.overallRating}\nsubtitle: ${mediaMetadata.subtitle}\n" +
				"totalTrackCount: ${mediaMetadata.totalTrackCount}\ntrackNumber: ${mediaMetadata.trackNumber}\n" +
				"userRating: ${mediaMetadata.userRating}\nrecordingDay: ${mediaMetadata.recordingDay}\n" +
				"recordingMonth: ${mediaMetadata.recordingMonth}\nrecordingYear: ${mediaMetadata.recordingYear}\n" +
				"releaseDay: ${mediaMetadata.releaseDay}\nreleaseMonth: ${mediaMetadata.releaseMonth}\n" +
				"releaseYear: ${mediaMetadata.releaseYear}"
		)
	}
}

@UnstableApi
class PlayerNotificationAdapter(
	private val context: Context,
	private val session: MediaSession?
) : PlayerNotificationManager.MediaDescriptionAdapter {
	override fun getCurrentContentTitle(player: Player): CharSequence {
		return player.mediaMetadata.title ?: "Unknown"
	}

	@RequiresApi(Build.VERSION_CODES.Q)
	override fun createCurrentContentIntent(player: Player): PendingIntent? {
		return session?.sessionActivity
	}

	override fun getCurrentContentText(player: Player): CharSequence {
		return player.mediaMetadata.artist ?: "Unknown"
	}

	override fun getCurrentLargeIcon(
		player: Player,
		callback: PlayerNotificationManager.BitmapCallback,
	): Bitmap? {
		val defaultArt = BitmapFactory.decodeResource(context.resources, R.drawable.default_art)
		return try {
			session?.player?.mediaMetadata?.let {
				session.bitmapLoader.loadBitmapFromMetadata(it)?.get()
			} ?: defaultArt
		} catch(e: Exception) {
			e.message?.let { Log.e("PlayerNotificationLog", it) }
			defaultArt
		}
	}
}