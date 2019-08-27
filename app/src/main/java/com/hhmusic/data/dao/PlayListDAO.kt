package com.hhmusic.data.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hhmusic.data.entities.PlayList

@Dao
interface PlayListDAO {
    /**
     * Counts the number of playlist in the table.
     *
     * @return The number of playlist.
     *
     */
    @Query("SELECT COUNT(*) FROM playLists")
    abstract fun count(): Int

    @Query("SELECT * FROM playLists ORDER BY name")
    fun selectAll(): LiveData<List<PlayList>>

    @Query("SELECT * FROM playLists WHERE id = :playListId")
    fun selectById(playListId: Long): PlayList

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playList: PlayList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(playLists: List<PlayList>)
}

