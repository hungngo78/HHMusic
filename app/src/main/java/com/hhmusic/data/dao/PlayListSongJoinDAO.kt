package com.hhmusic.data.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


import com.hhmusic.data.entities.PlayList
import com.hhmusic.data.entities.Song
import com.hhmusic.data.entities.PlayListSongJoin;

//https://android.jlelse.eu/android-architecture-components-room-relationships-bf473510c14a

@Dao
interface PlayListSongJoinDAO {
    @Insert
    fun insert(playListSongJoin: PlayListSongJoin)

    @Query("select p.* from playLists p, playList_song_join j where p.id=j.playListId AND j.songId=:songId")
    fun getPlayListsForSong(songId: Long): LiveData<List<PlayList>>

    @Query("select s.* from songs s, playList_song_join j where s.id = j.songId and j.playListId= :playListId")
    fun getSongsForPlayList(playListId: Long): LiveData<List<Song>>
}