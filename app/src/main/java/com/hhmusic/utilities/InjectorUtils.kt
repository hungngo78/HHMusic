package com.hhmusic.utilities

import android.content.Context
import com.hhmusic.data.HHMusicDatabase
import com.hhmusic.data.repository.MusicRepository
import com.hhmusic.viewmodels.ViewModelFactory

object InjectorUtils {

    fun provideViewModelFactory(context: Context): ViewModelFactory {
        val repository = getMusicRepository(context)
        return ViewModelFactory(repository)
    }

    private fun getMusicRepository(context: Context): MusicRepository {
        return MusicRepository.getInstance(
            HHMusicDatabase.getInstance(context.applicationContext).playListDao(),
            HHMusicDatabase.getInstance(context.applicationContext).songsDao())
    }
}