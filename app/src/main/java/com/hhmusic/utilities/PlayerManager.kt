package com.hhmusic.utilities

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.FileDataSourceFactory
import com.hhmusic.data.entities.Song

class PlayerManager private constructor(
    private val context: Context,
    private var playerEventListener: Player.EventListener?
){
    private var player: SimpleExoPlayer? = null

    private var songList: ArrayList<Song> = ArrayList()
    private var mediaSource: MediaSource? = null

    private var startAutoPlay: Boolean = true
    private var startWindow: Int = C.INDEX_UNSET
    private var startPosition: Long = C.TIME_UNSET

    var isPlaying: Boolean = false
    get() = field
    set(value) {field = value}

    init {
        initializePlayer()
    }

    companion object {
        @Volatile private var INSTANCE: PlayerManager ? = null
        fun getInstance(ctx: Context, listener: Player.EventListener?): PlayerManager{
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = PlayerManager(ctx, listener)
                }
            }
            return INSTANCE!!
        }
    }

    public fun initializePlayer() {
        player?:let{
            player = ExoPlayerFactory.newSimpleInstance(
                context, DefaultRenderersFactory(context)
                , DefaultTrackSelector(),
                DefaultLoadControl()
            )

            player?.apply {
                // AudioAttributes here from exoplayer package !!!
                val attr = AudioAttributes.Builder().setUsage(C.USAGE_MEDIA)
                    .setContentType(C.CONTENT_TYPE_MUSIC)
                    .build()
                // In 2.9.X you don't need to manually handle audio focus :D
                setAudioAttributes(attr, true)

                if (playerEventListener != null)
                    addListener(playerEventListener)

                playWhenReady = startAutoPlay
            }
        }
    }

    // no need to expose wrapped ExoPlayer
    fun getPlayer() = player

    fun setSongList(list: ArrayList<Song>) {
        if (list.size > 0) {
            songList = list

            var uris: ArrayList<Uri> = ArrayList()
            for (s: Song in songList) {
                var uri = Uri.parse(s?.uriStr)
                uris.add(uri)

                // build media sources for player
                val mediaSources = arrayOfNulls<MediaSource>(uris.size)
                for (i in uris.indices) {
                    mediaSources[i] = buildMediaSource(uris[i])
                }
                mediaSource = if (mediaSources.size == 1) mediaSources[0] else ConcatenatingMediaSource(*mediaSources)
            }
        }
    }

    fun setPlayerEventListener(_listener: Player.EventListener) {
        playerEventListener = _listener
        player?.addListener(playerEventListener)
    }

    fun play() {
        if (!isPlaying) {
            //player?.setPlayWhenReady(true);
            //player?.getPlaybackState();

            // seek player to previous position
            val haveStartPosition = startWindow != C.INDEX_UNSET
            if (haveStartPosition) {
                player?.seekTo(startWindow, startPosition)
            }

            player?.prepare(mediaSource, !haveStartPosition, false)
            isPlaying = true
        }
    }

    fun retry() {
        player?.retry()
        isPlaying = true
    }

    fun stop() {
        if (isPlaying) {
            //player?.setPlayWhenReady(false);
            //player?.getPlaybackState();

            updateStartPosition()
            player?.stop(false)

            isPlaying = false
        }
    }

    fun togglePlayStop() {
        if (isPlaying)
            stop()
        else
            retry()
    }

    fun updateStartPosition(_startAutoPlay: Boolean, _startWindow: Int, _startPosition: Long) {
        if (player != null) {
            startAutoPlay = _startAutoPlay
            startWindow = _startWindow
            startPosition = _startPosition
        }
    }

    fun updateStartPosition() {
        if (player != null) {
            startAutoPlay = player?.getPlayWhenReady() ?: true
            startWindow = player?.getCurrentWindowIndex() ?: C.INDEX_UNSET
            startPosition = Math.max(0, player?.getContentPosition() ?: C.TIME_UNSET)
        }
    }

    fun clearStartPosition() {
        startAutoPlay = true
        startWindow = C.INDEX_UNSET
        startPosition = C.TIME_UNSET
    }

    fun releasePlayer() {
        if (player != null) {
            //updateStartPosition()
            clearStartPosition()

            player?.release()
            player = null
            mediaSource = null
        }
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory = FileDataSourceFactory()
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }
}