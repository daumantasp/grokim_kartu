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
import com.dauma.grokimkartu.ui.main.adapters.PlayerLastInPageData
import com.dauma.grokimkartu.ui.main.adapters.PlayersListAdapter
import com.dauma.grokimkartu.ui.main.adapters.PlayersListData
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
        isPlayersRecyclerViewSetup = false

        binding.playersHeaderViewElement.setOnBackClick {
            playersViewModel.backClicked()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            playersViewModel.backClicked()
        }

        playersViewModel.viewIsReady()
        return view
    }

    private fun setupObservers() {
        playersViewModel.playerDetails.observe(viewLifecycleOwner, EventObserver { userId ->
            val args = Bundle()
            args.putInt("userId", userId)
            this.findNavController().navigate(R.id.action_playersFragment_to_playerDetailsFragment, args)
        })
        playersViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
        playersViewModel.playersListData.observe(viewLifecycleOwner, Observer { playersListData ->
                if (isPlayersRecyclerViewSetup == false) {
                    setupPlayersRecyclerView(playersListData)
                } else {
                    reloadRecyclerViewWithNewData(playersListData)
                }
            })
    }

    private fun setupPlayersRecyclerView(playersListData: List<Any>) {
        binding.playersRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.playersRecyclerView.adapter = PlayersListAdapter(
            context = requireContext(),
            playersListData = playersListData.toMutableList(),
            utils = utils,
            onItemClicked = { userId -> this.playersViewModel.playerClicked(userId) },
            loadNextPage = { this.playersViewModel.loadNextPlayersPage() })
        isPlayersRecyclerViewSetup = true
    }

    private fun reloadRecyclerViewWithNewData(newData: List<Any>) {
        val adapter = binding.playersRecyclerView.adapter
        if (adapter is PlayersListAdapter) {
            val previousData = adapter.playersListData

            var changedPosition: Int? = null
            var firstItemInsertedPosition: Int? = null
            var itemsInsertedCount: Int = 0

            val previousDataHasLastInPage = previousData.lastOrNull() is PlayerLastInPageData
            if (previousData.count() == newData.count()) {
                if (previousDataHasLastInPage && newData.lastOrNull() is PlayersListData) {
                    changedPosition = previousData.lastIndex
                }
            } else if (previousData.count() < newData.count()) {
                if (previousDataHasLastInPage && newData[previousData.lastIndex] is PlayersListData) {
                    changedPosition = previousData.lastIndex
                }

                firstItemInsertedPosition = if (changedPosition != null) changedPosition + 1 else previousData.count()
                itemsInsertedCount = newData.count() - firstItemInsertedPosition
            }

            if (changedPosition != null) {
                adapter.playersListData[changedPosition] = newData[changedPosition]
                adapter.notifyItemChanged(changedPosition)
            }
            if (firstItemInsertedPosition != null) {
                val toIndex = firstItemInsertedPosition + itemsInsertedCount
                val newDataToAdd = newData.subList(firstItemInsertedPosition, toIndex)
                adapter.playersListData.addAll(newDataToAdd)
                adapter.notifyItemRangeInserted(firstItemInsertedPosition, itemsInsertedCount)
            }
        }
    }
}