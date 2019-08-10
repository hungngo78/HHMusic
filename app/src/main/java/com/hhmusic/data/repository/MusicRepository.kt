package com.hhmusic.data.repository

import com.hhmusic.data.dao.PlayListDAO;
import com.hhmusic.data.dao.SongsDAO


class MusicRepository private constructor(private val playListDao: PlayListDAO, private val songsDao: SongsDAO) {

        /* PLAY LIST */
        fun getPlayLists() = playListDao.selectAll()
        fun getPlayList(playListId: Long) = playListDao.selectById(playListId)

        /* SONG */
        fun getSongList() = songsDao.selectAllSongs()
        fun getSong(songId: Long) = songsDao.selectSongById(songId)

        companion object {
            // For Singleton instantiation
            @Volatile private var instance: MusicRepository? = null

            fun getInstance(playListDao: PlayListDAO, songsDao: SongsDAO) =
                instance ?: synchronized(this) {
                    instance ?: MusicRepository(playListDao, songsDao).also { instance = it }
                }
        }
}