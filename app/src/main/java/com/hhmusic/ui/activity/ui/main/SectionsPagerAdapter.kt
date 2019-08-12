package com.hhmusic.ui.activity.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hhmusic.R
import com.hhmusic.ui.activity.MainActivity
import com.hhmusic.ui.fragment.PlaceholderFragment
import com.hhmusic.ui.fragment.PlayListsFragment
import com.hhmusic.ui.fragment.SongsFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_text_playlist,
    R.string.tab_text_song,
    R.string.tab_text_artist,
    R.string.tab_text_album,
    R.string.tab_text_folder
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: MainActivity, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        when (position) {
            0 -> return PlayListsFragment()
            1 -> return SongsFragment(context)
            else -> return PlaceholderFragment.newInstance(position + 1)
        }
        /*
        if (position == 1) {
            return SongsFragment(context)
        } else
            return PlaceholderFragment.newInstance(position + 1)*/
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 5 total pages.
        return 5
    }
}