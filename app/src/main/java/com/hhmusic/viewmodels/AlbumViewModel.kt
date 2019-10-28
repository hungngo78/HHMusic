package com.hhmusic.viewmodels

import androidx.lifecycle.*
//import com.hhmusic.data.model.Song
import com.hhmusic.data.entities.Song
import com.hhmusic.data.model.Album
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

    fun getObserverSongListFromAlbum(albumId: Long) : LiveData<List<Song>> {
       return  albumRepository.getSongListFromAlbum(albumId)
    }

    fun getObserverAlbumList(): LiveData<List<Album>> {
        return mObserverAlbumtList;
    }


}