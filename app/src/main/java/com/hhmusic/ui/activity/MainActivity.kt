package com.hhmusic.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.accounts.Account
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.hhmusic.HHMusicApplication
import com.hhmusic.R
import com.hhmusic.ui.activity.ui.main.SectionsPagerAdapter

import com.hhmusic.data.entities.Song
import com.hhmusic.ui.fragment.NowPlayerFragment
import com.hhmusic.utilities.PlayerManager
import com.hhmusic.utilities.SyncUtils


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var tabs: TabLayout
    private lateinit var mAccount: Account

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

        var playerManager = PlayerManager.getInstance(applicationContext, null)
        (application as HHMusicApplication).setPlayerManager(playerManager)
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
                SyncUtils.TriggerRefresh(mAccount)
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
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

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

    companion object {

        const val KEY_SONGS = "song"
        const val KEY_SONG_ID ="songId"
        const val KEY_SONG_POSITION ="songPosition"

        fun getBundle(context: Context, song: ArrayList<Song>, songId: Long, position: Int): Bundle {
            //val intent = Intent(context, PlayerActivity::class.java)
            val bundle = Bundle().apply {
                putParcelableArrayList(KEY_SONGS, song)
                putLong(KEY_SONG_ID, songId)
                putInt(KEY_SONG_POSITION, position)
            }

            return bundle
        }


        //fun getIntent(context: Context, songList: ArrayList<Song>, songId: Long, position: Int): Intent {
        fun getIntent(context: Context, song: Song): Intent {
            val intent = Intent(context, PlayerActivity::class.java)
            //intent.putParcelableArrayListExtra(KEY_SONGS, songList)
            intent.putExtra(KEY_SONGS, song)
            //intent.putExtra(KEY_SONG_ID, songId)
            intent.setAction(PlayerActivity.ACTION_VIEW);

            //intent.putExtra(KEY_SONG_POSITION, position)
            return intent
        }
    }

}
