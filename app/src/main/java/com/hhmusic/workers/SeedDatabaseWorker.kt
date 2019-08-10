package com.hhmusic.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.hhmusic.data.HHMusicDatabase
import com.hhmusic.data.entities.PlayList

class SeedDatabaseWorker (
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    private val TAG by lazy { SeedDatabaseWorker::class.java.simpleName }

    override fun doWork(): Result {
        return try {
            applicationContext.assets.open("play_lists.json").use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val plantType = object : TypeToken<List<PlayList>>() {}.type
                    val playList: List<PlayList> = Gson().fromJson(jsonReader, plantType)

                    val database = HHMusicDatabase.getInstance(applicationContext)
                    database.playListDao().insertAll(playList)

                    Result.success()
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error seeding database", ex)
            Result.failure()
        }
    }
}