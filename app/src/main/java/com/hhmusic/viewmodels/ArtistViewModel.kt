package com.hhmusic.viewmodels

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.*
import com.hhmusic.HHMusicApplication
//import com.hhmusic.data.model.Song
import com.hhmusic.data.entities.Song
import android.content.ContentUris
import com.hhmusic.data.entities.Artist
import com.hhmusic.data.repository.MusicRepository


class ArtistViewModel internal constructor(
    private val artistRepository: MusicRepository
) : ViewModel() {


    val mObserverArtistList = MediatorLiveData<List<Artist>>()
    val artistList = MutableLiveData<List<Artist>>()
    //val songList : MutableLiveData<List<Song>> =  MutableLiveData<List<Song>>()

    init {
        var artistList = artistRepository.getArtistList()
        mObserverArtistList.addSource(artistList, mObserverArtistList::setValue)
    }


    fun getObserverArtistList(): LiveData<List<Artist>> {
        return mObserverArtistList;
    }


}