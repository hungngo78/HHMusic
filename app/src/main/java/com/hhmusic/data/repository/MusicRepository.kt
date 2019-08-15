package com.hhmusic.data.repository

import com.hhmusic.data.dao.AlbumDAO
import com.hhmusic.data.dao.ArtistDAO
import com.hhmusic.data.dao.PlayListDAO;
import com.hhmusic.data.dao.SongsDAO


class MusicRepository private constructor(private val playListDao: PlayListDAO, private val songsDao: SongsDAO, private val artistDao: ArtistDAO, private  val albumDao: AlbumDAO) {

        /* PLAY LIST */
        fun getPlayLists() = playListDao.selectAll()
        fun getPlayList(playListId: Long) = playListDao.selectById(playListId)

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

            fun getInstance(playListDao: PlayListDAO, songsDao: SongsDAO, artistDao: ArtistDAO, albumDao: AlbumDAO) =
                instance ?: synchronized(this) {
                    instance ?: MusicRepository(playListDao, songsDao, artistDao, albumDao ).also { instance = it }
                }
        }
}
