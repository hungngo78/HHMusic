package com.hhmusic.viewmodels

import androidx.lifecycle.*
//import com.hhmusic.data.model.Song
import com.hhmusic.data.entities.Song
import com.hhmusic.data.model.Artist
import com.hhmusic.data.repository.MusicRepository


class ArtistViewModel internal constructor(
    private val artistRepository: MusicRepository
) : ViewModel() {


    val mObserverArtistList = MediatorLiveData<List<Artist>>()
    val artistList = MutableLiveData<List<Artist>>()
    val songList : MutableLiveData<List<Song>> =  MutableLiveData<List<Song>>()

    init {
        var artistList = artistRepository.getArtistList()
        mObserverArtistList.addSource(artistList, mObserverArtistList::setValue)
    }

    fun getSongListFromArtist(artistId : Long)  : LiveData<List<Song>> {

        return artistRepository.getSongListFromArtist(artistId);

    }

    fun getObserverArtistList(): LiveData<List<Artist>> {
        return mObserverArtistList;
    }


}