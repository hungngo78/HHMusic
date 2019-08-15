package com.hhmusic.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hhmusic.data.repository.MusicRepository

class AlbumViewModelFactory (
    private val repository: MusicRepository
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = AlbumViewModel(repository) as T
    }
