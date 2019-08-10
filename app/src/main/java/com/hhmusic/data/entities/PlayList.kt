package com.hhmusic.data.entities

import android.content.ContentValues
import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playLists")
class PlayList(

) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var playListId: Long = 0

    @ColumnInfo(name = "name") var name: String = ""
    @ColumnInfo(name = "isDefault") var default: Boolean = true

    companion object {
        val PLAY_LIST = arrayOf(
            "Favorites",
            "Most played",
            "Recently played",
            "Newly added",
            "My playlist"
        )
    }

    public fun getImageUrl(): String?{
        return "https://hungngobucket1.s3-us-west-1.amazonaws.com/Production/1000/Black+Sunflower/image1.jpg"
    }
}