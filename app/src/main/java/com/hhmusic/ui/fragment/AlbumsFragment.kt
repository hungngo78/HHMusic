package com.hhmusic.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.hhmusic.databinding.FragmentAlbumListBinding
import com.hhmusic.databinding.FragmentArtistListBinding
import com.hhmusic.databinding.FragmentSongListBinding
import com.hhmusic.ui.activity.MainActivity
import com.hhmusic.ui.adapters.AlbumListAdapter
import com.hhmusic.ui.adapters.ArtistListAdapter
import com.hhmusic.ui.adapters.SongListAdapter
import com.hhmusic.utilities.InjectorUtils
import com.hhmusic.viewmodels.*

class AlbumsFragment(private val myActivity: MainActivity): Fragment() {


    private lateinit var viewModel: AlbumViewModel
    lateinit var binding: FragmentAlbumListBinding
    lateinit var adapter: AlbumListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val context = context ?: return binding.root

        val factory = InjectorUtils.provideViewModelFactory(context)
        viewModel = ViewModelProviders.of(this, factory).get(AlbumViewModel::class.java)

        binding = FragmentAlbumListBinding.inflate(inflater,container, false)
//        val context = context ?: return binding.root


        adapter = AlbumListAdapter(myActivity)
        binding.albumListview.adapter = adapter
        subscribeUi()
        return binding.root


    }

    private fun subscribeUi() {
        viewModel.getObserverAlbumList().observe(viewLifecycleOwner, Observer {

            albums -> if(albums != null) {
            adapter.submitList(albums)
            adapter.setAlbumList(ArrayList(albums))
            //binding.adapter = adapter
            binding.albumListview.adapter = adapter
            }
        })
    }
}
