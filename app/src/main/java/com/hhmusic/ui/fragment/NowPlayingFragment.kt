package com.hhmusic.ui.fragment

import java.util.Date;
import android.os.Bundle
import android.os.Parcelable
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil
import com.google.android.exoplayer2.source.BehindLiveWindowException
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.util.ErrorMessageProvider
import com.google.android.exoplayer2.util.Util
import com.hhmusic.HHMusicApplication
import com.hhmusic.data.entities.Song
import com.hhmusic.databinding.FragmentNowPlayerBinding
import com.hhmusic.ui.activity.MainActivity
import com.hhmusic.ui.activity.PlayerActivity
import com.hhmusic.utilities.InjectorUtils
import com.hhmusic.utilities.PlayerManager
import com.hhmusic.viewmodels.SongViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NowPlayingFragment(private val myActivity: FragmentActivity): Fragment(), PlaybackPreparer,
                            PlayerControlView.VisibilityListener { //, View.OnClickListener {

    companion object {
        val TAG = "Playing song fragment"
    }

    //lateinit var binding: ContentPlayerBinding
    lateinit var binding: FragmentNowPlayerBinding

    private var playerManager: PlayerManager? = null

    private var mSong: Song? = null
    lateinit var songViewModel: SongViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity!!.setTitle(TAG)

        mSong = arguments!!.getParcelable<Parcelable>(MainActivity.KEY_SONGS) as Song

        val factory = InjectorUtils.provideViewModelFactory(myActivity)
        songViewModel = ViewModelProviders.of(myActivity, factory).get(SongViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //val viewRoot = inflater.inflate(R.layout.fragment_now_player, container, false)

        //binding= DataBindingUtil.inflate(inflater, R.layout.content_player, container, false)
        //binding = ContentPlayerBinding.inflate(inflater, container, false)
        binding = FragmentNowPlayerBinding.inflate(inflater, container, false)

        val toolbar: Toolbar = binding.toolbar
        toolbar.setTitle("Player");

        playerManager = (activity?.application as HHMusicApplication).getPlayerManager()
        if (mSong?.songId != playerManager?.getCurrentPlayedSong()?.value?.songId) {   // play another song
            //if (playerManager?.isPlaying!!) {
                releasePlayer()
                playerManager?.isPlaying = false
                playerManager?.initializePlayer()
            //}
        }

        mSong?.let {
            // bind song object onto UI
            binding.contentPlayer.songItem = mSong
        }

        //binding.contentPlayer.addToPlaylist.setOnClickListener(this)
        binding.contentPlayer.addPlayListOnClickListener = createAddPlayListListener()
        binding.contentPlayer.clickListener = createNowPlayingListListener()
        binding.contentPlayer.playerView.setControllerVisibilityListener(this)   // use the controller prev, next ...
        binding.contentPlayer.playerView.setControlDispatcher(PlayerControlDispatcher())  // detect the click event of exoplayer play/pause button
        binding.contentPlayer.playerView.setErrorMessageProvider(PlayerErrorMessageProvider())
        binding.contentPlayer.playerView.requestFocus()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()

            binding.contentPlayer.playerView?.let {
                binding.contentPlayer.playerView.onResume()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            if (binding.contentPlayer.playerView != null) {
                binding.contentPlayer.playerView.onPause()
            }
        }
    }

    private fun createAddPlayListListener() : View.OnClickListener {
        return View.OnClickListener {
            var fragment = AddPlayListFragment(activity as PlayerActivity, mSong!!)
            fragment.show(activity?.supportFragmentManager, "artist detail")
        }
    }
    private fun createNowPlayingListListener() : View.OnClickListener {
        return View.OnClickListener {
            var fragment = NowPlayingListFragment(activity as PlayerActivity, playerManager?.getSongList() )
            fragment.show(activity?.supportFragmentManager, "Now Playing list ")
        }
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

        binding.contentPlayer.playerView.setPlayer(playerManager?.getPlayer())
        binding.contentPlayer.playerView.setPlaybackPreparer(this)

        playerManager?.play()
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
            if(binding!=null && binding.contentPlayer!= null && playerManager!=null) {
                var song: Song? = playerManager?.getSongList()?.get(playerManager?.getPlayer()?.currentWindowIndex ?: 0)
                song?.let {
                    if (it?.songId == mSong?.songId) {
                        // update Most played Info
                        it.playedNumber += 1
                        // update Recently played Info
                        it.playedAt = Date(System.currentTimeMillis())
                        GlobalScope.launch {
                            songViewModel?.updateSong(it)
                        }
                    }

                    // update binding object bound to UI
                    mSong = it
                    binding.contentPlayer.songItem = mSong

                    // update LiveDate in PlayerManger so that MiniMusic bottom toolbar can observe
                    playerManager?.setCurrentPlayedSong(it)

                    // update current playing index, so when we go back by tapping on miniMusic bottom toolbar,
                    //  playerManager know where to seek back to
                    playerManager?.setCurrentSongIndex(playerManager?.getPlayer()?.currentWindowIndex!!)
                }
            }
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
        if (context != null)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}