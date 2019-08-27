package com.hhmusic.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hhmusic.HHMusicApplication
import com.hhmusic.data.entities.PlayList
import com.hhmusic.data.entities.Song
import com.hhmusic.databinding.PlayListItemBinding
import com.hhmusic.ui.activity.MainActivity
import com.hhmusic.ui.fragment.PlayListDetailFragment
import com.hhmusic.utilities.InjectorUtils
import com.hhmusic.viewmodels.PlayListViewModel


class PlayListAdapter (private val myActivity: MainActivity) : ListAdapter<PlayList, PlayListAdapter.PlayListViewHolder>(PlayListDiffCallback()) {

    lateinit  var viewModel: PlayListViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
        val factory = InjectorUtils.provideViewModelFactory(myActivity)
        viewModel = ViewModelProviders.of(myActivity, factory).get(PlayListViewModel::class.java)

        return PlayListViewHolder(PlayListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false ))
    }

    override fun onBindViewHolder(holder: PlayListAdapter.PlayListViewHolder, position: Int) {

        val playList: PlayList = getItem(position)
        holder.apply {
            bind(createOnClickListener(playList), playList)
            itemView.tag = playList
        }
    }

    private fun createOnClickListener(playList: PlayList): View.OnClickListener {
        return View.OnClickListener {
            //Toast.makeText(HHMusicApplication.applicationContext(), "Play song", Toast.LENGTH_SHORT).show()
            viewModel.getSongsByPlayList(playList.playListId).observe(myActivity,  Observer {
                it?.let {
                    if (it.size > 0) {
                        var playListDetailFragment = PlayListDetailFragment(myActivity, ArrayList(it))
                        playListDetailFragment.show(myActivity.supportFragmentManager, "artist detail")
                    } else
                        Toast.makeText(HHMusicApplication.applicationContext(), "Playlist has 0 song", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    class PlayListViewHolder(private val binding: PlayListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: PlayList) {
            binding.apply {
                clickListener = listener
                playList = item

                executePendingBindings()
            }
        }
    }


    private class PlayListDiffCallback : DiffUtil.ItemCallback<PlayList>() {

        override fun areItemsTheSame(oldItem: PlayList, newItem: PlayList): Boolean {
            return oldItem.playListId == newItem.playListId
        }

        override fun areContentsTheSame(oldItem: PlayList, newItem: PlayList): Boolean {
            return oldItem.name == newItem.name &&
                    (oldItem.default && newItem.default)
        }
    }
}