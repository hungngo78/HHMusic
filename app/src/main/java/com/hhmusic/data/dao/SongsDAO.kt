package com.hhmusic.data.dao

import android.database.Cursor
import androidx.room.*
import com.hhmusic.data.entities.Song
import java.util.concurrent.CompletableFuture

@Dao
interface SongsDAO {
    @Query("SELECT * FROM songs ORDER BY title")
    fun selectAll(): Cursor

    @Query("SELECT * FROM songs WHERE id = :songId")
    fun selectById(songId: Long): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(song: Song): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(songs: List<Song>): LongArray

    @Update
    fun update(song: Song): Int

    @Query("DELETE FROM songs WHERE id = :songId")
    fun deleteById(songId: Long): Int
}