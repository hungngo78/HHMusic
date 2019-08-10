package com.hhmusic.utilities

import android.content.Context
import com.hhmusic.data.HHMusicDatabase
import com.hhmusic.data.repository.MusicRepository
import com.hhmusic.viewmodels.PlayListViewModelFactory
import com.hhmusic.viewmodels.SongViewModelFactory

object InjectorUtils {

    fun providePlayListViewModelFactory(context: Context): PlayListViewModelFactory {
        val repository = getMusicRepository(context)
        return PlayListViewModelFactory(repository)
    }

    fun provideSongViewModelFactory(context: Context): SongViewModelFactory {
        val repository = getMusicRepository(context)
        return SongViewModelFactory(repository)
    }

    private fun getMusicRepository(context: Context): MusicRepository {
        return MusicRepository.getInstance(
            HHMusicDatabase.getInstance(context.applicationContext).playListDao(),
            HHMusicDatabase.getInstance(context.applicationContext).songsDao())
    }
}