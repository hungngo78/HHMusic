package com.hhmusic.data.entities

import android.content.ContentValues
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums", primaryKeys = ["albumId","songId"])
data class Album (
    @ColumnInfo(name = "albumId")
    var albumId: Long = 0,
    @ColumnInfo(name = "albumName")
    var albumName : String ="",
    @ColumnInfo (name = "songId")
    var songId: Long =0,
    @ColumnInfo (name = "imagePathStr")
    var albumeUrl: String = "",
    var numberOfTrack: Long = 0

) : Parcelable{

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {

        dest?.writeLong(albumId)
        dest?.writeString(albumName)
        dest?.writeLong(songId)
        dest?.writeString(albumeUrl)
        dest?.writeLong(numberOfTrack)

    }

    private constructor(parcel : Parcel) : this (
        albumId = parcel.readLong(),
        albumName = parcel.readString(),
        songId = parcel.readLong(),
        albumeUrl = parcel.readString(),
        numberOfTrack = parcel.readLong()

    )
    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Album> {
            override fun createFromParcel(parcel: Parcel) = Album(parcel)
            override fun newArray(size: Int) = arrayOfNulls<Album>(size)
        }

        /**
         * Create a new [Song] from the specified [ContentValues].
         *
         * @param values A [ContentValues] that at least contain [.COLUMN_NAME].
         * @return A newly created [Song] instance.
         */
        fun fromContentValues(values: ContentValues): Album {
            //values?.let {
            val album = Album()

            if (values.containsKey("id")) {
                album.songId = values.getAsLong("id")!!
            }
            if (values.containsKey("albumName")) {
                album.albumName = values.getAsString("albumName")
            }
            if (values.containsKey("albumId")) {
                album.albumId = values.getAsLong("albumId")
            }
            if (values.containsKey("imagePathStr")) {
                album.albumeUrl = values.getAsString("imagePathStr")
            }


            return album
        }

    }
}