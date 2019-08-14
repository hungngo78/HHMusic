package com.hhmusic.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hhmusic.databinding.FragmentPlayListBinding
import com.hhmusic.ui.adapters.PlayListAdapter
import com.hhmusic.utilities.InjectorUtils
import com.hhmusic.viewmodels.PlayListViewModel


class PlayListsFragment : Fragment() {

    private lateinit var viewModel: PlayListViewModel
    lateinit var binding: FragmentPlayListBinding
    lateinit var mAdapter: PlayListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPlayListBinding.inflate(inflater,container, false)
        val context = context ?: return binding.root

        val factory = InjectorUtils.provideViewModelFactory(context)
        viewModel = ViewModelProviders.of(this, factory).get(PlayListViewModel::class.java)

        mAdapter = PlayListAdapter()
        binding.playListsView.adapter = mAdapter

        subscribeUi()

        return binding.root
    }

    private fun subscribeUi() {
        viewModel.getPlants().observe(viewLifecycleOwner, Observer {playLists ->
            playLists?. let {
                mAdapter.submitList(playLists)
            }
        })
    }
}