package com.hhmusic.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hhmusic.data.dao.*
import com.hhmusic.data.entities.PlayList
import com.hhmusic.data.entities.PlayListSongJoin
import com.hhmusic.data.entities.Song


class MusicRepository private constructor(private val playListDao: PlayListDAO,
                                          private val playListSongJoinDao: PlayListSongJoinDAO,
                                          private val songsDao: SongsDAO,
                                          private val artistDao: ArtistDAO,
                                          private  val albumDao: AlbumDAO) {

        /* PLAY LIST */
        fun getPlayLists() = playListDao.selectAll()
        fun getPlayList(playListId: Long) = playListDao.selectById(playListId)
        fun getSongListFromPlayList(playListId: Long) = playListSongJoinDao.getSongsForPlayList(playListId)
        fun addNewPlayList(playListName: String) : Long {
            var result : Long = 0

            var list: List<PlayList> = playListDao.selectByName(playListName)

            if (list.size == 0) {
                var newPlayList = PlayList()
                newPlayList.name = playListName
                newPlayList.default= false

                result = playListDao.insert(newPlayList)
            }

            return result
        }
        fun addSongToPlayList(songId: Long, playListName: String) : Long {
            var result : Long = 0

            var song: Song = songsDao.selectSongById(songId)
            var playLists: List<PlayList> = playListDao.selectByName(playListName)
            if ((playLists.size == 1) && (song != null)) {
                var existedRecord : PlayListSongJoin = playListSongJoinDao.getRecordByPlayListIdAndSongId(playLists.get(0).playListId, song.songId)
                if (existedRecord == null) {
                    var playListSong = PlayListSongJoin(playLists.get(0).playListId, song.songId)
                    playListSongJoinDao.insert(playListSong)

                    result = song.songId
                }
            }

            return result
        }

        /* SONG */
        fun getSongList() = songsDao.selectAllSongs()
        fun getRecentlyPlayedSong() = songsDao.getRecentlyPlayedSongs()
        fun getMostPlayedSong() = songsDao.getMostPlayedSongs()
        fun getSong(songId: Long) = songsDao.selectSongById(songId)
        fun removeSong(songId: Long) : Int {
            playListSongJoinDao.deleteBySongId(songId)

            var result: Int = 0
            result = songsDao.deleteById(songId)

            return result
        }
        fun updateSong(song: Song) : Int {
            return songsDao.update(song)
        }

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
                    instance ?: MusicRepository(playListDao, playListSongJoinDao, songsDao,
                                                    artistDao, albumDao ).also { instance = it }
                }
        }
}
