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
import com.dauma.grokimkartu.databinding.FragmentThomannBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.ui.main.adapters.ThomannListAdapter
import com.dauma.grokimkartu.ui.main.adapters.ThomannListData
import com.dauma.grokimkartu.viewmodels.main.ThomannViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThomannFragment : Fragment() {
    private val thomannViewModel by viewModels<ThomannViewModel>()
    private var _binding: FragmentThomannBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "ThomannFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThomannBinding.inflate(inflater, container, false)
        binding.model = thomannViewModel
        val view = binding.root
        setupObservers()

        binding.thomannHeaderViewElement.setOnBackClick {
            thomannViewModel.backClicked()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            thomannViewModel.backClicked()
        }

        // DEBUG
        setupRecyclerView(listOf())

        thomannViewModel.viewIsReady()
        return view
    }

    private fun setupObservers() {
        thomannViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
    }

    private fun setupRecyclerView(listData: List<ThomannListData>) {
        binding.thomannRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.thomannRecyclerView.adapter = ThomannListAdapter(listData) { thomannItemId ->
            this.thomannViewModel.thomannItemClicked(thomannItemId)
        }
    }
}