package com.hhmusic.common

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.MutableLiveData
import android.support.v4.media.session.PlaybackStateCompat;
import com.hhmusic.data.entities.Song


class MediaSessionConnection (context : Context) {


    val playbackState = MutableLiveData<PlaybackStateCompat>().apply{
        postValue(EMPTY_PLAYBACK_STATE)
    }

    val nowPlaying = MutableLiveData<MediaMetadataCompat>().apply {
        postValue(NOTHING_PLAYING)
    }

    private lateinit var mediaController : MediaControllerCompat

    val transportControls : MediaControllerCompat.TransportControls
        get() = mediaController.transportControls




}

@Suppress("PropertyName")
public val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f).build()

@Suppress("PropertyName")
public val NOTHING_PLAYING : MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()