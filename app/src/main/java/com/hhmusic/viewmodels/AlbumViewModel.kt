package com.hhmusic.viewmodels

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.*
import com.hhmusic.HHMusicApplication
//import com.hhmusic.data.model.Song
import com.hhmusic.data.entities.Song
import android.content.ContentUris
import com.hhmusic.data.entities.Album
import com.hhmusic.data.entities.Artist
import com.hhmusic.data.repository.MusicRepository


class AlbumViewModel internal constructor(
    private val albumRepository: MusicRepository
) : ViewModel() {


    val mObserverAlbumtList = MediatorLiveData<List<Album>>()
    val artistList = MutableLiveData<List<Album>>()
    //val songList : MutableLiveData<List<Song>> =  MutableLiveData<List<Song>>()

    init {
        var albumList = albumRepository.getAlbumList()
        mObserverAlbumtList.addSource(albumList, mObserverAlbumtList::setValue)
    }


    fun getObserverAlbumList(): LiveData<List<Album>> {
        return mObserverAlbumtList;
    }


}