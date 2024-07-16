package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentAllThomannsBinding
import com.dauma.grokimkartu.general.DummyCell
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannsPage
import com.dauma.grokimkartu.ui.main.adapters.ThomannListAdapter
import com.dauma.grokimkartu.viewmodels.main.AllThomannsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AllThomannsFragment : Fragment() {
    private val allThomannsViewModel by viewModels<AllThomannsViewModel>()
    private var isRecyclerViewSetup: Boolean = false
    @Inject lateinit var utils: Utils

    private var _binding: FragmentAllThomannsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "AllThomannsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllThomannsBinding.inflate(inflater, container, false)
        binding.model = allThomannsViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isRecyclerViewSetup = false
        setupOnClickers()
        setupObservers()

        val typedValue = TypedValue()
        context?.theme?.resolveAttribute(R.attr.swipe_to_refresh_progress_spinner_color, typedValue, true)
        binding.swipeRefreshLayout.setColorSchemeColors(typedValue.data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupOnClickers() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            allThomannsViewModel.reload()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    allThomannsViewModel.uiState.collect {
                        val data = getAllThomannsFromPages(it.thomannsPages)
                        if (!isRecyclerViewSetup) {
                            setupRecyclerView(data)
                        } else {
                            reloadRecyclerViewWithNewData(data)
                        }
                        if (binding.swipeRefreshLayout.isRefreshing) {
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                        if (it.isThomannDetailsStarted != -1) {
                            val args = Bundle()
                            args.putInt("thomannId", it.isThomannDetailsStarted)
                            findNavController().navigate(R.id.action_thomannFragment_to_thomannDetailsFragment, args)
                        }
                    }
                }
            }
        }
    }

    private fun getAllThomannsFromPages(pages: List<ThomannsPage>) : List<Any> {
        val data: MutableList<Any> = mutableListOf()
        for (page in pages) {
            page.thomanns?.let {
                data.addAll(it)
            }
        }
        if (pages.lastOrNull()?.isLast == false) {
            data.add(DummyCell())
        }
        return data
    }

    private fun setupRecyclerView(thomannListData: List<Any>) {
        binding.allThomannsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.allThomannsRecyclerView.adapter = ThomannListAdapter(
            context = requireContext(),
            thomannListData = thomannListData.toMutableList(),
            utils = utils,
            onItemClicked = { thomannId -> this.allThomannsViewModel.thomannItemClicked(thomannId) },
            loadNextPage = { this.allThomannsViewModel.loadNextThomannsPage() })
        isRecyclerViewSetup = true
    }

    private fun reloadRecyclerViewWithNewData(newData: List<Any>) {
        val adapter = binding.allThomannsRecyclerView.adapter
        if (adapter is ThomannListAdapter) {
            val previousData = adapter.thomannListData

            val changedItems: MutableList<Int> = mutableListOf()
            val insertedItems: MutableList<Int> = mutableListOf()
            val removedItems: MutableList<Int> = mutableListOf()

            if (previousData.count() <= newData.count()) {
                for (i in 0 until previousData.count()) {
                    val previousItem = previousData[i]
                    val newItem = newData[i]
                    if (previousItem is Thomann && newItem is Thomann) {
                        if (previousItem.id != newItem.id) {
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
                    if (previousItem is Thomann && newItem is Thomann) {
                        if (previousItem.id != newItem.id) {
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

            adapter.thomannListData = newData.toMutableList()
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