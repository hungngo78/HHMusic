package com.hhmusic.data.entities

import android.content.ContentValues

import android.os.Parcel
import android.os.Parcelable
import com.hhmusic.utilities.HHMusicConstants
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//@SuppressLint("ParcelCreator")
@Entity(tableName = "songs")
data class Song(  @PrimaryKey @ColumnInfo(name = "id") var songId: Long = 0,
                    @ColumnInfo(name = "title") var title: String = "",
                    @ColumnInfo(name = "artistName") var artistName: String = "",
                    @ColumnInfo(name = "albumName") var albumName: String = "",
                    @ColumnInfo(name = "duration") var duration: Long = 0,
                    @ColumnInfo(name = "uriStr") var uriStr: String = "",
                    @ColumnInfo(name = "imagePathStr") var imagePathStr: String = "",
                    @ColumnInfo(name = "artistId") var artistId: Long = 0,
                    @ColumnInfo(name = "albumId") var albumId: Long = 0
) : Parcelable{


    private constructor(parcel: Parcel) : this (
        songId = parcel.readLong(),
        title = parcel.readString(),
        artistName = parcel.readString(),
        albumName = parcel.readString(),
        duration = parcel.readLong(),
        uriStr = parcel.readString(),
        imagePathStr = parcel.readString(),
        artistId = parcel.readLong(),
        albumId =  parcel.readLong()
    )

    override fun writeToParcel(dest: Parcel, flags: Int)  {
        dest.writeLong(songId)
        dest.writeString(title)
        dest.writeString(artistName)
        dest.writeString(albumName)
        dest.writeLong(duration)
        dest.writeString(uriStr)
        dest.writeString(imagePathStr)
        dest.writeLong(artistId)
        dest.writeLong(albumId)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Song> {
            override fun createFromParcel(parcel: Parcel) = Song(parcel)
            override fun newArray(size: Int) = arrayOfNulls<Song>(size)
        }

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
                if (values.containsKey("artistId")) {
                    song.artistId = values.getAsLong("artistId")!!
                }
                if (values.containsKey("albumId")) {
                    song.albumId = values.getAsLong("albumId")!!
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
                if (values.containsKey("uriStr")) {
                    song.uriStr = values.getAsString("uriStr")
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
}
