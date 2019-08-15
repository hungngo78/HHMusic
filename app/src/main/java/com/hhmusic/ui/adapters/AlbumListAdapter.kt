package com.hhmusic.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hhmusic.HHMusicApplication
import com.hhmusic.data.entities.Album
import com.hhmusic.data.entities.Artist
//import com.hhmusic.data.model.Song
import com.hhmusic.data.entities.Song
import com.hhmusic.databinding.AlbumListItemBinding
import com.hhmusic.databinding.ArtistListItemBinding
import com.hhmusic.databinding.SongListItemBinding
import com.hhmusic.ui.activity.MainActivity

class AlbumListAdapter(private val myActivity: MainActivity): ListAdapter<Album, AlbumListAdapter.AlbumListViewHolder>(AlbumDiffCallback()) {

    lateinit var albumList: List<Album>;

    fun setAlbumList(list : ArrayList<Album>){
        albumList = ArrayList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumListViewHolder {

        return  AlbumListViewHolder(AlbumListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false ))

    }

    override fun onBindViewHolder(holder: AlbumListAdapter.AlbumListViewHolder, position: Int) {

        val album: Album = getItem(position)
        holder.apply {
            bind(createOnClickListener(album, position), album)
            itemView.tag = album
        }
    }


    private fun createOnClickListener(album: Album, position: Int): View.OnClickListener {
        return View.OnClickListener {
           // val bundle =  MainActivity.getIntent(it.context, ArrayList(songList), song.songId, position)
            //myActivity.openPlayerScreen(bundle)
            Toast.makeText(HHMusicApplication.applicationContext(), "Open SongList of  Artist", Toast.LENGTH_SHORT).show()
        }
    }

     class AlbumListViewHolder(private val binding: AlbumListItemBinding): RecyclerView.ViewHolder(binding.root) {

         fun bind(listener: View.OnClickListener, item: Album) {
             binding.apply {
                 clickListener = listener
                 albumItem = item
                 executePendingBindings()
             }
         }
     }


    private class AlbumDiffCallback : DiffUtil.ItemCallback<Album>() {

        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            //return oldItem.id == newItem.id
            return oldItem.albumId == newItem.albumId && oldItem.songId == newItem.songId
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.albumId == newItem.albumId &&
                    oldItem.albumName == newItem.albumName &&
                    oldItem.songId == newItem.songId &&
                    oldItem.numberOfTrack == newItem.numberOfTrack

        }
    }
}

