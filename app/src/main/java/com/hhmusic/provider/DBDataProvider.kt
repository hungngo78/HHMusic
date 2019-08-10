package com.hhmusic.provider

import android.content.*
import android.database.Cursor
import android.net.Uri
import com.hhmusic.data.HHMusicDatabase
import com.hhmusic.data.entities.Song
import java.util.ArrayList
import java.util.concurrent.Callable


/**
 * A [ContentProvider] based on a Room database.
 *
 *
 * Note that you don't need to implement a ContentProvider unless you want to expose the data
 * outside your process or your application already uses a ContentProvider.
 */
class DBDataProvider: ContentProvider() {


    companion object {
        val SONGS_TABLE_NAME = "songs"

        /** The authority of this content provider.  */
        private val AUTHORITY = "com.hhmusic.android.contentprovider.provider"

        /** The URI for the Cheese table.  */
        private val URI_PLAYLIST = Uri.parse(
            "content://" + AUTHORITY + "/" + SONGS_TABLE_NAME
        )

        /** The match code for some items in the Song table.  */
        private val CODE_SONGS = 1
        private val CODE_SONG_ID = 2

        /** The URI matcher.  */
        /**
         * UriMatcher, used to decode incoming URIs.
         */
        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH)
        init {
            MATCHER.addURI(AUTHORITY, SONGS_TABLE_NAME, CODE_SONGS)
            MATCHER.addURI(AUTHORITY, SONGS_TABLE_NAME + "/*", CODE_SONG_ID)
        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val code = MATCHER.match(uri)
        if (code == CODE_SONGS || code == CODE_SONG_ID) {
            val context = context ?: return null
            val songsDao = HHMusicDatabase.getInstance(context).songsDao()
            val cursor: Cursor
            if (code == CODE_SONGS) {
                cursor = songsDao.selectAll()
            } else {
                cursor = songsDao.selectById(ContentUris.parseId(uri))
            }
            cursor.setNotificationUri(context.contentResolver, uri)
            return cursor
        } else {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? {
        when (MATCHER.match(uri)) {
            /**
             * MIME type for lists of entries.
             */
            CODE_SONGS -> return "vnd.android.cursor.dir/" + "/vnd.basicsyncadapter.entries";

            /**
             * MIME type for individual entries.
             */
            CODE_SONG_ID -> return "vnd.android.cursor.dir/" + "/vnd.basicsyncadapter.entry";

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (MATCHER.match(uri)) {
            CODE_SONGS -> {
                val context = context ?: return null

                values?.let {
                    val id = HHMusicDatabase.getInstance(context).songsDao()
                        .insert(Song.fromContentValues(values)).toLong()

                    context.contentResolver.notifyChange(uri, null)
                    return ContentUris.withAppendedId(uri, id)
                }

                return null
            }
            CODE_SONG_ID -> throw IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(
        uri: Uri, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        when (MATCHER.match(uri)) {
            CODE_SONGS -> throw IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            CODE_SONG_ID -> {
                val context = context ?: return 0
                val count = HHMusicDatabase.getInstance(context).songsDao()
                    .deleteById(ContentUris.parseId(uri))

                context.contentResolver.notifyChange(uri, null)
                return count
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        when (MATCHER.match(uri)) {
            CODE_SONGS -> throw IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            CODE_SONG_ID -> {
                val context = context ?: return 0

                values?.let {
                    val song = Song.fromContentValues(values)
                    song.songId = ContentUris.parseId(uri)
                    val count = HHMusicDatabase.getInstance(context).songsDao()
                        .update(song)

                    context.contentResolver.notifyChange(uri, null)
                    return count
                }

                return 0;
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    @Throws(OperationApplicationException::class)
    override fun applyBatch(
        operations: ArrayList<ContentProviderOperation>
    ): Array<ContentProviderResult> {
        // quái, chỗ nay ko biet viet lam sao
         //val context = context ?: return arrayOfNulls(0)!!
        val context = context

        val database = HHMusicDatabase.getInstance(context)

        return database.runInTransaction(Callable<Array<ContentProviderResult>> {
            super@DBDataProvider.applyBatch(
                operations
            )
        })
    }

    override fun bulkInsert(uri: Uri, valuesArray: Array<ContentValues>): Int {
        when (MATCHER.match(uri)) {
            CODE_SONGS -> {
                val context = context ?: return 0
                val database = HHMusicDatabase.getInstance(context)

                var songs: List<Song> = listOf() //arrayOfNulls<Song>(valuesArray.size)
                for (i in valuesArray.indices) {
                    //songs[i] = Song.fromContentValues(valuesArray[i])
                    songs += Song.fromContentValues(valuesArray[i])
                }

                return database.songsDao().insertAll(songs).size
            }
            CODE_SONG_ID -> throw IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}