package com.hhmusic.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hhmusic.R
import com.hhmusic.data.entities.Song
import com.hhmusic.ui.activity.MainActivity
import com.hhmusic.ui.adapters.SongListAdapter



class ArtistDetailFragment(private val myActivity: MainActivity, private val songList: ArrayList<Song>): DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        var view = inflater.inflate(R.layout.fragment_song_list, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var adapter =  SongListAdapter(myActivity)
        adapter.setSongList(songList)
        adapter.submitList(songList)
        var recyclerView = view.findViewById<RecyclerView>(R.id.songListview)
        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()

    }

}