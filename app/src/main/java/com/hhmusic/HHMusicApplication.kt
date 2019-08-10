package com.hhmusic

import android.app.Application
import android.content.Context

class HHMusicApplication: Application(){


    init {
        instance = this
    }

    // create static
    companion object {

        private var instance: HHMusicApplication ? = null

        fun  applicationContext(): Context{
            return instance!!.applicationContext

        }


    }

    override fun onCreate() {
        super.onCreate()
        val context: Context = HHMusicApplication.applicationContext()
    }


}