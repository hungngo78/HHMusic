package com.hhmusic.data.entities

import android.content.ContentValues
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists", primaryKeys = ["artistId","songId"])
data class Artist (
    @ColumnInfo(name = "artistId")
    var artistId: Long = 0,
    @ColumnInfo(name = "artistName")
    var artistName : String ="",
    @ColumnInfo (name = "songId")
    var songId: Long =0,
    var numberOfTrack: Long = 0

) : Parcelable{


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(artistId)
        dest?.writeString(artistName)
        dest?.writeLong(songId)
        dest?.writeLong(numberOfTrack)

    }

    private constructor(parce : Parcel) : this (
        artistId = parce.readLong(),
        artistName = parce.readString(),
        songId = parce.readLong(),
        numberOfTrack = parce.readLong()
    )
    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Artist> {
            override fun createFromParcel(parcel: Parcel) = Artist(parcel)
            override fun newArray(size: Int) = arrayOfNulls<Artist>(size)
        }

        /**
         * Create a new [Song] from the specified [ContentValues].
         *
         * @param values A [ContentValues] that at least contain [.COLUMN_NAME].
         * @return A newly created [Song] instance.
         */
        fun fromContentValues(values: ContentValues): Artist {
            //values?.let {
            val artist = Artist()

            System.out.println("Huong -> artistId = " + artist.artistId)
            System.out.println("Huong -> artistName = " + artist.artistName)
            System.out.println("Huong -> songId = " + artist.songId)
            if (values.containsKey("id")) {
                artist.songId = values.getAsLong("id")!!
            }
            if (values.containsKey("artistName")) {
                artist.artistName = values.getAsString("artistName")
            }
            if (values.containsKey("artistId")) {
                artist.artistId = values.getAsLong("artistId")
            }

            return artist
        }

    }
}