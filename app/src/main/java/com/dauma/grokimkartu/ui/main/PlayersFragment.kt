package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dauma.grokimkartu.data.players.entities.FirestorePlayer
import com.dauma.grokimkartu.databinding.FragmentPlayersBinding
import com.dauma.grokimkartu.ui.main.adapters.PlayersListAdapter
import com.dauma.grokimkartu.viewmodels.main.PlayersViewModel
import dagger.hilt.android.AndroidEntryPoint

// read more at https://medium.com/mobile-app-development-publication/injecting-viewmodel-with-dagger-hilt-54ca2e433865

@AndroidEntryPoint
class PlayersFragment : Fragment() {
    private val playersViewModel by viewModels<PlayersViewModel>()
    private var isPlayersRecyclerViewSetup: Boolean = false

    private var _binding: FragmentPlayersBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "PlayersFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayersBinding.inflate(inflater, container, false)
        binding.model = playersViewModel
        val view = binding.root
        setupObservers()

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            playersViewModel.backClicked()
        }

        playersViewModel.loadPlayers()

        return view
    }

    private fun setupObservers() {
        playersViewModel.players.observe(viewLifecycleOwner, Observer {
                if (isPlayersRecyclerViewSetup == false) {
                    setupPlayersRecyclerView(it)
                } else {
                    binding.playersRecyclerView.adapter?.notifyDataSetChanged()
                }
            })
    }

    private fun setupPlayersRecyclerView(players: List<FirestorePlayer>) {
        binding.playersRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.playersRecyclerView.adapter = PlayersListAdapter(players)
    }
}