package com.hhmusic.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
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

import android.view.View.OnClickListener

import kotlinx.android.synthetic.main.activity_player.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.google.android.exoplayer2.upstream.FileDataSourceFactory
import com.hhmusic.HHMusicApplication
import com.hhmusic.R
import com.hhmusic.ui.fragment.NowPlayerFragment
import com.hhmusic.utilities.PlayerManager


class PlayerActivity : AppCompatActivity() {

    companion object {
        val ACTION_VIEW = "com.hhmusic.android.action.VIEW"
        //val ACTION_VIEW_LIST = "com.hhmusic.android.action.VIEW_LIST"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        // set background color
        //setTheme(com.hhmusic.R.style.PlayerTheme_Spherical)

        super.onCreate(savedInstanceState)
        setContentView(com.hhmusic.R.layout.activity_player)

        // receiving our song from Main Activity
        val action = intent.action
        if (ACTION_VIEW == action) {
            val song: Song? = intent.getParcelableExtra<Parcelable>(MainActivity.KEY_SONGS) as Song

            var fragment1 = NowPlayerFragment()
            val bundle1 = Bundle()
            bundle1.putParcelable(MainActivity.KEY_SONGS, song)
            fragment1.setArguments(bundle1)

            supportFragmentManager.beginTransaction()
                .replace(com.hhmusic.R.id.fragment_container, fragment1, NowPlayerFragment.TAG).commit()
        }
    }
}
