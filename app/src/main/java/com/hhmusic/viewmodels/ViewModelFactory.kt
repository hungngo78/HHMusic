package com.hhmusic.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hhmusic.data.repository.MusicRepository

class ViewModelFactory (
    private val repository: MusicRepository
    ) : ViewModelProvider.NewInstanceFactory() {

        //@Suppress("UNCHECKED_CAST")
        //override fun <T : ViewModel> create(modelClass: Class<T>) = PlayListViewModel(repository) as T

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            try {
                //var res : T = modelClass.newInstance()

                if (modelClass.simpleName.equals("SongViewModel")) {
                    return SongViewModel(repository) as T
                } else if (modelClass.simpleName.equals("PlayListViewModel")) {
                    return PlayListViewModel(repository) as T
                } else if (modelClass.simpleName.equals("AlbumViewModel")) {
                    return AlbumViewModel(repository) as T
                } else if (modelClass.simpleName.equals("ArtistViewModel")) {
                    return ArtistViewModel(repository) as T
                }
            } catch (e: InstantiationException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            }
            return null as T
        }
    }

