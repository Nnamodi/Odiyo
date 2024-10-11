package com.roland.android.data_repository.data_util.system

import com.roland.android.domain.model.Music

interface SystemMusicUtil {

	fun renameSong(song: Music)

	fun deleteSong(song: Music)

	fun setAsRingtone(songs: Music, ringType: Int)

	fun shareSong(songs: List<Music>)

}