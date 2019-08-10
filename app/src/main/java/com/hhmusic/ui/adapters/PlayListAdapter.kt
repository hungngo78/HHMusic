package com.hhmusic.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hhmusic.HHMusicApplication
import com.hhmusic.data.entities.PlayList
import com.hhmusic.databinding.PlayListItemBinding


class PlayListAdapter : ListAdapter<PlayList, PlayListAdapter.PlayListViewHolder>(PlayListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
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
            //val direction = PlantListFragmentDirections.ActionPlantListFragmentToPlantDetailFragment(plantId)
            //it.findNavController().navigate(direction)
            Toast.makeText(HHMusicApplication.applicationContext(), "Play song", Toast.LENGTH_SHORT).show()
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