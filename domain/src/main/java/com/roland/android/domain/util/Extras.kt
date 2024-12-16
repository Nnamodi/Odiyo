package com.roland.android.domain.util

import android.media.RingtoneManager
import androidx.annotation.DrawableRes
import com.roland.android.domain.R

enum class IntentOptions(
	@DrawableRes val iconRes: Int,
	val menuText: Int
) {
	Play(R.drawable.play_arrow_icon, R.string.play),
	PlayNext(R.drawable.queue_music_icon, R.string.play_next),
	AddToQueue(R.drawable.add_to_queue_icon, R.string.add_to_queue),
	AlwaysAsk(R.drawable.help_icon, R.string.always_ask)
}

enum class LanguageOptions(val title: Int, val local: String) {
	FollowSystem(R.string.follow_system, ""),
	English(R.string.english, "en"),
	French(R.string.french, "fr")
}

enum class RingtoneOptions(val title: Int, val ringType: Int) {
	Ringtone(R.string.ringtone, RingtoneManager.TYPE_RINGTONE),
	Alarm(R.string.alarm_sound, RingtoneManager.TYPE_ALARM),
	Notification(R.string.notification_tone, RingtoneManager.TYPE_NOTIFICATION),
}

enum class SortOptions(val title: Int) {
	NameAZ(R.string.name_a_z),
	NameZA(R.string.name_z_a),
	NewestFirst(R.string.newest_first),
	OldestFirst(R.string.oldest_first)
}

enum class Themes(val title: Int) {
	System(R.string.follow_system),
	Dark(R.string.dark_theme),
	Light(R.string.light_theme)
}
