package com.hhmusic.viewmodels

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.*
import com.hhmusic.HHMusicApplication
import com.hhmusic.data.model.Song
import android.content.ContentUris



class PageViewModel : ViewModel() {

    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }

    val mObserverSongList = MediatorLiveData<List<Song>>()
    val songList : MutableLiveData<List<Song>> =  MutableLiveData<List<Song>>()

    init {

        mObserverSongList.addSource(songList, mObserverSongList::setValue)
    }


    fun setIndex(index: Int) {
        _index.value = index
    }

    fun getObserverSongList(): LiveData<List<Song>> {
        return mObserverSongList;
    }


    fun getSongList() {

        var listSong : ArrayList<Song> =  ArrayList<Song>()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

        val cursor: Cursor? = HHMusicApplication.applicationContext().contentResolver.query(
            uri,
            null,
            selection,
            null,
            sortOrder
        )

        if (cursor != null && cursor.moveToFirst()){
            val id : Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)

            val title: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)

            val duration: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)

            val albumName: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)

            val albumID: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

            val artistName: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)


            do {

                val songId: Long = cursor.getLong(id)
                val songTitle: String = cursor.getString(title)
                val songAlbum: String = cursor.getString(albumName)
                val songArtist: String = cursor.getString(artistName)
                val songDuration: Long = cursor.getLong(duration)
                val albumId: Long = cursor.getLong(albumID)

                val sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart")
                val albumArtUri = ContentUris.withAppendedId(
                    sArtworkUri,
                    albumId
                )
                listSong.add(
                    Song(
                        songId,
                        songTitle,
                        songArtist,
                        songAlbum,
                        songDuration,
                        albumArtUri
                    )
                )

            } while (cursor.moveToNext())
        }

        songList.value = listSong;


    }


}