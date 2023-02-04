package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentMyThomannsBinding
import com.dauma.grokimkartu.general.DummyCell
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannsPage
import com.dauma.grokimkartu.ui.main.adapters.ThomannListAdapter
import com.dauma.grokimkartu.viewmodels.main.MyThomannsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyThomannsFragment : Fragment() {
    private val myThomannsViewModel by viewModels<MyThomannsViewModel>()
    private var isRecyclerViewSetup: Boolean = false
    @Inject lateinit var utils: Utils

    private var _binding: FragmentMyThomannsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "MyThomannsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyThomannsBinding.inflate(inflater, container, false)
        binding.model = myThomannsViewModel
        val view = binding.root

        setupObservers()
        isRecyclerViewSetup = false

        binding.swipeRefreshLayout.setOnRefreshListener {
            myThomannsViewModel.reload()
        }
        val typedValue = TypedValue()
        context?.theme?.resolveAttribute(R.attr.swipe_to_refresh_progress_spinner_color, typedValue, true)
        binding.swipeRefreshLayout.setColorSchemeColors(typedValue.data)

        myThomannsViewModel.viewIsReady()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        myThomannsViewModel.thomannsPages.observe(viewLifecycleOwner, { thomannsPages ->
            val data = getAllThomannsFromPages(thomannsPages)
            if (isRecyclerViewSetup == false) {
                setupRecyclerView(data)
            } else {
                reloadRecyclerViewWithNewData(data)
            }
            if (binding.swipeRefreshLayout.isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
        myThomannsViewModel.navigation.observe(viewLifecycleOwner, EventObserver {
            handleNavigation(it)
        })
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
        binding.myThomannsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.myThomannsRecyclerView.adapter = ThomannListAdapter(
            context = requireContext(),
            thomannListData = thomannListData.toMutableList(),
            utils = utils,
            onItemClicked = { thomannId -> this.myThomannsViewModel.thomannItemClicked(thomannId) },
            loadNextPage = { this.myThomannsViewModel.loadThomannsNextPage() })
        isRecyclerViewSetup = true
    }

    private fun reloadRecyclerViewWithNewData(newData: List<Any>) {
        val adapter = binding.myThomannsRecyclerView.adapter
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

    private fun handleNavigation(navigationCommand: NavigationCommand) {
        when (navigationCommand) {
            is NavigationCommand.ToDirection -> findNavController().navigate(navigationCommand.directions)
            is NavigationCommand.Back -> findNavController().popBackStack()
            is NavigationCommand.CloseApp -> activity?.finish()
        }
    }
}