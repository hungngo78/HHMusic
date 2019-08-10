package com.hhmusic.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

// https://android.jlelse.eu/android-architecture-components-room-relationships-bf473510c14a
@Entity(tableName = "playList_song_join",
        primaryKeys = [ "playListId", "songId" ],
        foreignKeys = [
            ForeignKey(entity = PlayList::class,
                        parentColumns = ["id"],
                        childColumns = ["playListId"]),
            ForeignKey(entity = Song::class,
                        parentColumns = ["id"],
                        childColumns = ["songId"])
            ])
class PlayListSongJoin (
    @ColumnInfo(name = "playListId") val playListId: Long= 0,
    @ColumnInfo(name = "songId") val songId: Long = 0
){

}