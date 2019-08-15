package com.hhmusic.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hhmusic.databinding.FragmentArtistListBinding
import com.hhmusic.databinding.FragmentSongListBinding
import com.hhmusic.ui.activity.MainActivity
import com.hhmusic.ui.adapters.ArtistListAdapter
import com.hhmusic.ui.adapters.SongListAdapter
import com.hhmusic.utilities.InjectorUtils
import com.hhmusic.viewmodels.ArtistViewModel
import com.hhmusic.viewmodels.PageViewModel
import com.hhmusic.viewmodels.PlayListViewModel
import com.hhmusic.viewmodels.SongViewModel

class ArtistsFragment(private val myActivity: MainActivity): Fragment() {


    private lateinit var viewModel: ArtistViewModel
    lateinit var binding: FragmentArtistListBinding
    lateinit var adapter: ArtistListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val context = context ?: return binding.root

        val factory = InjectorUtils.provideArtistViewModelFactory(context)
        viewModel = ViewModelProviders.of(this, factory).get(ArtistViewModel::class.java)

        binding = FragmentArtistListBinding.inflate(inflater,container, false)
//        val context = context ?: return binding.root


        adapter = ArtistListAdapter(myActivity)
        binding.artistListview.adapter = adapter
        subscribeUi()
        return binding.root


    }

    private fun subscribeUi() {
        viewModel.getObserverArtistList().observe(viewLifecycleOwner, Observer {

            artists -> if(artists != null) {
            adapter.submitList(artists)
            adapter.setArtistList(ArrayList(artists))
            //binding.adapter = adapter
            binding.artistListview.adapter = adapter
            }
        })
    }
}
