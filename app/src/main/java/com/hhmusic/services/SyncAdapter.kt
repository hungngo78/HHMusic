package com.hhmusic.services

import android.accounts.Account
import android.content.*
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import com.hhmusic.provider.DBDataContract
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap
import android.net.Uri
import android.os.RemoteException
import android.provider.MediaStore
import com.hhmusic.data.entities.Song

class SyncAdapter @JvmOverloads constructor(
    context: Context,
    autoInitialize: Boolean,
    /**
     * Using a default argument along with @JvmOverloads
     * generates constructor for both method signatures to maintain compatibility
     * with Android 3.0 and later platform versions
     */
    allowParallelSyncs: Boolean = false,
    /*
     * If your app uses a content resolver, get an instance of it
     * from the incoming Context
     */
    val mContentResolver: ContentResolver = context.contentResolver
) : AbstractThreadedSyncAdapter(context, autoInitialize, allowParallelSyncs) {

    val TAG = "SyncAdapter"

    /**
     * Project used when querying content provider. Returns all known fields.
     */
    private val PROJECTION : Array<String> =  arrayOf<String> ( //arrayOf<KProperty1<DBDataContract.Entry, String>>(
        DBDataContract.Entry.COLUMN_NAME_ENTRY_ID,
        DBDataContract.Entry.COLUMN_NAME_TITLE,
        DBDataContract.Entry.COLUMN_NAME_ARTIST_NAME,
        DBDataContract.Entry.COLUMN_NAME_ALBUM_NAME,
        DBDataContract.Entry.COLUMN_NAME_DURATION,
        DBDataContract.Entry.COLUMN_NAME_URI_STR,
        DBDataContract.Entry.COLUMN_NAME_IMAGE_PATH_STR,
        DBDataContract.Entry.COLUMN_NAME_ENTRY_ALBUM_ID,
        DBDataContract.Entry.COLUMN_NAME_ENTRY_ARTIST_ID

    ) //as Array<String>

    override fun onPerformSync(
        account: Account, extras: Bundle, authority: String,
        provider: ContentProviderClient, syncResult: SyncResult
    ) {
        Log.i(TAG, "Beginning media synchronization")
        try {
            var songList : List<Song>

            // get song list from media provider
            songList = getSongList()

            // sync with data in DB
            updateLocalFeedData(songList, syncResult)
        } catch (e: IOException) {
            Log.e(TAG, "Error reading from network: $e")
            syncResult.stats.numIoExceptions++
            return
        } catch (e: OperationApplicationException) {
            Log.e(TAG, "Error updating database: $e")
            syncResult.databaseError = true
            return
        } catch (e: RemoteException) {
            Log.e(TAG, "Error updating database: " + e.toString())
            syncResult.databaseError = true
            return
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Error updating database: " + e.toString())
            syncResult.databaseError = true
            return
        }

        Log.i(TAG, "Media synchronization complete")
    }


    @Throws(
        IOException::class,
        OperationApplicationException::class,
        RemoteException::class,
        IllegalArgumentException::class
    )
    fun updateLocalFeedData(songList: List<Song>, syncResult: SyncResult) {

        val batch = ArrayList<ContentProviderOperation>()

        // Build hash table of incoming entries
        val entryMap = HashMap<Long, Song>()
        for (s in songList) {
            entryMap.put(s.songId, s)
        }

        // Get list of all items
        Log.i(TAG, "Fetching local entries for merge")
        val uri : Uri = DBDataContract.Entry.CONTENT_URI  // Get all entries
        val c = mContentResolver.query(
            uri, PROJECTION, // URI where data was modified
            null, null, null
        )!!
        Log.i(TAG, "Found " + c.count + " local entries. Computing merge solution...")

        // Find stale data
        var entryId: Long
        var title: String
        var artistName: String
        var albumName: String
        var duration: Long
        var uriStr : String
        var imagePathStr: String
        var artistId: Long
        var albumId: Long

        // representing column positions from PROJECTION.
        var entryIdIndex: Int
        var titleIndex: Int
        var artistNameIndex: Int
        var albumNameIndex: Int
        var durationIndex: Int
        var uriStrIndex: Int
        var imagePathStrIndex: Int
        var artistIdIndex: Int
        var albumIdIndex: Int

        while (c.moveToNext()) {
            syncResult.stats.numEntries++

            // get indexes of colums in cursor
            entryIdIndex = c.getColumnIndexOrThrow(DBDataContract.Entry.COLUMN_NAME_ENTRY_ID)
            titleIndex = c.getColumnIndexOrThrow(DBDataContract.Entry.COLUMN_NAME_TITLE)
            artistNameIndex = c.getColumnIndexOrThrow(DBDataContract.Entry.COLUMN_NAME_ARTIST_NAME)
            albumNameIndex = c.getColumnIndexOrThrow(DBDataContract.Entry.COLUMN_NAME_ALBUM_NAME)
            durationIndex = c.getColumnIndexOrThrow(DBDataContract.Entry.COLUMN_NAME_DURATION)
            uriStrIndex = c.getColumnIndexOrThrow(DBDataContract.Entry.COLUMN_NAME_URI_STR)
            imagePathStrIndex = c.getColumnIndexOrThrow(DBDataContract.Entry.COLUMN_NAME_IMAGE_PATH_STR)
            artistIdIndex = c.getColumnIndexOrThrow(DBDataContract.Entry.COLUMN_NAME_ENTRY_ARTIST_ID)
            albumIdIndex = c.getColumnIndexOrThrow(DBDataContract.Entry.COLUMN_NAME_ENTRY_ALBUM_ID)

            // get values from cursor
            entryId = c.getLong(entryIdIndex)
            title = c.getString(titleIndex)
            artistName = c.getString(artistNameIndex)
            albumName = c.getString(albumNameIndex)
            duration = c.getLong(durationIndex)
            uriStr = c.getString(uriStrIndex)
            imagePathStr = c.getString(imagePathStrIndex)
            artistId = c.getLong(artistIdIndex)
            albumId = c.getLong(albumIdIndex)


            val match = entryMap.get(entryId)
            if (match != null) {
                // Entry exists in DB, so remove from entry map to prevent duplicated insert.
                entryMap.remove(entryId)

                // Check to see if the entry needs to be updated
                val existingUri = DBDataContract.Entry.CONTENT_URI.buildUpon()
                                                    .appendPath(entryId.toString()).build()
                if (match!!.title != null && !match!!.title.equals(title) ||
                    match!!.artistName != null && !match!!.artistName.equals(artistName) ||
                    match!!.albumName != null && !match!!.albumName.equals(albumName) ||
                    match!!.artistName != null && !match!!.artistName.equals(artistName) ||
                    !match!!.duration.equals(duration) ||
                    !match!!.artistId.equals(artistId) ||
                    !match!!.albumId.equals(albumId) ||
                    match!!.uriStr != null && !match!!.uriStr.equals(uriStr) ||
                    match!!.imagePathStr != null && !match!!.imagePathStr.equals(imagePathStr)
                ) {
                    // Update existing record
                    Log.i(TAG, "Scheduling update: $existingUri")
                    batch.add(
                        ContentProviderOperation.newUpdate(existingUri)
                            .withValue(DBDataContract.Entry.COLUMN_NAME_TITLE, match!!.title)
                            .withValue(DBDataContract.Entry.COLUMN_NAME_ARTIST_NAME, match!!.artistName)
                            .withValue(DBDataContract.Entry.COLUMN_NAME_ALBUM_NAME, match!!.albumName)
                            .withValue(DBDataContract.Entry.COLUMN_NAME_DURATION, match!!.duration)
                            .withValue(DBDataContract.Entry.COLUMN_NAME_URI_STR, match!!.uriStr)
                            .withValue(DBDataContract.Entry.COLUMN_NAME_IMAGE_PATH_STR, match!!.imagePathStr)
                            .withValue(DBDataContract.Entry.COLUMN_NAME_ENTRY_ARTIST_ID, match!!.artistId)
                            .withValue(DBDataContract.Entry.COLUMN_NAME_ENTRY_ALBUM_ID, match!!.albumId)
                            .build()
                    )
                    syncResult.stats.numUpdates++
                } else {
                    Log.i(TAG, "No action: $existingUri")
                }
            } else {
                // Entry doesn't exist. Remove it from the database.
                val deleteUri = DBDataContract.Entry.CONTENT_URI.buildUpon()
                                                  .appendPath(entryId.toString()).build()

                Log.i(TAG, "Scheduling delete: $deleteUri")

                batch.add(ContentProviderOperation.newDelete(deleteUri).build())
                syncResult.stats.numDeletes++
            }
        }
        c.close()

        // Add new items
        for (e in entryMap.values) {
            Log.i(TAG, "Scheduling insert: entry_id=" + e.songId)
            batch.add(
                ContentProviderOperation.newInsert(DBDataContract.Entry.CONTENT_URI)
                    .withValue(DBDataContract.Entry.COLUMN_NAME_ENTRY_ID, e.songId)
                    .withValue(DBDataContract.Entry.COLUMN_NAME_TITLE, e.title)
                    .withValue(DBDataContract.Entry.COLUMN_NAME_ARTIST_NAME, e.artistName)
                    .withValue(DBDataContract.Entry.COLUMN_NAME_ALBUM_NAME, e.albumName)
                    .withValue(DBDataContract.Entry.COLUMN_NAME_DURATION, e.duration)
                    .withValue(DBDataContract.Entry.COLUMN_NAME_URI_STR, e.uriStr)
                    .withValue(DBDataContract.Entry.COLUMN_NAME_IMAGE_PATH_STR, e.imagePathStr)
                    .withValue(DBDataContract.Entry.COLUMN_NAME_ENTRY_ARTIST_ID, e.artistId)
                    .withValue(DBDataContract.Entry.COLUMN_NAME_ENTRY_ALBUM_ID, e.albumId)
                    .build()
            )
            syncResult.stats.numInserts++
        }

        Log.i(TAG, "Merge solution ready. Applying batch update")
        mContentResolver.applyBatch(DBDataContract.CONTENT_AUTHORITY, batch)
        mContentResolver.notifyChange(
            DBDataContract.Entry.CONTENT_URI, null, // No local observer
            false
            // If syncToNetwork is true, this will attempt to schedule a local sync
            //     * using the sync adapter that's registered for the authority of the
            //     * provided uri. No account will be passed to the sync adapter, so all
            //     * matching accounts will be synchronized.
        )
    }

    private fun getSongList() : List<Song> {
        var listSong : ArrayList<Song> =  ArrayList<Song>()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

        val cursor: Cursor? = mContentResolver.query(
            uri,
            null,
            selection,
            null,
            sortOrder
        )

        if (cursor != null && cursor.moveToFirst()){
            val id : Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val title: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val duration: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val data: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val albumName: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val albumID: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val artistName: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val artistId: Int =  cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)

            do {
                val songId: Long = cursor.getLong(id)
                val songTitle: String = cursor.getString(title)
                val songAlbum: String = cursor.getString(albumName)
                val songArtist: String = cursor.getString(artistName)
                val songDuration: Long = cursor.getLong(duration)
                val songUriStr: String = cursor.getString(data)
                val albumId: Long = cursor.getLong(albumID)
                val artistId: Long = cursor.getLong(artistId)

                val sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart")
                val albumArtUri = ContentUris.withAppendedId(
                    sArtworkUri,
                    albumId
                ).toString()

                listSong.add(
                    Song(
                        songId,
                        songTitle,
                        songArtist,
                        songAlbum,
                        songDuration,
                        songUriStr,
                        albumArtUri,
                        artistId,
                        albumId
                    )
                )
            } while (cursor.moveToNext())
        }

        return listSong
    }
}