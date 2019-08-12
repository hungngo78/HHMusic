package com.hhmusic.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hhmusic.databinding.FragmentSongListBinding
import com.hhmusic.ui.activity.MainActivity
import com.hhmusic.ui.adapters.SongListAdapter
import com.hhmusic.utilities.InjectorUtils
import com.hhmusic.viewmodels.PageViewModel
import com.hhmusic.viewmodels.PlayListViewModel
import com.hhmusic.viewmodels.SongViewModel

class SongsFragment(private val myActivity: MainActivity): Fragment() {


    private lateinit var viewModel: SongViewModel
    lateinit var binding: FragmentSongListBinding
    lateinit var adapter: SongListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //viewModel = ViewModelProviders.of(this).get(SongViewModel::class.java).apply {

        //}

        val context = context ?: return binding.root

        val factory = InjectorUtils.provideSongViewModelFactory(context)
        viewModel = ViewModelProviders.of(this, factory).get(SongViewModel::class.java)

        binding = FragmentSongListBinding.inflate(inflater,container, false)
//        val context = context ?: return binding.root


        adapter = SongListAdapter(myActivity)
        binding.songListview.adapter = adapter
        subscribeUi()
        viewModel.getSongList()
        return binding.root


    }

    private fun subscribeUi() {
        viewModel.getObserverSongList().observe(viewLifecycleOwner, Observer {

            songs -> if(songs != null) {
            adapter.submitList(songs)
            adapter.setSongList(ArrayList(songs))
            //binding.adapter = adapter
            binding.songListview.adapter = adapter
            }
        })
    }
}
