package com.hhmusic.ui.activity

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity;
import com.hhmusic.HHMusicApplication
import com.hhmusic.data.entities.Song

import com.hhmusic.ui.fragment.NowPlayingFragment
import com.hhmusic.utilities.PlayerManager


class PlayerActivity : AppCompatActivity() {

    companion object {
        val ACTION_PLAY_FROM_SONG_LIST = "com.hhmusic.android.action.PLAY_FROM_SONG_LIST"
        val ACTION_PLAY_FROM_MINI_MUSIC = "com.hhmusic.android.action.PLAY_FROM_MINI_MUSIC"
    }

    private var playerManager : PlayerManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // set background color
        setTheme(com.hhmusic.R.style.PlayerTheme_Spherical)

        super.onCreate(savedInstanceState)
        setContentView(com.hhmusic.R.layout.activity_player)

        // receiving our song from Main Activity
        val action = intent.action
        var song: Song? = null
        if (ACTION_PLAY_FROM_SONG_LIST == action) {
            song = intent.getParcelableExtra<Parcelable>(MainActivity.KEY_SONGS) as Song
        } else if (ACTION_PLAY_FROM_MINI_MUSIC == action) {
            playerManager = (this?.application as HHMusicApplication).getPlayerManager()
            song = playerManager?.getCurrentPlayedSong()?.value
        }

        var fragment1 = NowPlayingFragment(this)
        val bundle1 = Bundle()
        bundle1.putParcelable(MainActivity.KEY_SONGS, song)
        fragment1.setArguments(bundle1)

        supportFragmentManager.beginTransaction()
            .replace(com.hhmusic.R.id.fragment_container, fragment1, NowPlayingFragment.TAG).commit()
    }
}
