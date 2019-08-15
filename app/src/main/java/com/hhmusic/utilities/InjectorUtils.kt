package com.hhmusic.utilities

import android.content.Context
import com.hhmusic.data.HHMusicDatabase
import com.hhmusic.data.repository.MusicRepository
import com.hhmusic.viewmodels.*
import com.hhmusic.viewmodels.ViewModelFactory

object InjectorUtils {

    fun provideViewModelFactory(context: Context): ViewModelFactory {
        val repository = getMusicRepository(context)
        return ViewModelFactory(repository)
    }
    fun provideArtistViewModelFactory(context: Context): ArtistViewModelFactory {
        val repository = getMusicRepository(context)
        return ArtistViewModelFactory(repository)
    }

    fun provideAlbumViewModelFactory(context: Context): AlbumViewModelFactory {
        val repository = getMusicRepository(context)
        return AlbumViewModelFactory(repository)
    }
    private fun getMusicRepository(context: Context): MusicRepository {
        return MusicRepository.getInstance(
            HHMusicDatabase.getInstance(context.applicationContext).playListDao(),
            HHMusicDatabase.getInstance(context.applicationContext).songsDao(),
            HHMusicDatabase.getInstance(context.applicationContext).artistsDao(),
            HHMusicDatabase.getInstance(context.applicationContext).albumDao())
    }
}