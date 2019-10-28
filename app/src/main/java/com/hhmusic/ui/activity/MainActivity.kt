package com.hhmusic.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.accounts.Account
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Layout
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.hhmusic.HHMusicApplication
import com.hhmusic.R
import com.hhmusic.ui.activity.ui.main.SectionsPagerAdapter

import com.hhmusic.data.entities.Song
import com.hhmusic.utilities.PlayerManager
import com.hhmusic.utilities.SyncUtils


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var tabs: TabLayout
    private lateinit var mAccount: Account
    private lateinit var miniMusicView: View
    private lateinit var miniPlayPauseBtn: ImageButton

    private var playerManager : PlayerManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        checkPermission()

        /*
         * Create the dummy account. The code for CreateSyncAccount
         * is listed in the lesson Creating a Sync Adapter
         */
        mAccount = SyncUtils.CreateSyncAccount(applicationContext)
        SyncUtils.TriggerRefresh(mAccount)

        // create instance of PlayerManager to play music
        playerManager = PlayerManager.getInstance(applicationContext, null)
        (application as HHMusicApplication).setPlayerManager(playerManager)

        miniMusicView = findViewById(R.id.layout_mini_player)
        miniPlayPauseBtn = miniMusicView?.findViewById<View>(R.id.play_pause) as ImageButton
    }



    val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123

    fun checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val hasWritePermission: Int = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            val hasReadPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            var permissions: MutableList<String> = ArrayList<String>();
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
//              preferencesUtility.setString("storage", "true");
            }

            if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

            } else {
//              preferencesUtility.setString("storage", "true");
            }

            if (!permissions.isEmpty()) {
              //requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
                requestPermissions(permissions.toTypedArray(), MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//                ActivityCompat.requestPermissions(
//                    this, arrayOf(
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE
//                    ),
//                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
//                );
            } else {
                initTabLayout();
            }
        }
    }

    // Receive the permissions request result
    override  fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                                grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE ->{
                val isPermissionsGranted = processPermissionsResult(requestCode,permissions,grantResults)

                if(isPermissionsGranted){
                    // Do the task now
                    toast("Permissions granted.")
                    initTabLayout();

                }else{
                    toast("Permissions denied.")
                }
                return
            }
        }
    }

    // Process permissions result
    fun processPermissionsResult(requestCode: Int, permissions: Array<String>,
                                 grantResults: IntArray): Boolean {
        var result = 0
        if (grantResults.isNotEmpty()) {
            for (item in grantResults) {
                result += item
            }
        }
        if (result == PackageManager.PERMISSION_GRANTED) return true
        return false
    }

    // Extension function to show toast message
    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun initTabLayout(){
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(com.hhmusic.R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        tabs = findViewById(com.hhmusic.R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        setupTabicons()
    }

    fun setupTabicons() {
        tabs.getTabAt(0)?.setIcon(R.drawable.ic_tab_1)
        tabs.getTabAt(1)?.setIcon(R.drawable.ic_tab_2)
        tabs.getTabAt(2)?.setIcon(R.drawable.ic_tab_3)
        tabs.getTabAt(3)?.setIcon(R.drawable.ic_tab_4)
        tabs.getTabAt(4)?.setIcon(R.drawable.ic_tab_5)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // If the user clicks the "Setting" button.
            R.id.action_settings -> {
                //SyncUtils.TriggerRefresh(mAccount)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_google_drive-> {

            }
            R.id.nav_scan_media -> {

            }
            R.id.nav_about -> {

            }

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun openPlayerScreen(intent: Intent) {
        intent?.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (playerManager?.isPlaying!!) {
            miniPlayPauseBtn.setImageResource(android.R.drawable.ic_media_pause)
        } else {
            miniPlayPauseBtn.setImageResource(android.R.drawable.ic_media_play)
        }
    }

    fun setupMiniMusic(song: Song) {
        /* mini music player */
        //val miniMusicView : View = findViewById(R.id.layout_mini_player)
        miniMusicView?.let { miniMusic ->
            miniMusicView.visibility = View.VISIBLE

            miniMusicView?.setOnClickListener(View.OnClickListener {
                val intent =  getIntent(it.context, PlayerActivity.ACTION_PLAY_FROM_MINI_MUSIC)
                openPlayerScreen(intent)
            })

            val miniTitle: TextView = miniMusic.findViewById<View>(R.id.song_title) as TextView
            miniTitle?.text = song.title

            val miniArtist: TextView = miniMusic.findViewById<View>(R.id.artist) as TextView
            miniArtist?.text = song.artistName

            miniPlayPauseBtn.setOnClickListener(View.OnClickListener {
                var btn: ImageButton = it as ImageButton

                if (playerManager?.isPlaying!!) {
                    playerManager?.pause()

                    btn.setImageResource(android.R.drawable.ic_media_play)
                } else {
                    playerManager?.resume()

                    btn.setImageResource(android.R.drawable.ic_media_pause)

                }
            })

            // observe the track change in PlayerManager
            playerManager?.getCurrentPlayedSong()?.observe(this, Observer {
                miniTitle?.text = it.title
                miniArtist?.text = it.artistName
            })

            // observe the playing status (onPlaying or stopped) of player in PlayerManager
            playerManager?.getCurrentPlayedStatus()?.observe(this, Observer {
                if (!it) {
                    miniPlayPauseBtn.setImageResource(android.R.drawable.ic_media_play)
                } else {
                    miniPlayPauseBtn.setImageResource(android.R.drawable.ic_media_pause)
                }
            })
        }
    }

    companion object {

        const val KEY_SONGS = "song"

        fun getIntent(context: Context, action: String, song: Song): Intent {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(KEY_SONGS, song)
            intent.setAction(action);

            return intent
        }

        fun getIntent(context: Context, action: String): Intent {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.setAction(action);

            return intent
        }
    }

}
