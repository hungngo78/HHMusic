package com.hhmusic.data.repository

import com.hhmusic.data.dao.*


class MusicRepository private constructor(private val playListDao: PlayListDAO,
                                          private val playListSongJoinDao: PlayListSongJoinDAO,
                                          private val songsDao: SongsDAO,
                                          private val artistDao: ArtistDAO,
                                          private  val albumDao: AlbumDAO) {

        /* PLAY LIST */
        fun getPlayLists() = playListDao.selectAll()
        fun getPlayList(playListId: Long) = playListDao.selectById(playListId)
        fun getSongListFromPlayList(playListId: Long) = playListSongJoinDao.getSongsForPlayList(playListId)

        /* SONG */
        fun getSongList() = songsDao.selectAllSongs()
        fun getSong(songId: Long) = songsDao.selectSongById(songId)

        /* Artist */
        fun getArtistList() = artistDao.getAllArtistTrackSong()
        fun getSongListFromArtist(artistId: Long) = artistDao.getSongListFromArtist(artistId)

        /* Album */
        fun getAlbumList() = albumDao.getAllAlbumTrackSong()
        fun getSongListFromAlbum(albumId: Long) = albumDao.getSongListFromAlbum(albumId)


        companion object {
            // For Singleton instantiation
            @Volatile private var instance: MusicRepository? = null

            fun getInstance(playListDao: PlayListDAO, playListSongJoinDao: PlayListSongJoinDAO,
                            songsDao: SongsDAO, artistDao: ArtistDAO, albumDao: AlbumDAO) =
                instance ?: synchronized(this) {
                    instance ?: MusicRepository(playListDao, playListSongJoinDao, songsDao, artistDao, albumDao ).also { instance = it }
                }
        }
}
