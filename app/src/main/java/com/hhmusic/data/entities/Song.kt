package com.hhmusic.data.entities

import android.content.ContentValues
import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hhmusic.utilities.HHMusicConstants


@Entity(tableName = "songs")
class Song(
    @PrimaryKey @ColumnInfo(name = "id") var songId: Long = 0,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "artistName") var artistName: String = "",
    @ColumnInfo(name = "albumName") var albumName: String = "",
    @ColumnInfo(name = "duration") var duration: Long = 0,
    @ColumnInfo(name = "imagePathStr") var imagePathStr: String = ""
) {

    companion object {
        /**
         * Create a new [Song] from the specified [ContentValues].
         *
         * @param values A [ContentValues] that at least contain [.COLUMN_NAME].
         * @return A newly created [Song] instance.
         */
        fun fromContentValues(values: ContentValues): Song {
            //values?.let {
                val song = Song()

                if (values.containsKey("id")) {
                    song.songId = values.getAsLong("id")!!
                }
                if (values.containsKey("title")) {
                    song.title = values.getAsString("title")
                }
                if (values.containsKey("artistName")) {
                    song.artistName = values.getAsString("artistName")
                }
                if (values.containsKey("albumName")) {
                    song.albumName = values.getAsString("albumName")
                }
                if (values.containsKey("duration")) {
                    song.duration = values.getAsLong("duration")
                }
                if (values.containsKey("imagePathStr")) {
                    song.imagePathStr = values.getAsString("imagePathStr")
                }

                return song
            //}
            //return null!!
        }
    }

    public fun getImageUrl(): String?{
        return imagePathStr
    }
    public fun getDurationFormat(): String?{

        return HHMusicConstants.setCorrectDuration(duration)
    }

    override fun toString() = title
}