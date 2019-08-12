package com.hhmusic.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Pair
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.util.ErrorMessageProvider
import com.google.android.exoplayer2.util.Util
import com.hhmusic.data.entities.Song
import com.hhmusic.databinding.ContentPlayerBinding

import kotlinx.android.synthetic.main.activity_player.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import android.R
import android.view.KeyEvent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.FileDataSourceFactory




class PlayerActivity : AppCompatActivity(), PlaybackPreparer, PlayerControlView.VisibilityListener {

    lateinit var binding: ContentPlayerBinding

    private var songList: ArrayList<Song> = ArrayList()
    private var songId: Long = 0

    private var player: SimpleExoPlayer? = null
    private var mediaSource: MediaSource? = null
    private var uris: ArrayList<Uri> = ArrayList()

    private var startAutoPlay: Boolean = true
    private var startWindow: Int = 0
    private var startPosition: Long = 0

    // Saved instance state keys.
    private val KEY_WINDOW = "window"
    private val KEY_POSITION = "position"
    private val KEY_AUTO_PLAY = "auto_play"

    val SPHERICAL_STEREO_MODE_EXTRA = "spherical_stereo_mode"
    val SPHERICAL_STEREO_MODE_MONO = "mono"
    val SPHERICAL_STEREO_MODE_TOP_BOTTOM = "top_bottom"
    val SPHERICAL_STEREO_MODE_LEFT_RIGHT = "left_right"

    companion object {
        val ACTION_VIEW = "com.hhmusic.android.action.VIEW"
        val ACTION_VIEW_LIST = "com.hhmusic.android.action.VIEW_LIST"
    }

