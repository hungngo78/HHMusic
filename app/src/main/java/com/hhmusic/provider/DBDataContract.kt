package com.hhmusic.provider

import android.content.ContentResolver
import android.net.Uri

class DBDataContract {

    private constructor() {
    }


    companion object {
        /**
         * Content provider authority.
         */
        val CONTENT_AUTHORITY = "com.hhmusic.android.contentprovider.provider"

        /**
         * Base URI. (content://com.hhmusic.android.contentprovider.provider)
         */
        val BASE_CONTENT_URI: Uri = Uri.parse("content://$CONTENT_AUTHORITY/")

        /**
         * Path component for "entry"-type resources..
         */
        private val PATH_ENTRIES = "entries"
    }

     class Entry {
         companion object {
             /**
              * MIME type for lists of entries.
              */
             val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.basicsyncadapter.entries"
             /**
              * MIME type for individual entries.
              */
             val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.basicsyncadapter.entry"

             /**
              * Fully qualified URI for "entry" resources.
              */
             val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build()

             /**
              * Atom ID. (Note: Not to be confused with the database primary key, which is _ID.
              */
             val COLUMN_NAME_ENTRY_ID = "id"
             val COLUMN_NAME_TITLE = "title"
             val COLUMN_NAME_ARTIST_NAME = "artistName"
             val COLUMN_NAME_ALBUM_NAME = "albumName"
             val COLUMN_NAME_DURATION = "duration"
             val COLUMN_NAME_IMAGE_PATH_STR = "imagePathStr"
        }
    }

}