package com.hhmusic.data.model

import android.net.Uri
import androidx.annotation.Nullable
import com.hhmusic.utilities.HHMusicConstants

data class Song(public var id: Long) {

    lateinit var title: String
    lateinit var artistName: String
    lateinit var albumName: String
    lateinit var imagePath: Uri


    var duration: Long = 0

    public constructor(id: Long, title: String, artistName: String, albumName: String, duration: Long, imagePath: Uri): this(id) {

        this.title = title
        this.artistName = artistName
        this.albumName = albumName
        this.duration = duration
        this.id = id
        this.imagePath = imagePath

    }

    public fun getImageUrl(): String?{

        if (imagePath != null) {
            return imagePath.toString()
        } else
            return null
    }
    public fun getDurationFormat(): String?{

        return HHMusicConstants.setCorrectDuration(duration)
    }
}

