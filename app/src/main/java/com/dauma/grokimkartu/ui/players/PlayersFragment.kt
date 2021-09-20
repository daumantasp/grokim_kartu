package com.dauma.grokimkartu.ui.players

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.globals.ComponentProvider
import com.dauma.grokimkartu.models.Player
import com.dauma.grokimkartu.viewmodels.players.PlayersViewModel
import com.dauma.grokimkartu.viewmodels.players.PlayersViewModelImpl

class PlayersFragment : Fragment() {
    private var playersViewModel: PlayersViewModel? = null
    private var playersRecyclerView: RecyclerView? = null
    private var isPlayersRecyclerViewSetup: Boolean = false

    companion object {
        private var TAG = "PlayersFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playersViewModel = ViewModelProvider(this, ComponentProvider.ioCContainer.playersViewModelFactory()).get(
            PlayersViewModelImpl::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_players, container, false)
        playersRecyclerView = rootView.findViewById(R.id.playersRecyclerView)

        playersViewModel!!.getPlayers().observe(
            viewLifecycleOwner, Observer {
                if (isPlayersRecyclerViewSetup == false) {
                    setupPlayersRecyclerView(it)
                } else {
                    playersRecyclerView?.adapter?.notifyDataSetChanged()
                }
            })

        return rootView
    }

    private fun setupPlayersRecyclerView(players: List<Player>) {
        if (playersRecyclerView == null) {
            Log.d(TAG, "ERROR: playersRecyclerView not found")
            return
        }

        playersRecyclerView!!.layoutManager = LinearLayoutManager(context)
        playersRecyclerView!!.adapter = PlayersListAdapter(players)
    }
}