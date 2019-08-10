package com.hhmusic.data.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import java.util.List;

import com.hhmusic.data.entities.PlayList
import com.hhmusic.data.entities.Song
import com.hhmusic.data.entities.PlayListSongJoin;

@Dao
interface PlayListSongJoinDAO {
    @Insert
    fun insert(playListSongJoin: PlayListSongJoin)

    @Query("SELECT * FROM playLists INNER JOIN playList_song_join " +
            "ON playLists.id=playList_song_join.playListId " +
            "WHERE playList_song_join.songId=:songId")
    fun getPlayListsForSong(songId: Long): Cursor

    @Query("SELECT * FROM songs INNER JOIN playList_song_join " +
            "ON songs.id=playList_song_join.songId " +
            "WHERE playList_song_join.playListId=:playListId")
    fun getSongsForPlayList(playListId: Long): Cursor


}