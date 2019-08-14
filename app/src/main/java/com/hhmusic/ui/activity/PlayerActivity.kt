package com.hhmusic.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Pair
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.util.ErrorMessageProvider
import com.google.android.exoplayer2.util.Util
import com.hhmusic.data.entities.Song
import com.hhmusic.databinding.ContentPlayerBinding

import android.view.View.OnClickListener

import kotlinx.android.synthetic.main.activity_player.*
import com.google.android.exoplayer2.source.MediaSource
import android.view.KeyEvent
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.android.exoplayer2.upstream.FileDataSourceFactory
import com.hhmusic.HHMusicApplication
import com.hhmusic.utilities.PlayerManager


class PlayerActivity : AppCompatActivity(), PlaybackPreparer, PlayerControlView.VisibilityListener, OnClickListener {

    lateinit var binding: ContentPlayerBinding

    private var startAutoPlay: Boolean = true
    private var startWindow: Int = C.INDEX_UNSET
    private var startPosition: Long = C.TIME_UNSET

    // Saved instance state keys.
    private val KEY_WINDOW = "window"
    private val KEY_POSITION = "position"
    private val KEY_AUTO_PLAY = "auto_play"

    private var playerManager: PlayerManager? = null


    companion object {
        val ACTION_VIEW = "com.hhmusic.android.action.VIEW"
        //val ACTION_VIEW_LIST = "com.hhmusic.android.action.VIEW_LIST"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        // set background color
        setTheme(com.hhmusic.R.style.PlayerTheme_Spherical)

        super.onCreate(savedInstanceState)
        setContentView(com.hhmusic.R.layout.activity_player)
        setSupportActionBar(toolbar)

        binding = DataBindingUtil.setContentView<ContentPlayerBinding>(this, com.hhmusic.R.layout.content_player)

        playerManager = (application as HHMusicApplication).getPlayerManager()
        val action = intent.action
        if (ACTION_VIEW == action) {
            val song: Song? = intent.getParcelableExtra<Parcelable>(MainActivity.KEY_SONGS) as Song
            song?.let {
                var songList: ArrayList<Song> = ArrayList()
                songList.add(song)
                playerManager?.setSongList(songList)

                // bind song object onto UI
                binding.songItem = song
            }
        }

        binding.addToPlaylist.setOnClickListener(this)

        binding.playerView.setControllerVisibilityListener(this)   // use the controller prev, next ...
        binding.playerView.setControlDispatcher(PlayerControlDispatcher())  // detect the click event of exoplayer play/pause button
        binding.playerView.setErrorMessageProvider(PlayerErrorMessageProvider())
        binding.playerView.requestFocus()

        if (savedInstanceState != null) {
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY)
            startWindow = savedInstanceState.getInt(KEY_WINDOW)
            startPosition = savedInstanceState.getLong(KEY_POSITION)

            playerManager?.updateStartPosition(startAutoPlay, startWindow, startPosition)
        } else {
            clearStartPosition()
        }
    }

    override fun onClick(view: View) {
        playerManager?.togglePlayStop()
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
        playerManager?.retry()
    }

    // PlaybackControlView.VisibilityListener implementation
    override fun onVisibilityChange(visibility: Int) {
        //
    }

    private fun initializePlayer() {
        playerManager?.setPlayerEventListener(PlayerEventListener())

        binding.playerView.setPlayer(playerManager?.getPlayer())
        binding.playerView.setPlaybackPreparer(this)

        playerManager?.play()
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory = FileDataSourceFactory()
        return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }

    private fun updateStartPosition() {
        playerManager?.updateStartPosition()
    }

    private fun clearStartPosition() {
        playerManager?.clearStartPosition()
    }

    private fun releasePlayer() {
        playerManager?.releasePlayer()
    }

    private inner class PlayerControlDispatcher : DefaultControlDispatcher() {
        override fun dispatchSetPlayWhenReady(player: Player?, playWhenReady: Boolean): Boolean {
            // Play button clicked: true
            // Paused button clicked: false
            playerManager?.isPlaying = playWhenReady

            return super.dispatchSetPlayWhenReady(player, playWhenReady)
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
