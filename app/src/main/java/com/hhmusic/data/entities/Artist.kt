package com.hhmusic.data.entities

import android.content.ContentValues
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Artist (
    var artistId: Long = 0,
    var artistName : String ="",
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
    }
}