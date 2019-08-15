package com.hhmusic.data.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import com.hhmusic.data.entities.PlayList
import com.hhmusic.data.entities.Song
import java.util.concurrent.CompletableFuture

@Dao
interface SongsDAO {
    /* for Content Provider */
    @Query("SELECT * FROM songs ORDER BY title")
    fun selectAll(): Cursor

    @Query("SELECT * FROM songs WHERE id = :songId")
    fun selectById(songId: Long): Cursor    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(song: Song): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(songs: List<Song>): LongArray

    @Update
    fun update(song: Song): Int

    @Query("DELETE FROM songs WHERE id = :songId")
    fun deleteById(songId: Long): Int

    /*  for ViewModel*/
    @Query("SELECT * FROM songs ORDER BY title")
    fun selectAllSongs(): LiveData<List<Song>>

    @Query("SELECT * FROM songs WHERE id = :songId")
    fun selectSongById(songId: Long): Song



}