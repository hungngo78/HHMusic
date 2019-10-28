package com.hhmusic.viewmodels

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.*
import com.hhmusic.HHMusicApplication
import android.content.ContentUris
import com.hhmusic.data.entities.Song


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


}