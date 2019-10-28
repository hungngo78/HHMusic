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
import com.hhmusic.data.entities.Song
import com.hhmusic.databinding.SongListItemBinding
import com.hhmusic.ui.activity.MainActivity
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.widget.PopupMenu;
import android.R
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hhmusic.ui.activity.PlayerActivity
import com.hhmusic.ui.fragment.AddPlayListFragment
import com.hhmusic.ui.fragment.AlbumDetailFragment
import com.hhmusic.ui.fragment.ArtistDetailFragment
import com.hhmusic.utilities.InjectorUtils
import com.hhmusic.utilities.MediaStoreUtils
import com.hhmusic.viewmodels.AlbumViewModel
import com.hhmusic.viewmodels.ArtistViewModel
import com.hhmusic.viewmodels.SongViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class SongListAdapter(private val myActivity: MainActivity):
                                        ListAdapter<Song, SongListAdapter.SongListViewHolder>(SongDiffCallback()) {

    lateinit var songList: List<Song>

    private var albumViewModel: AlbumViewModel
    private var artistViewModel: ArtistViewModel
    private var songViewModel: SongViewModel

    init {
        val factory = InjectorUtils.provideViewModelFactory(myActivity)

        albumViewModel = ViewModelProviders.of(myActivity, factory).get(AlbumViewModel::class.java)
        artistViewModel = ViewModelProviders.of(myActivity, factory).get(ArtistViewModel::class.java)
        songViewModel = ViewModelProviders.of(myActivity, factory).get(SongViewModel::class.java)
    }

    fun setSongList(list : ArrayList<Song>){
        songList = ArrayList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongListViewHolder {
        return  SongListViewHolder(SongListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false ))
    }

    override fun onBindViewHolder(holder: SongListAdapter.SongListViewHolder, position: Int) {
        val song: Song = getItem(position)

//        var uri = Uri.parse("content://media/external/audio/media/" + song.songId + "/albumart")
//        System.out.println("url = " + song.imagePathStr)
//        System.out.println("uri = " +uri.path)
//
//        // Way 1: load cover art -> ok
        song.imagePathStr = "content://media/external/audio/media/" + song.songId + "/albumart"

        val res = myActivity.getContentResolver()
        val inputStream = res.openInputStream(Uri.parse(song.imagePathStr))
        val songImage = BitmapFactory.decodeStream(inputStream)

        /*
        // Way 2: load cover art -> ok
//        val metaRetriver = MediaMetadataRetriever()
//
//        metaRetriver.setDataSource(myActivity, Uri.parse(song.uriStr))
//        //System.out.println(song.uriStr)
//        val picArray = metaRetriver.embeddedPicture
//
//        var songImage : Bitmap? = null;
//        if (picArray!= null) {
//            songImage = BitmapFactory.decodeByteArray(picArray, 0, picArray.size)
//        }*/

        holder.apply {
           // bind(createOnClickListener(song, position), song, songImage)
            bind(createOnClickListener(song, position),
                createPopupMenuBtnOnClickListener(song),
                song, songImage)
            itemView.tag = song
        }
    }

    private fun createPopupMenuBtnOnClickListener(song: Song): View.OnClickListener {
        return View.OnClickListener {
            val popup = PopupMenu(myActivity, it)

            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    val i = item.itemId
                    return if (i == com.hhmusic.R.id.add_to_playlist) {
                        var fragment = AddPlayListFragment(myActivity , song!!)
                        fragment.show(myActivity?.supportFragmentManager, "artist detail")

                        true
                    } else if (i == com.hhmusic.R.id.go_to_album) {
                        // get all songs of this album, and open Album Detail Fragment
                        albumViewModel.getObserverSongListFromAlbum(song.albumId).observe(myActivity, Observer {
                            var albumDetailFragment = AlbumDetailFragment(myActivity, ArrayList(it))

                            // myActivity.supportFragmentManager.beginTransaction().addToBackStack("artist detail").replace()
                            albumDetailFragment.show(myActivity.supportFragmentManager, "album detail")
                        })

                        true
                    } else if (i == com.hhmusic.R.id.go_to_artist) {
                        // get all songs of this artist, and open Album Detail Fragment
                        artistViewModel.getSongListFromArtist(song.artistId).observe(myActivity, Observer {
                            var artistDetailFragment = ArtistDetailFragment(myActivity, ArrayList(it))

                            // myActivity.supportFragmentManager.beginTransaction().addToBackStack("artist detail").replace()
                            artistDetailFragment.show(myActivity.supportFragmentManager, "artist detail")
                        })

                        true
                    } else if (i == com.hhmusic.R.id.delete) {
                        GlobalScope.launch () {
                            // update DB
                            var result = songViewModel.removeSong(song.songId)

                            withContext(Dispatchers.Main) {
                                if (result > 0) {
                                    // no need to callback to fragment for updating UI
                                    //  because the songlist in DB has been already observed by fragment

                                    // delete song on sd card
                                    MediaStoreUtils.deleteFromMediaStore(song.uriStr, myActivity)
                                }
                            }
                        }

                        true
                    } else {
                        onMenuItemClick(item)
                    }
                }
            })

            popup.inflate(com.hhmusic.R.menu.song_list_popup)
            popup.show()
        }
    }

    private fun createOnClickListener(song: Song, position: Int): View.OnClickListener {
        return View.OnClickListener {
            var playerManager = (myActivity.application as HHMusicApplication).getPlayerManager()
            playerManager?.removeMediaSource()
            playerManager?.setSongList(ArrayList(songList), position)

            val intent =  MainActivity.getIntent(it.context, PlayerActivity.ACTION_PLAY_FROM_SONG_LIST, song)
            myActivity.openPlayerScreen(intent)

            // setup mini music
            myActivity.setupMiniMusic(song)

            Toast.makeText(HHMusicApplication.applicationContext(), "Play song", Toast.LENGTH_SHORT).show()
        }
    }

    class SongListViewHolder(private val binding: SongListItemBinding): RecyclerView.ViewHolder(binding.root) {

         fun bind(itemOnClickListener: View.OnClickListener,
                  popupMenuBtnOnClickListener: View.OnClickListener,
                  item: Song, artwork: Bitmap?) {
             binding.apply {
                 clickListener = itemOnClickListener
                 popupBtnOnClickListener = popupMenuBtnOnClickListener
                 songItem = item
               //  binding.imageAlbum.setImageURI(Uri.parse(item.imagePathStr))
                 if(artwork!= null)
                     binding.imageAlbum.setImageBitmap(artwork)
                 else {
                     binding.imageAlbum.setImageBitmap(null)
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

