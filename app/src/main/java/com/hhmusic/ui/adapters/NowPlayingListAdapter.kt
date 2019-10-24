package com.hhmusic.ui.adapters

import android.net.Uri
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
import android.R
import android.content.ContentUris
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.model.stream.MediaStoreImageThumbLoader
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.content.ContentResolver
import android.media.MediaMetadataRetriever
import androidx.annotation.Nullable
import com.hhmusic.ui.activity.PlayerActivity


class NowPlayingListAdapter(private val myActivity: PlayerActivity): ListAdapter<Song, NowPlayingListAdapter.SongListViewHolder>(SongDiffCallback()) {

    lateinit var songList: List<Song>;

    fun setSongList(list : ArrayList<Song>){
        songList = ArrayList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongListViewHolder {

        return  SongListViewHolder(SongListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false ))

    }

    override fun onBindViewHolder(holder: NowPlayingListAdapter.SongListViewHolder, position: Int) {

        val song: Song = getItem(position)

        val metaRetriver = MediaMetadataRetriever()
        metaRetriver.setDataSource(myActivity, Uri.parse(song.uriStr))
        val picArray = metaRetriver.embeddedPicture

        var songImage : Bitmap? = null;
        if (picArray!= null) {
            songImage = BitmapFactory.decodeByteArray(picArray, 0, picArray.size)
        }
        holder.apply {
            bind(createOnClickListener(song, position), song, songImage)
            itemView.tag = song
        }
    }


    private fun createOnClickListener(song: Song, position: Int): View.OnClickListener {
        return View.OnClickListener {
            var playerManager = (myActivity.application as HHMusicApplication).getPlayerManager()
           // playerManager?.setSongList(ArrayList(songList), position)
            if(position != playerManager?.getPlayer()?.currentAdGroupIndex) {
                playerManager?.setCurrentSong(position)
                playerManager?.stop()
                playerManager?.play()
            }

            Toast.makeText(HHMusicApplication.applicationContext(), "Play song", Toast.LENGTH_SHORT).show()
        }
    }

     class SongListViewHolder(private val binding: SongListItemBinding): RecyclerView.ViewHolder(binding.root) {

         fun bind(listener: View.OnClickListener, item: Song, artwork: Bitmap?) {
       // fun bind(listener: View.OnClickListener, item: Song, artwork: Bitmap?) {
             binding.apply {
                 clickListener = listener
                 songItem = item
                 if(artwork!= null)
                     binding.imageAlbum.setImageBitmap(artwork)
                 else {
                     binding.imageAlbum.setImageResource(com.hhmusic.R.drawable.ic_tab_2)
                 }
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

