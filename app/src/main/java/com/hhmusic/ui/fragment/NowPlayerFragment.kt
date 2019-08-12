package com.hhmusic.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.hhmusic.R
import com.hhmusic.data.entities.Song
import com.hhmusic.databinding.FragmentNowPlayerBinding
import com.hhmusic.ui.activity.MainActivity

class NowPlayerFragment (private val context: MainActivity): Fragment() {

    companion object {
        fun newInstance(bundle: Bundle, context: MainActivity): NowPlayerFragment {
            val fragment = NowPlayerFragment(context)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)

        //val binding = FragmentNowPlayerBinding.inflate(inflater, container, false)

        return inflater.inflate(R.layout.fragment_now_player, container, false)
       // return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val songList : List<Song> = arguments!!.getParcelableArrayList<Song>(MainActivity.KEY_SONGS)
        val songId = arguments!!.getLong(MainActivity.KEY_SONG_ID)
        val songPosition: Int = arguments!!.getInt(MainActivity.KEY_SONG_POSITION)



    }

}