package com.hhmusic.data.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import com.hhmusic.data.entities.Album
import com.hhmusic.data.entities.Artist
import com.hhmusic.data.entities.Song

@Dao
interface AlbumDAO {

    @Query("Select * from albums")
    fun selectAll(): Cursor

    @Query("select s.* from albums a, songs s where a.songId = s.id and a.albumId= :albumId")
    fun getSongListFromAlbum(albumId : Long): LiveData<List<Song>>

    @Query("select count(*) as numberOfTrack, albumId, albumName, songId, imagePathStr from albums group by albumId")
    fun getAllAlbumTrackSong(): LiveData<List<Album>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(album: List<com.hhmusic.data.entities.Album>): LongArray

    @Update
    fun update(song: com.hhmusic.data.entities.Album): Int

    @Query("DELETE FROM albums WHERE songId =:songId")
    fun deleteById(songId: Long): Int

    /*  for ViewModel*/
    @Query("SELECT * FROM albums ORDER BY  albumName")
    fun selectAllbum(): LiveData<List<com.hhmusic.data.entities.Album>>

    @Query("SELECT * FROM albums WHERE albumId = :albumId")
    fun selectSongById(albumId: Long): com.hhmusic.data.entities.Album

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(album: Album): Long

}