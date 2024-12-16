package com.roland.android.odiyo.data

import androidx.core.net.toUri
import com.roland.android.domain.model.Album
import com.roland.android.domain.model.Artist
import com.roland.android.domain.model.Music
import com.roland.android.domain.model.Playlist

val previewData = listOf(
	Music(
		id = 0,
		uri = "0".toUri(),
		name = "",
		title = "He is the same",
		artist = "John Bellion",
		album = "",
		path = "",
		addedOn = "",
		duration = 198963,
		size = ""
	),
	Music(
		id = 1,
		uri = "1".toUri(),
		name = "",
		title = "Jantamanta",
		artist = "MAVINS",
		album = "",
		path = "",
		addedOn = "",
		duration = 615976,
		size = ""
	),
	Music(
		id = 2,
		uri = "2".toUri(),
		name = "",
		title = "Duke and the spear for old durations' sake",
		artist = "James Hadley-chase",
		album = "",
		path = "",
		addedOn = "",
		duration = 542155,
		size = ""
	),
	Music(
		id = 3,
		uri = "3".toUri(),
		name = "",
		title = "I'm real",
		artist = "Ja Rule",
		album = "",
		path = "",
		addedOn = "",
		duration = 947149,
		size = ""
	),
	Music(
		id = 4,
		uri = "4".toUri(),
		name = "Lil Wayne - My president is black || roland.com.mp3",
		title = "My president is black",
		artist = "Lil Wayne",
		album = "Indiana",
		path = "/storage/emulated/0/Xender/audio/Lil Wayne - My president is black || roland.com.mp3",
		addedOn = "24 July, 2006",
		duration = 259464,
		size = "6.8 MB",
	),
	Music(
		id = 5,
		uri = "5".toUri(),
		name = "",
		title = "Yellow",
		artist = "Coldplay",
		album = "",
		path = "",
		addedOn = "",
		duration = 345698,
		size = ""
	),
	Music(
		id = 6,
		uri = "6".toUri(),
		name = "",
		title = "Caribbean duration to duration",
		artist = "High Sea Crew",
		album = "",
		path = "",
		addedOn = "",
		duration = 202056,
		size = ""
	),
	Music(
		id = 7,
		uri = "7".toUri(),
		name = "",
		title = "Country road",
		artist = "Afleck Sam",
		album = "",
		path = "",
		addedOn = "",
		duration = 641208,
		size = ""
	),
	Music(
		id = 8,
		uri = "8".toUri(),
		name = "",
		title = "All duration low",
		artist = "John Bellion",
		album = "",
		path = "",
		addedOn = "",
		duration = 118946,
		size = ""
	),
	Music(
		id = 9,
		uri = "9".toUri(),
		name = "",
		title = "Cold heart",
		artist = "Elton John",
		album = "",
		path = "",
		addedOn = "",
		duration = 858963,
		size = ""
	)
)

val previewAlbum = listOf(
	Album(
		uri = "0".toUri(),
		numberOfSongs = 4,
		album = "Does it have to be me?"
	),
	Album(
		uri = "1".toUri(),
		numberOfSongs = 3,
		album = "Fire for fun"
	),
	Album(
		uri = "2".toUri(),
		numberOfSongs = 1,
		album = "Why should I care?"
	),
	Album(
		uri = "3".toUri(),
		numberOfSongs = 15,
		album = "None of our business"
	),
	Album(
		uri = "4".toUri(),
		numberOfSongs = 9,
		album = "@Sare_hen.com"
	),
	Album(
		uri = "5".toUri(),
		numberOfSongs = 2,
		album = "The only one"
	),
	Album(
		uri = "6".toUri(),
		numberOfSongs = 15,
		album = "Who said it"
	),
	Album(
		uri = "7".toUri(),
		numberOfSongs = 8,
		album = "Undying remedies"
	),
	Album(
		uri = "8".toUri(),
		numberOfSongs = 3,
		album = "Not yet born"
	),
	Album(
		uri = "9".toUri(),
		numberOfSongs = 1,
		album = "Hero from the sun"
	)
)

val previewArtist = listOf(
	Artist(
		uri = "0".toUri(),
		numberOfTracks = 8,
		artist = "Imagine Dragons"
	),
	Artist(
		uri = "1".toUri(),
		numberOfTracks = 5,
		artist = "Elton John"
	),
	Artist(
		uri = "2".toUri(),
		numberOfTracks = 1,
		artist = "Lonial Jr."
	),
	Artist(
		uri = "3".toUri(),
		numberOfTracks = 7,
		artist = "One Republic"
	),
	Artist(
		uri = "4".toUri(),
		numberOfTracks = 10,
		artist = "Coldplay"
	),
	Artist(
		uri = "5".toUri(),
		numberOfTracks = 12,
		artist = "Halun-vid"
	),
	Artist(
		uri = "6".toUri(),
		numberOfTracks = 3,
		artist = "Samune"
	),
	Artist(
		uri = "7".toUri(),
		numberOfTracks = 9,
		artist = "Denveri"
	),
	Artist(
		uri = "8".toUri(),
		numberOfTracks = 17,
		artist = "Faluo"
	),
	Artist(
		uri = "9".toUri(),
		numberOfTracks = 2,
		artist = "David Mcklin"
	),
)

val previewPlaylist = listOf(
	Playlist(
		id = 0,
		name = "Hit jams",
		songs = previewData.plus(previewData).map { it.uri }.take(15)
	),
	Playlist(
		id = 1,
		name = "Original songs",
		songs = previewData.plus(previewData).map { it.uri }.take(20)
	),
	Playlist(
		id = 2,
		name = "Music for the soul",
		songs = previewData.map { it.uri }.take(5)
	),
	Playlist(
		id = 3,
		name = "Cool music",
		songs = previewData.map { it.uri }.take(2)
	),
	Playlist(
		id = 4,
		name = "Gbedu",
		songs = previewData.map { it.uri }
	)
)