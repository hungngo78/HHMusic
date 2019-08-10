package com.hhmusic.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hhmusic.data.repository.MusicRepository

class SongViewModelFactory (
    private val repository: MusicRepository
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = SongViewModel(repository) as T
    }
