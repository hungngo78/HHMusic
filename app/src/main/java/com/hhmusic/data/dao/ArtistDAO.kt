package com.hhmusic.data.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import com.hhmusic.data.entities.Artist
import com.hhmusic.data.entities.Song

@Dao
interface ArtistDAO {

    @Query("Select * from artists")
    fun selectAll(): Cursor

    @Query("select s.* from artists a, songs s where a.songId = s.id and a.artistId= :artistId")
    fun getSongListFromArtist(artistId : Long): LiveData<List<Song>>

    @Query("select count(*) as numberOfTrack, artistId, artistName, songId from artists group by artistId")
    fun getAllArtistTrackSong(): LiveData<List<Artist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(artist: List<com.hhmusic.data.entities.Artist>): LongArray

    @Update
    fun update(song: com.hhmusic.data.entities.Artist): Int

    @Query("DELETE FROM artists WHERE songId =:songId")
    fun deleteById(songId: Long): Int

    /*  for ViewModel*/
    @Query("SELECT * FROM artists ORDER BY  artistName")
    fun selectAllArtist(): LiveData<List<com.hhmusic.data.entities.Artist>>

    @Query("SELECT * FROM artists WHERE artistId = :artistId")
    fun selectSongById(artistId: Long): com.hhmusic.data.entities.Artist

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(artist: Artist): Long

}