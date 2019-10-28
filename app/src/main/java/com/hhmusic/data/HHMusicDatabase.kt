package com.hhmusic.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.hhmusic.data.dao.*
import com.hhmusic.data.entities.*

import com.hhmusic.workers.SeedDatabaseWorker

/**
 * The Room database for this app
 */
@Database(entities = [PlayList::class, Song::class, PlayListSongJoin::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class HHMusicDatabase : RoomDatabase() {

    abstract fun playListDao(): PlayListDAO
    abstract fun songsDao(): SongsDAO
    abstract fun artistsDao(): ArtistDAO
    abstract fun playListSongJoinDao(): PlayListSongJoinDAO
    abstract fun albumDao() : AlbumDAO

    companion object {

        // For Singleton instantiation
        @Volatile private var sInstance: HHMusicDatabase? = null

        fun getInstance(context: Context): HHMusicDatabase {
            return sInstance ?: synchronized(this) {
                sInstance ?: buildDatabase(context).also { sInstance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): HHMusicDatabase {
            return Room.databaseBuilder(context, HHMusicDatabase::class.java, "hhmusic_db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
                        WorkManager.getInstance().enqueue(request)
                    }
                })
                .build()
        }
    }
}