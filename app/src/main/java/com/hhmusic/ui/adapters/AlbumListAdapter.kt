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
import com.hhmusic.R
import com.hhmusic.data.entities.Album
import com.hhmusic.databinding.AlbumListItemBinding
import com.hhmusic.ui.activity.MainActivity
import com.hhmusic.ui.fragment.AlbumDetailFragment
import com.hhmusic.utilities.InjectorUtils
import com.hhmusic.viewmodels.AlbumViewModel

class AlbumListAdapter(private val myActivity: MainActivity): ListAdapter<Album, AlbumListAdapter.AlbumListViewHolder>(AlbumDiffCallback()) {

    lateinit var albumList: List<Album>;
    lateinit  var viewModel: AlbumViewModel

    init {
        val factory = InjectorUtils.provideAlbumViewModelFactory(myActivity)
        viewModel = ViewModelProviders.of(myActivity, factory).get(AlbumViewModel::class.java)


    }
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
            //get song list on Artist
            viewModel.getObserverSongListFromAlbum(album.albumId).observe(myActivity, Observer{
                    songList -> songList?.let {

                var artistDetailFragment = AlbumDetailFragment(myActivity, ArrayList(it))
                // myActivity.supportFragmentManager.beginTransaction().addToBackStack("artist detail").replace()
                artistDetailFragment.show(myActivity.supportFragmentManager, "artist detail")

                Toast.makeText(HHMusicApplication.applicationContext(), "Open SongList of  ALbum", Toast.LENGTH_SHORT).show()
            }
            })
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

