package com.hhmusic.data.entities

import android.content.ContentValues
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Album (
    var albumId: Long = 0,
    var albumName : String ="",
    var songId: Long =0,
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
    }
}