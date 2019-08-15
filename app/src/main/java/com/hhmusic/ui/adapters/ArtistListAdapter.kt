package com.hhmusic.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hhmusic.HHMusicApplication
import com.hhmusic.data.entities.Artist
//import com.hhmusic.data.model.Song
import com.hhmusic.data.entities.Song
import com.hhmusic.databinding.ArtistListItemBinding
import com.hhmusic.databinding.SongListItemBinding
import com.hhmusic.ui.activity.MainActivity

class ArtistListAdapter(private val myActivity: MainActivity): ListAdapter<Artist, ArtistListAdapter.ArtistListViewHolder>(ArtistDiffCallback()) {

    lateinit var artistList: List<Artist>;

    fun setArtistList(list : ArrayList<Artist>){
        artistList = ArrayList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistListViewHolder {

        return  ArtistListViewHolder(ArtistListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false ))

    }

    override fun onBindViewHolder(holder: ArtistListAdapter.ArtistListViewHolder, position: Int) {

        val artist: Artist = getItem(position)
        holder.apply {
            bind(createOnClickListener(artist, position), artist)
            itemView.tag = artist
        }
    }


    private fun createOnClickListener(artist: Artist, position: Int): View.OnClickListener {
        return View.OnClickListener {
           // val bundle =  MainActivity.getIntent(it.context, ArrayList(songList), song.songId, position)
            //myActivity.openPlayerScreen(bundle)
            Toast.makeText(HHMusicApplication.applicationContext(), "Open SongList of  Artist", Toast.LENGTH_SHORT).show()
        }
    }

     class ArtistListViewHolder(private val binding: ArtistListItemBinding): RecyclerView.ViewHolder(binding.root) {

         fun bind(listener: View.OnClickListener, item: Artist) {
             binding.apply {
                 clickListener = listener
                 artistItem = item
                 executePendingBindings()
             }
         }
     }


    private class ArtistDiffCallback : DiffUtil.ItemCallback<Artist>() {

        override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            //return oldItem.id == newItem.id
            return oldItem.artistId == newItem.artistId && oldItem.songId == newItem.songId
        }

        override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem.artistId == newItem.artistId &&
                    oldItem.artistName == newItem.artistName &&
                    oldItem.songId == newItem.songId &&
                    oldItem.numberOfTrack == newItem.numberOfTrack

        }
    }
}

