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
import com.hhmusic.ui.activity.PlayerActivity
import com.hhmusic.ui.adapters.NowPlayingListAdapter
import com.hhmusic.ui.adapters.SongListAdapter



class NowPlayingListFragment(private val myActivity: PlayerActivity, private val songList: ArrayList<Song>?): DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        var view = inflater.inflate(R.layout.dialog_fragment_detail, container, false)

       var toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Playing Song list ")
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            view -> dismiss()
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var adapter =  NowPlayingListAdapter(myActivity)
        adapter.setSongList(songList!!)
        adapter.submitList(ArrayList(songList))
        var recyclerView = view.findViewById<RecyclerView>(R.id.songListview)
        recyclerView.adapter = adapter

        recyclerView.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {
               dismiss()
            }

        })
        adapter.notifyDataSetChanged()

    }

}