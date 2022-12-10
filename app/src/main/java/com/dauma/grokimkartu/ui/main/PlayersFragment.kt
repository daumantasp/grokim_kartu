package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.util.TypedValue
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
import com.dauma.grokimkartu.general.DummyCell
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.players.entities.Player
import com.dauma.grokimkartu.repositories.players.entities.PlayersPage
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
        isPlayersRecyclerViewSetup = false

        binding.playersHeaderViewElement.setOnBackClick {
            playersViewModel.backClicked()
        }
        binding.playersHeaderViewElement.setOnRightTextClick {
            playersViewModel.filterClicked()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            playersViewModel.reload()
        }
        val typedValue = TypedValue()
        context?.theme?.resolveAttribute(R.attr.swipe_to_refresh_progress_spinner_color, typedValue, true)
        binding.swipeRefreshLayout.setColorSchemeColors(typedValue.data)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            playersViewModel.backClicked()
        }

        playersViewModel.viewIsReady()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        playersViewModel.filter.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(R.id.action_playersFragment_to_playersFilterFragment)
        })
        playersViewModel.filterEnabled.observe(viewLifecycleOwner, EventObserver {
            binding.playersHeaderViewElement.showRightTextAttentioner(it)
        })
        playersViewModel.playersPages.observe(viewLifecycleOwner, Observer { playersPages ->
            val data = getAllPlayersFromPages(playersPages)
            if (isPlayersRecyclerViewSetup == false) {
                setupPlayersRecyclerView(data)
            } else {
                reloadRecyclerViewWithNewData(data)
            }
            if (binding.swipeRefreshLayout.isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun getAllPlayersFromPages(pages: List<PlayersPage>) : List<Any> {
        val data: MutableList<Any> = mutableListOf()
        for (page in pages) {
            page.players?.let {
                data.addAll(it)
            }
        }
        if (pages.lastOrNull()?.isLast == false) {
            data.add(DummyCell())
        }
        return data
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

            val changedItems: MutableList<Int> = mutableListOf()
            val insertedItems: MutableList<Int> = mutableListOf()
            val removedItems: MutableList<Int> = mutableListOf()

            if (previousData.count() <= newData.count()) {
                for (i in 0 until previousData.count()) {
                    val previousItem = previousData[i]
                    val newItem = newData[i]
                    if (previousItem is Player && newItem is Player) {
                        if (previousItem.userId != newItem.userId) {
                            changedItems.add(i)
                        }
                    } else if (previousItem is DummyCell && newItem is DummyCell) {
                        // DO NOTHING
                    } else {
                        changedItems.add(i)
                    }
                }
                for (i in previousData.count() until newData.count()) {
                    insertedItems.add(i)
                }
            } else {
                for (i in 0 until newData.count()) {
                    val previousItem = previousData[i]
                    val newItem = newData[i]
                    if (previousItem is Player && newItem is Player) {
                        if (previousItem.userId != newItem.userId) {
                            changedItems.add(i)
                        }
                    } else if (previousItem is DummyCell && newItem is DummyCell) {
                        // DO NOTHING
                    } else {
                        changedItems.add(i)
                    }
                }
                for (i in newData.count() until previousData.count()) {
                    removedItems.add(i)
                }
            }

            val sortedChangedItems = changedItems.sorted()
            val sortedInsertedItems = insertedItems.sorted()
            val sortedRemovedItems = removedItems.sorted()

            val sortedChangedRanges = utils.otherUtils.getRanges(sortedChangedItems)
            val sortedInsertedRanges = utils.otherUtils.getRanges(sortedInsertedItems)
            val sortedRemovedRanges = utils.otherUtils.getRanges(sortedRemovedItems)

            adapter.playersListData = newData.toMutableList()
            for (range in sortedRemovedRanges.reversed()) {
                adapter.notifyItemRangeRemoved(range[0], range[1])
            }
            for (range in sortedInsertedRanges) {
                adapter.notifyItemRangeInserted(range[0], range[1])
            }
            for (range in sortedChangedRanges) {
                adapter.notifyItemRangeChanged(range[0], range[1])
            }
        }
    }
}