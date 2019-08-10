package com.hhmusic.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hhmusic.data.entities.PlayList
import com.hhmusic.data.repository.PlayListRepository

class PlayListViewModel internal constructor(
    private val playListRepository: PlayListRepository
) : ViewModel() {

    private val plantList = MediatorLiveData<List<PlayList>>()

    init {
        val livePlantList = playListRepository.getPlayLists()

        plantList.addSource(livePlantList, plantList::setValue)
    }

    fun getPlants() = plantList

}
