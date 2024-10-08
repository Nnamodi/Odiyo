package com.roland.android.data_system.data_util

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.roland.android.data_repository.data_util.system.SystemMusicUtil
import com.roland.android.data_system.R
import com.roland.android.data_system.database.MusicUtil
import com.roland.android.data_system.player.MusicQueueUtils
import com.roland.android.data_system.util.Converters.convertToSongDetails
import com.roland.android.data_system.util.Converters.toMediaItems
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.QueueMediaItem
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SystemMusicUtilImpl(
	private val context: Context
) : SystemMusicUtil, KoinComponent {
	private val musicQueueUtils by inject<MusicQueueUtils>()
	private val musicUtil by inject<MusicUtil>()

	override fun playNext(uri: Uri) {
		musicQueueUtils.playNext(uri)
	}

	override fun playNext(songs: List<Music>) {
		musicQueueUtils.playNext(songs)
	}

	override fun populateMusicQueue(songs: List<Music>) {
		musicQueueUtils.populateMusicQueue(songs.toMediaItems())
	}

	override fun addToQueue(uri: Uri) {
		musicQueueUtils.addToQueue(uri)
	}

	override fun addToQueue(songs: List<Music>) {
		musicQueueUtils.addToQueue(songs)
	}

	override fun playFromQueue(song: QueueMediaItem) {
		musicQueueUtils.playSongFromQueue(song)
	}

	override fun duplicateSong(song: QueueMediaItem) {
		musicQueueUtils.duplicateSongInQueue(song)
	}

	override fun removeSong(song: QueueMediaItem) {
		musicQueueUtils.removeSongFromQueue(song)
	}

	override fun renameSong(song: Music) {
		val songDetails = song.convertToSongDetails()
		try { musicUtil.updateSong(songDetails) }
		catch (e: Exception) { Log.e("WriteStorageInfo", "Couldn't rename song", e) }
	}

	override fun deleteSong(song: Music) {
		val songDetails = song.convertToSongDetails()
		try { musicUtil.deleteSong(songDetails) }
		catch (e: Exception) { Log.e("WriteStorageInfo", "Couldn't delete song", e) }
	}

	override fun setAsRingtone(songs: Music, ringType: Int) {
		try {
			RingtoneManager.setActualDefaultRingtoneUri(
				context, ringType, songs.uri
			)
		} catch (e: Exception) {
			Toast.makeText(context, "Failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
			Log.e("RingtoneSettingInfo", "Couldn't set ringtone", e)
		}
	}

	override fun shareSong(songs: List<Music>) {
		val uris: ArrayList<Uri> = arrayListOf()
		songs.forEach { uris += it.uri }
		Intent(Intent.ACTION_SEND_MULTIPLE).apply {
			type = "audio/*"
			putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
		}.also { intent ->
			val title = if (songs.size > 1) context.getString(R.string.songs) else songs[0].name
			val chooserIntent = Intent.createChooser(intent, context.getString(R.string.send_audio_file, title))
			context.startActivity(chooserIntent)
		}
	}
}