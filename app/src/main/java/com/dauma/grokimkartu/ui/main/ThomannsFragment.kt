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
        thomannsViewModel.thomannsListData.observe(viewLifecycleOwner, { thomannListData ->
            if (isRecyclerViewSetup == false) {
                setupRecyclerView(thomannListData)
            } else {
                reloadRecyclerViewWithNewData(thomannListData)
            }
        })
        thomannsViewModel.thomannDetails.observe(viewLifecycleOwner, EventObserver { thomannId ->
            val args = Bundle()
            args.putInt("thomannId", thomannId)
            this.findNavController().navigate(R.id.action_thomannFragment_to_thomannDetailsFragment, args)
        })
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

            var changedPosition: Int? = null
            var firstItemInsertedPosition: Int? = null
            var itemsInsertedCount: Int = 0

            val previousDataHasLastInPage = previousData.lastOrNull() is ThomannLastInPageData
            if (previousData.count() == newData.count()) {
                if (previousDataHasLastInPage && newData.lastOrNull() is ThomannsListData) {
                    changedPosition = previousData.lastIndex
                }
            } else if (previousData.count() < newData.count()) {
                if (previousDataHasLastInPage && newData[previousData.lastIndex] is ThomannsListData) {
                    changedPosition = previousData.lastIndex
                }

                firstItemInsertedPosition = if (changedPosition != null) changedPosition + 1 else previousData.count()
                itemsInsertedCount = newData.count() - firstItemInsertedPosition
            }

            if (changedPosition != null) {
                adapter.thomannListData[changedPosition] = newData[changedPosition]
                adapter.notifyItemChanged(changedPosition)
            }
            if (firstItemInsertedPosition != null) {
                val toIndex = firstItemInsertedPosition + itemsInsertedCount
                val newDataToAdd = newData.subList(firstItemInsertedPosition, toIndex)
                adapter.thomannListData.addAll(newDataToAdd)
                adapter.notifyItemRangeInserted(firstItemInsertedPosition, itemsInsertedCount)
            }
        }
    }
}