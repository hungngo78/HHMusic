package com.hhmusic.viewmodels

import kotlinx.coroutines.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.hhmusic.data.entities.PlayList
import com.hhmusic.data.entities.Song
import com.hhmusic.data.repository.MusicRepository
import kotlinx.coroutines.GlobalScope

class PlayListViewModel internal constructor(
    private val playListRepository: MusicRepository
) : ViewModel() {

    private val playLists = MediatorLiveData<List<PlayList>>()

    init {
        val livePlantList = playListRepository.getPlayLists()

        playLists.addSource(livePlantList, playLists::setValue)
    }

    fun getPlayLists() = playLists

    fun getSongsByPlayList(playListId: Long) : LiveData<List<Song>> {
        return playListRepository.getSongListFromPlayList(playListId)
    }

    fun addNewPlayList(playListName: String) : Long  {
        return playListRepository.addNewPlayList(playListName)
    }
    fun addSongToPlayList(songId: Long, playListName: String) : Long {
        return playListRepository.addSongToPlayList(songId, playListName)
    }

    fun getRecentlyPlayedSongs(): LiveData<List<Song>> {
        return playListRepository.getRecentlyPlayedSong()
    }

    fun getMostPlayedSongs(): LiveData<List<Song>> {
        return playListRepository.getMostPlayedSong()
    }
}
