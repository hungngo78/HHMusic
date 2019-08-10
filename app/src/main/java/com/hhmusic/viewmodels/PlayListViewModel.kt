package com.hhmusic.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.hhmusic.data.entities.PlayList
import com.hhmusic.data.repository.MusicRepository

class PlayListViewModel internal constructor(
    private val playListRepository: MusicRepository
) : ViewModel() {

    private val plantList = MediatorLiveData<List<PlayList>>()

    init {
        val livePlantList = playListRepository.getPlayLists()

        plantList.addSource(livePlantList, plantList::setValue)
    }

    fun getPlants() = plantList

}
