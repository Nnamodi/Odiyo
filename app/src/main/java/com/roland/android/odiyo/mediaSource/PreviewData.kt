package com.roland.android.odiyo.mediaSource

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.roland.android.odiyo.model.Album
import com.roland.android.odiyo.model.Artist
import com.roland.android.odiyo.model.Music
import com.roland.android.odiyo.model.Playlist

@RequiresApi(Build.VERSION_CODES.Q)
val previewData = listOf(
	Music(
		id = 0,
		uri = "0".toUri(),
		name = "",
		title = "He is the same",
		artist = "John Bellion",
		time = 198963
	),
	Music(
		id = 1,
		uri = "1".toUri(),
		name = "",
		title = "Jantamanta",
		artist = "MAVINS",
		time = 615976
	),
	Music(
		id = 2,
		uri = "2".toUri(),
		name = "",
		title = "Duke and the spear for old times' sake",
		artist = "James Hadley-chase",
		time = 542155
	),
	Music(
		id = 3,
		uri = "3".toUri(),
		name = "",
		title = "I'm real",
		artist = "Ja Rule",
		time = 947149
	),
	Music(
		id = 4,
		uri = "4".toUri(),
		name = "Lil Wayne - My president is black || roland.com.mp3",
		title = "My president is black",
		artist = "Lil Wayne",
		time = 259464,
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
		time = 345698
	),
	Music(
		id = 6,
		uri = "6".toUri(),
		name = "",
		title = "Caribbean time to time",
		artist = "High Sea Crew",
		time = 202056
	),
	Music(
		id = 7,
		uri = "7".toUri(),
		name = "",
		title = "Country road",
		artist = "Afleck Sam",
		time = 641208
	),
	Music(
		id = 8,
		uri = "8".toUri(),
		name = "",
		title = "All time low",
		artist = "John Bellion",
		time = 118946
	),
	Music(
		id = 9,
		uri = "9".toUri(),
		name = "",
		title = "Cold heart",
		artist = "Elton John",
		time = 858963
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

@RequiresApi(Build.VERSION_CODES.Q)
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