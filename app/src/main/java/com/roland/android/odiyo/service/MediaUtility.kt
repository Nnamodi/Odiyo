package com.roland.android.odiyo.service

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import com.roland.android.odiyo.service.Util.mediaSession
import com.roland.android.odiyo.service.Util.nowPlaying
import com.roland.android.odiyo.service.Util.playingState

@RequiresApi(Build.VERSION_CODES.Q)
class PlayerListener : Player.Listener {
	override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
		super.onMediaMetadataChanged(mediaMetadata)
		nowPlaying.value = mediaSession?.player?.currentMediaItem
		Log.i("MediaMetaData", "onMediaMetadataChanged: New song[$mediaMetadata]")
	}

	override fun onIsPlayingChanged(isPlaying: Boolean) {
		super.onIsPlayingChanged(isPlaying)
		playingState.value = isPlaying
	}
}