package com.hhmusic.utilities

import java.lang.reflect.Constructor

object HHMusicConstants {




    public fun setCorrectDuration(songs_duration: Long): String? {
        var songs_duration = songs_duration
        // TODO Auto-generated method stub
        var result: String
        if (songs_duration != null) {

            var seconds = songs_duration / 1000
            val minutes = seconds / 60
            seconds = seconds % 60

            if (seconds < 10) {
                result = "$minutes:0$seconds"
            } else {
                result = "$minutes:$seconds"
            }
            return result
        }
        return "00:00"
    }

}

