package com.hhmusic.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hhmusic.HHMusicApplication
//import com.hhmusic.data.model.Song
import com.hhmusic.data.entities.Song
import com.hhmusic.databinding.SongListItemBinding
import com.hhmusic.ui.activity.MainActivity

class SongListAdapter(private val myActivity: MainActivity): ListAdapter<Song, SongListAdapter.SongListViewHolder>(SongDiffCallback()) {

    lateinit var songList: List<Song>;

    fun setSongList(list : ArrayList<Song>){
        songList = ArrayList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongListViewHolder {

        return  SongListViewHolder(SongListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false ))

    }

    override fun onBindViewHolder(holder: SongListAdapter.SongListViewHolder, position: Int) {

        val song: Song = getItem(position)
        holder.apply {
            bind(createOnClickListener(song, position), song)
            itemView.tag = song
        }
    }


    private fun createOnClickListener(song: Song, position: Int): View.OnClickListener {
        return View.OnClickListener {
            var playerManager = (myActivity.application as HHMusicApplication).getPlayerManager()
            playerManager?.setSongList(ArrayList(songList))

            //val bundle =  MainActivity.getIntent(it.context, ArrayList(songList), song.songId, position)
            val bundle =  MainActivity.getIntent(it.context, song)
            myActivity.openPlayerScreen(bundle)

            // setup mini music
            myActivity.setupMiniMusic(song)

            Toast.makeText(HHMusicApplication.applicationContext(), "Play song", Toast.LENGTH_SHORT).show()
        }
    }

     class SongListViewHolder(private val binding: SongListItemBinding): RecyclerView.ViewHolder(binding.root) {

         fun bind(listener: View.OnClickListener, item: Song) {
             binding.apply {
                 clickListener = listener
                 songItem = item
                 executePendingBindings()
             }
         }
     }


    private class SongDiffCallback : DiffUtil.ItemCallback<Song>() {

        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            //return oldItem.id == newItem.id
            return oldItem.songId == newItem.songId
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.albumName == newItem.albumName &&
                    oldItem.artistName == newItem.artistName &&
                    oldItem.title == newItem.title &&
                    oldItem.duration == newItem.duration &&
                    oldItem.uriStr == newItem.uriStr &&
                    oldItem.imagePathStr == newItem.imagePathStr
        }
    }
}

