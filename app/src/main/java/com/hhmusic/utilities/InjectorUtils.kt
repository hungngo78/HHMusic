package com.hhmusic.utilities

import android.content.Context
import com.hhmusic.data.HHMusicDatabase
import com.hhmusic.data.repository.PlayListRepository
import com.hhmusic.viewmodels.PlayListViewModelFactory

object InjectorUtils {

    fun providePlayListViewModelFactory(context: Context): PlayListViewModelFactory {
        val repository = getPlayListRepository(context)
        return PlayListViewModelFactory(repository)
    }

    private fun getPlayListRepository(context: Context): PlayListRepository {
        return PlayListRepository.getInstance(
            HHMusicDatabase.getInstance(context.applicationContext).playListDao())
    }
}