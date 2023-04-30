package com.roland.android.odiyo.service

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
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
	}

	override fun onIsPlayingChanged(isPlaying: Boolean) {
		super.onIsPlayingChanged(isPlaying)
		playingState.value = isPlaying
	}
}