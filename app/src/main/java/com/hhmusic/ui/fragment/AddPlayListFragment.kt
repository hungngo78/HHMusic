package com.hhmusic.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.hhmusic.R
import com.hhmusic.ui.activity.MainActivity
import com.hhmusic.ui.activity.PlayerActivity
import com.hhmusic.ui.adapters.SongListAdapter
import com.hhmusic.utilities.InjectorUtils
import com.hhmusic.viewmodels.PlayListViewModel

class AddPlayListFragment (private val myActivity: PlayerActivity) : DialogFragment() {

    lateinit  var viewModel: PlayListViewModel
    private var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var viewRoot = inflater.inflate(R.layout.dialog_fragment_add_playlist, container, false)

        var toolbar = viewRoot.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle("Add to PlayList")
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
                view -> dismiss()
        })

        var values : List<String> = listOf()
        values += "Create New PlayList"
        values += "PlayList 1"
        values += "PlayList 2"

        var adapter : ArrayAdapter<String> = ArrayAdapter<String>(
                        activity!!,
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        values
                        )

        // Assign adapter to ListView
        listView = viewRoot.findViewById(R.id.list)
        listView?.setAdapter(adapter)


/*
        val factory = InjectorUtils.provideViewModelFactory(myActivity)
        viewModel = ViewModelProviders.of(myActivity, factory).get(PlayListViewModel::class.java)
        viewModel.getPlayLists().observe(myActivity, Observer {
            it?.let {
                for (i in it.indices) {
                    values += it[i].name
                }

                adapter.addAll(values)
                adapter.notifyDataSetChanged()
            }
        })
*/


        listView?.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            // ListView Clicked item value
            val itemValue = listView?.getItemAtPosition(position) as String
            if (position == 0) {
                Toast.makeText(activity, "User chose create new PlayList", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(activity, "User chose " + values[position], Toast.LENGTH_SHORT).show()

            }
        })

        return viewRoot
    }
}