    private fun getSongToPlay(songId: Long) : Song? {
        for (s: Song in songList) {
            if (s.songId == songId)
                return s
        }
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //val sphericalStereoMode = intent.getStringExtra(SPHERICAL_STEREO_MODE_EXTRA)
        //if (sphericalStereoMode != null) {
            setTheme(com.hhmusic.R.style.PlayerTheme_Spherical)
        //}

        super.onCreate(savedInstanceState)
        setContentView(com.hhmusic.R.layout.activity_player)
        setSupportActionBar(toolbar)

        binding = DataBindingUtil.setContentView<ContentPlayerBinding>(this, com.hhmusic.R.layout.content_player)

        var song: Song? = null
        val bundle: Bundle? = intent.extras

        bundle?.let {
            songList = bundle.getParcelableArrayList<Song>(MainActivity.KEY_SONGS)
            if (songList != null && songList.size > 0) {
                val action = intent.action
                if (ACTION_VIEW == action) {
                    songId = bundle.getLong(MainActivity.KEY_SONG_ID)

                    song = getSongToPlay(songId) ?: songList.get(0)

                    song?.let {
                        var uri = Uri.parse(song?.uriStr)
                        uris.add(uri)

                        binding.songItem = song
                    }
                } else if (ACTION_VIEW_LIST == action) {
                    // get albumId
                    // ....
                } else {
                    showToast(getString(com.hhmusic.R.string.unexpected_intent_action, action))
                    return
                }
            }
        }

        binding.playerView.setControllerVisibilityListener(this)   // use the controller prev, next ...
        binding.playerView.setErrorMessageProvider(PlayerErrorMessageProvider())
        binding.playerView.requestFocus()

        if (savedInstanceState != null) {
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY)
            startWindow = savedInstanceState.getInt(KEY_WINDOW)
            startPosition = savedInstanceState.getLong(KEY_POSITION)
        } else {
            clearStartPosition()
        }
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        releasePlayer()
        clearStartPosition()
    }

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()

            binding.playerView?.let {
                binding.playerView.onResume()
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()

            binding.playerView?.let {
                binding.playerView.onResume()
            }
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            if (binding.playerView != null) {
                binding.playerView.onPause()
            }
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            if (binding.playerView != null) {
                binding.playerView.onPause()
            }
            releasePlayer()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        updateStartPosition()

        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay)
        outState.putInt(KEY_WINDOW, startWindow)
        outState.putLong(KEY_POSITION, startPosition)
    }

    // Activity input
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // See whether the player view wants to handle media or DPAD keys events.
        return binding.playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event)
    }

    // PlaybackControlView.PlaybackPreparer implementation
    override fun preparePlayback() {
        player?.retry()
    }

    // PlaybackControlView.VisibilityListener implementation
    override fun onVisibilityChange(visibility: Int) {
        //
    }

    private fun initializePlayer() {

        //if (player == null) {
        player?:let{
            player = ExoPlayerFactory.newSimpleInstance(
                        this, DefaultRenderersFactory(this)
                        , DefaultTrackSelector(),
                        DefaultLoadControl())

            player?.apply {
                // AudioAttributes here from exoplayer package !!!
                val attr = AudioAttributes.Builder().setUsage(C.USAGE_MEDIA)
                        .setContentType(C.CONTENT_TYPE_MUSIC)
                        .build()
                // In 2.9.X you don't need to manually handle audio focus :D
                setAudioAttributes(attr, true)

                addListener(PlayerEventListener())
                playWhenReady = startAutoPlay
            }

            binding.playerView.setPlayer(player)
            binding.playerView.setPlaybackPreparer(this)

            val mediaSources = arrayOfNulls<MediaSource>(uris.size)
            for (i in uris.indices) {
                mediaSources[i] = buildMediaSource(uris[i])
            }
            mediaSource = if (mediaSources.size == 1) mediaSources[0] else ConcatenatingMediaSource(*mediaSources)
        }

        val haveStartPosition = startWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player?.seekTo(startWindow, startPosition)
        }
        player?.prepare(mediaSource, !haveStartPosition, false)
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory = FileDataSourceFactory()
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }

    private fun updateStartPosition() {
        if (player != null) {
            startAutoPlay = player?.getPlayWhenReady() ?: true
            startWindow = player?.getCurrentWindowIndex() ?: 0
            startPosition = Math.max(0, player?.getContentPosition()?:0)
        }
    }

    private fun clearStartPosition() {
        startAutoPlay = true
        startWindow = C.INDEX_UNSET
        startPosition = C.TIME_UNSET
    }

    private fun releasePlayer() {
        if (player != null) {
            updateStartPosition()

            player?.release()
            player = null
            mediaSource = null
        }
    }


    private inner class PlayerEventListener : Player.EventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                showToast("Music play ended")
            }
        }

        override fun onPlayerError(e: ExoPlaybackException?) {
            if (isBehindLiveWindow(e!!)) {
                clearStartPosition()
                initializePlayer()
            }
        }

        private fun isBehindLiveWindow(e: ExoPlaybackException): Boolean {
            if (e.type != ExoPlaybackException.TYPE_SOURCE) {
                return false
            }
            var cause: Throwable? = e.sourceException
            while (cause != null) {
                if (cause is BehindLiveWindowException) {
                    return true
                }
                cause = cause.cause
            }
            return false
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        }
    }

    private inner class PlayerErrorMessageProvider : ErrorMessageProvider<ExoPlaybackException> {

        override fun getErrorMessage(e: ExoPlaybackException): Pair<Int, String> {
            var errorString = getString(com.hhmusic.R.string.error_generic)
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                val cause = e.rendererException
                if (cause is MediaCodecRenderer.DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    if (cause.decoderName == null) {
                        if (cause.cause is MediaCodecUtil.DecoderQueryException) {
                            errorString = getString(com.hhmusic.R.string.error_querying_decoders)
                        } else if (cause.secureDecoderRequired) {
                            errorString = getString(
                                com.hhmusic.R.string.error_no_secure_decoder, cause.mimeType
                            )
                        } else {
                            errorString = getString(com.hhmusic.R.string.error_no_decoder, cause.mimeType)
                        }
                    } else {
                        errorString = getString(
                            com.hhmusic.R.string.error_instantiating_decoder,
                            cause.decoderName
                        )
                    }
                }
            }
            return Pair.create(0, errorString)
        }
    }

    private fun showToast(messageId: Int) {
        showToast(getString(messageId))
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }
}
