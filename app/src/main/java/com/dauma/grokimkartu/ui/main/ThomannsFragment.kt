package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentThomannsBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannsPage
import com.dauma.grokimkartu.ui.main.adapters.*
import com.dauma.grokimkartu.viewmodels.main.ThomannsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ThomannsFragment : Fragment() {
    private val thomannsViewModel by viewModels<ThomannsViewModel>()
    private var isRecyclerViewSetup: Boolean = false
    @Inject lateinit var utils: Utils

    private var _binding: FragmentThomannsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "ThomannsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThomannsBinding.inflate(inflater, container, false)
        binding.model = thomannsViewModel
        val view = binding.root
        setupObservers()
        isRecyclerViewSetup = false

        binding.thomannsHeaderViewElement.setOnBackClick {
            thomannsViewModel.backClicked()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            thomannsViewModel.backClicked()
        }

        thomannsViewModel.viewIsReady()
        return view
    }

    private fun setupObservers() {
        thomannsViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
        thomannsViewModel.navigateToCreation.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(R.id.action_thomannFragment_to_thomannEditFragment)
        })
        thomannsViewModel.thomannsPages.observe(viewLifecycleOwner, { thomannsPages ->
            val data = getAllThomannsFromPages(thomannsPages)
            if (isRecyclerViewSetup == false) {
                setupRecyclerView(data)
            } else {
                reloadRecyclerViewWithNewData(data)
            }
        })
        thomannsViewModel.thomannDetails.observe(viewLifecycleOwner, EventObserver { thomannId ->
            val args = Bundle()
            args.putInt("thomannId", thomannId)
            this.findNavController().navigate(R.id.action_thomannFragment_to_thomannDetailsFragment, args)
        })
    }

    private fun getAllThomannsFromPages(pages: List<ThomannsPage>) : List<Any> {
        val data: MutableList<Any> = mutableListOf()
        for (page in pages) {
            if (page.thomanns != null) {
                for (thomann in page.thomanns) {
                    data.add(ThomannsListData(thomann))
                }
            }
        }
        if (pages.lastOrNull()?.isLast == false) {
            data.add(ThomannLastInPageData())
        }
        return data
    }

    private fun setupRecyclerView(thomannListData: List<Any>) {
        binding.thomannsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.thomannsRecyclerView.adapter = ThomannListAdapter(
            context = requireContext(),
            thomannListData = thomannListData.toMutableList(),
            utils = utils,
            onItemClicked = { thomannId -> this.thomannsViewModel.thomannItemClicked(thomannId) },
            loadNextPage = { this.thomannsViewModel.loadThomannsNextPage() })
        isRecyclerViewSetup = true
    }

    private fun reloadRecyclerViewWithNewData(newData: List<Any>) {
        val adapter = binding.thomannsRecyclerView.adapter
        if (adapter is ThomannListAdapter) {
            val previousData = adapter.thomannListData

            val changedItems: MutableList<Int> = mutableListOf()
            val insertedItems: MutableList<Int> = mutableListOf()
            val removedItems: MutableList<Int> = mutableListOf()

            if (previousData.count() <= newData.count()) {
                for (i in 0 until previousData.count()) {
                    val previousItem = previousData[i]
                    val newItem = newData[i]
                    if (previousItem is ThomannsListData && newItem is ThomannsListData) {
                        if (previousItem.thomann.id != newItem.thomann.id) {
                            changedItems.add(i)
                        }
                    } else if (previousItem is ThomannLastInPageData && newItem is ThomannLastInPageData) {
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
                    if (previousItem is ThomannsListData && newItem is ThomannsListData) {
                        if (previousItem.thomann.id != newItem.thomann.id) {
                            changedItems.add(i)
                        }
                    } else if (previousItem is ThomannLastInPageData && newItem is ThomannLastInPageData) {
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