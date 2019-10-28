package com.hhmusic.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hhmusic.data.model.Artist
import com.hhmusic.data.entities.Song

@Dao
interface ArtistDAO {

    @Query("select * from songs where artistId= :artistId")
    fun getSongListFromArtist(artistId : Long): LiveData<List<Song>>

    @Query("select count(*) as numberOfTrack, artistId, artistName, id as songId from songs group by artistId")
    fun getAllArtistTrackSong(): LiveData<List<Artist>>
}