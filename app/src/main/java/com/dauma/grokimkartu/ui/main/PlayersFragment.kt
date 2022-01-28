package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentPlayersBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.players.entities.Player
import com.dauma.grokimkartu.ui.main.adapters.PlayersListAdapter
import com.dauma.grokimkartu.viewmodels.main.PlayersViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// read more at https://medium.com/mobile-app-development-publication/injecting-viewmodel-with-dagger-hilt-54ca2e433865

@AndroidEntryPoint
class PlayersFragment : Fragment() {
    private val playersViewModel by viewModels<PlayersViewModel>()
    private var isPlayersRecyclerViewSetup: Boolean = false
    @Inject lateinit var utils: Utils

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
        playersViewModel.playerDetails.observe(viewLifecycleOwner, EventObserver {
            val args = Bundle()
            args.putString("userId", it)
            this.findNavController().navigate(R.id.action_playersFragment_to_playerDetailsFragment, args)
        })
    }

    private fun setupPlayersRecyclerView(players: List<Player>) {
        binding.playersRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.playersRecyclerView.adapter = PlayersListAdapter(requireContext(), players, utils) { userId ->
            this.playersViewModel.playerClicked(userId)
        }
    }
}