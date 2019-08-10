package com.hhmusic.data.repository

import com.hhmusic.data.dao.PlayListDAO;


class PlayListRepository private constructor(private val playListDao: PlayListDAO) {

        fun getPlayLists() = playListDao.selectAll()

        fun getPlayList(playListId: Long) = playListDao.selectById(playListId)

        companion object {
            // For Singleton instantiation
            @Volatile private var instance: PlayListRepository? = null

            fun getInstance(playListDao: PlayListDAO) =
                instance ?: synchronized(this) {
                    instance ?: PlayListRepository(playListDao).also { instance = it }
                }
        }
}