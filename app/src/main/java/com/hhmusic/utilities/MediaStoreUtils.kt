package com.hhmusic.utilities

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

object MediaStoreUtils {

    fun deleteFromMediaStore(pathToDelete: String, context: Context) {
        val contentResolver = context.getContentResolver()
        if (contentResolver != null) {
            val matchingIndex = contentResolver!!.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf<String>("_id", "_data"),
                "_data=?",
                arrayOf<String>(pathToDelete),
                null
            )

            if (matchingIndex != null && matchingIndex!!.getCount() > 0) {
                matchingIndex!!.moveToFirst()
                while (!matchingIndex!!.isAfterLast()) {
                    context.getContentResolver().delete(
                        ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            matchingIndex!!.getLong(matchingIndex!!.getColumnIndex("_id"))
                        ), null, null
                    )
                    matchingIndex!!.moveToNext()
                }
                matchingIndex!!.close()
            }
        }
    }
}