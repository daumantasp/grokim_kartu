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
import com.dauma.grokimkartu.ui.main.adapters.ThomannListAdapter
import com.dauma.grokimkartu.ui.main.adapters.ThomannsListData
import com.dauma.grokimkartu.viewmodels.main.ThomannsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ThomannsFragment : Fragment() {
    private val thomannsViewModel by viewModels<ThomannsViewModel>()
    private var isRecyclerViewSetup: Boolean = false
    @Inject lateinit var utils: Utils
    private var thomannsRecyclerViewData: MutableList<ThomannsListData> = mutableListOf()

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
        thomannsViewModel.thomannsListData.observe(viewLifecycleOwner, {
            this.thomannsRecyclerViewData.clear()
            this.thomannsRecyclerViewData.addAll(it)
            if (isRecyclerViewSetup == false) {
                setupRecyclerView()
            } else {
                binding.thomannsRecyclerView.adapter?.notifyDataSetChanged()
            }
        })
        thomannsViewModel.thomannDetails.observe(viewLifecycleOwner, EventObserver {
            val args = Bundle()
            args.putString("thomannId", it)
            this.findNavController().navigate(R.id.action_thomannFragment_to_thomannDetailsFragment, args)
        })
    }

    private fun setupRecyclerView() {
        binding.thomannsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.thomannsRecyclerView.adapter = ThomannListAdapter(requireContext(), thomannsRecyclerViewData, utils) { thomannItemId ->
            this.thomannsViewModel.thomannItemClicked(thomannItemId)
        }
        isRecyclerViewSetup = true
    }
}