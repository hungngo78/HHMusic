package com.hhmusic.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hhmusic.data.entities.Album
import com.hhmusic.data.entities.Song

@Dao
interface AlbumDAO {

    @Query("select * from songs where albumId= :albumId")
    fun getSongListFromAlbum(albumId: Long): LiveData<List<Song>>

    @Query("select count(*) as numberOfTrack, albumId, albumName, id  as songId , imagePathStr as albumeUrl from songs group by albumId")
    fun getAllAlbumTrackSong(): LiveData<List<Album>>
}
