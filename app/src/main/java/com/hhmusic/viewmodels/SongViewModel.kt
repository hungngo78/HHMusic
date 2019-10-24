package com.hhmusic.viewmodels

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.*
import com.hhmusic.HHMusicApplication
//import com.hhmusic.data.model.Song
import com.hhmusic.data.entities.Song
import android.content.ContentUris
import com.hhmusic.data.repository.MusicRepository


class SongViewModel internal constructor(
    private val songRepository: MusicRepository
) : ViewModel() {


    val mObserverSongList = MediatorLiveData<List<Song>>()
    //val songList : MutableLiveData<List<Song>> =  MutableLiveData<List<Song>>()

    init {
        var songList = songRepository.getSongList()
        mObserverSongList.addSource(songList, mObserverSongList::setValue)
    }

    fun getObserverSongList(): LiveData<List<Song>> {
        return mObserverSongList;
    }

    fun removeSong(songId: Long) : Int {
        return songRepository.removeSong(songId)
    }
}