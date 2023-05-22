package com.roland.android.odiyo.mediaSource

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.roland.android.odiyo.model.Album
import com.roland.android.odiyo.model.Artist
import com.roland.android.odiyo.model.Music

@RequiresApi(Build.VERSION_CODES.Q)
val previewData = listOf(
	Music(
		id = 0,
		uri = "0".toUri(),
		name = "",
		title = "He is the same",
		artist = "John Bellion",
		time = 198963,
		thumbnail = null
	),
	Music(
		id = 1,
		uri = "1".toUri(),
		name = "",
		title = "Jantamanta",
		artist = "MAVINS",
		time = 615976,
		thumbnail = null
	),
	Music(
		id = 2,
		uri = "2".toUri(),
		name = "",
		title = "Duke and the spear for old times' sake",
		artist = "James Hadley-chase",
		time = 542155,
		thumbnail = null
	),
	Music(
		id = 3,
		uri = "3".toUri(),
		name = "",
		title = "I'm real",
		artist = "Ja Rule",
		time = 947149,
		thumbnail = null
	),
	Music(
		id = 4,
		uri = "4".toUri(),
		name = "Lil Wayne - My president is black || roland.com.mp3",
		title = "My president is black",
		artist = "Lil Wayne",
		time = 259464,
		thumbnail = null,
		bytes = 6849581,
		addedOn = 1597243262,
		album = "Indiana",
		path = "/storage/emulated/0/Xender/audio/Lil Wayne - My president is black || roland.com.mp3"
	),
	Music(
		id = 5,
		uri = "5".toUri(),
		name = "",
		title = "Yellow",
		artist = "Coldplay",
		time = 345698,
		thumbnail = null
	),
	Music(
		id = 6,
		uri = "6".toUri(),
		name = "",
		title = "Caribbean time to time",
		artist = "High Sea Crew",
		time = 202056,
		thumbnail = null
	),
	Music(
		id = 7,
		uri = "7".toUri(),
		name = "",
		title = "Country road",
		artist = "Afleck Sam",
		time = 641208,
		thumbnail = null
	),
	Music(
		id = 8,
		uri = "8".toUri(),
		name = "",
		title = "All time low",
		artist = "John Bellion",
		time = 118946,
		thumbnail = null
	),
	Music(
		id = 9,
		uri = "9".toUri(),
		name = "",
		title = "Cold heart",
		artist = "Elton John",
		time = 858963,
		thumbnail = null
	)
)

val previewAlbum = listOf(
	Album(
		uri = "0".toUri(),
		numSongs = "4",
		album = "Does it have to be me?"
	),
	Album(
		uri = "1".toUri(),
		numSongs = "3",
		album = "Fire for fun"
	),
	Album(
		uri = "2".toUri(),
		numSongs = "1",
		album = "Why should I care?"
	),
	Album(
		uri = "3".toUri(),
		numSongs = "15",
		album = "None of our business"
	),
	Album(
		uri = "4".toUri(),
		numSongs = "9",
		album = "@Sare_hen.com"
	),
	Album(
		uri = "5".toUri(),
		numSongs = "2",
		album = "The only one"
	),
	Album(
		uri = "6".toUri(),
		numSongs = "15",
		album = "Who said it"
	),
	Album(
		uri = "7".toUri(),
		numSongs = "8",
		album = "Undying remedies"
	),
	Album(
		uri = "8".toUri(),
		numSongs = "3",
		album = "Not yet born"
	),
	Album(
		uri = "9".toUri(),
		numSongs = "1",
		album = "Hero from the sun"
	)
)

val previewArtist = listOf(
	Artist(
		uri = "0".toUri(),
		numTracks = "8",
		artist = "Imagine Dragons"
	),
	Artist(
		uri = "1".toUri(),
		numTracks = "5",
		artist = "Elton John"
	),
	Artist(
		uri = "2".toUri(),
		numTracks = "1",
		artist = "Lonial Jr."
	),
	Artist(
		uri = "3".toUri(),
		numTracks = "7",
		artist = "One Republic"
	),
	Artist(
		uri = "4".toUri(),
		numTracks = "10",
		artist = "Coldplay"
	),
	Artist(
		uri = "5".toUri(),
		numTracks = "12",
		artist = "Halun-vid"
	),
	Artist(
		uri = "6".toUri(),
		numTracks = "3",
		artist = "Samune"
	),
	Artist(
		uri = "7".toUri(),
		numTracks = "9",
		artist = "Denveri"
	),
	Artist(
		uri = "8".toUri(),
		numTracks = "17",
		artist = "Faluo"
	),
	Artist(
		uri = "9".toUri(),
		numTracks = "2",
		artist = "David Mcklin"
	),
)