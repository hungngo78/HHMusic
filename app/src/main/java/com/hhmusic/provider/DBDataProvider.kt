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

        /** The authority of this content provider.  */
        private val AUTHORITY = DBDataContract.CONTENT_AUTHORITY


        /** The match code for some items in the Song table.  */
        /**
         * URI ID for route: /entries
         */
        val ROUTE_ENTRIES = 1

        /**
         * URI ID for route: /entries/{ID}
         */
        val ROUTE_ENTRIES_ID = 2

        /** The URI matcher.  */
        /**
         * UriMatcher, used to decode incoming URIs.
         */
        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH)
        init {
            MATCHER.addURI(AUTHORITY, "/entries", ROUTE_ENTRIES)
            MATCHER.addURI(AUTHORITY, "/entries/*", ROUTE_ENTRIES_ID)
        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun getType(uri: Uri): String? {
        when (MATCHER.match(uri)) {
            /**
             * MIME type for lists of entries.
             */
            ROUTE_ENTRIES -> return DBDataContract.Entry.CONTENT_TYPE

            /**
             * MIME type for individual entries.
             */
            ROUTE_ENTRIES_ID -> return DBDataContract.Entry.CONTENT_ITEM_TYPE

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val code = MATCHER.match(uri)
        if (code == ROUTE_ENTRIES || code == ROUTE_ENTRIES_ID) {
            val context = context ?: return null
            val songsDao = HHMusicDatabase.getInstance(context).songsDao()
            val cursor: Cursor
            if (code == ROUTE_ENTRIES) {
                cursor = songsDao.selectAll()
            } else {
                //val id = uri.lastPathSegment
                cursor = songsDao.selectById(ContentUris.parseId(uri))
            }
            cursor.setNotificationUri(context.contentResolver, uri)
            return cursor
        } else {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (MATCHER.match(uri)) {
            ROUTE_ENTRIES -> {
                val context = context ?: return null

                values?.let {
                    val id = HHMusicDatabase.getInstance(context).songsDao()
                        .insert(Song.fromContentValues(values)).toLong()

                    context.contentResolver.notifyChange(uri, null)
                    return ContentUris.withAppendedId(uri, id)
                }

                return null
            }
            ROUTE_ENTRIES_ID -> throw IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(
        uri: Uri, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        when (MATCHER.match(uri)) {
            ROUTE_ENTRIES -> throw IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            ROUTE_ENTRIES_ID -> {
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
            ROUTE_ENTRIES -> throw IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            ROUTE_ENTRIES_ID -> {
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
            ROUTE_ENTRIES -> {
                val context = context ?: return 0
                val database = HHMusicDatabase.getInstance(context)

                var songs: List<Song> = listOf() //arrayOfNulls<Song>(valuesArray.size)
                for (i in valuesArray.indices) {
                    //songs[i] = Song.fromContentValues(valuesArray[i])
                    songs += Song.fromContentValues(valuesArray[i])


                }
                return database.songsDao().insertAll(songs).size
            }
            ROUTE_ENTRIES_ID -> throw IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}