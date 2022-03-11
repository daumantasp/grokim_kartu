package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.viewmodels.main.ThomannDetailsViewModel
import com.dauma.grokimkartu.databinding.FragmentThomannDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThomannDetailsFragment : Fragment() {
    private val thomannDetailsViewModel by viewModels<ThomannDetailsViewModel>()

    private var _binding: FragmentThomannDetailsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentThomannDetailsBinding.inflate(inflater, container, false)
        binding.model = thomannDetailsViewModel
        val view = binding.root
        setupObservers()

        if (savedInstanceState == null) {
            // TODO: Still reloads on device rotate, probably need to save state instance
            thomannDetailsViewModel.loadDetails()
        }

        binding.thomannDetailsHeaderViewElement.setOnBackClick {
            thomannDetailsViewModel.backClicked()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        thomannDetailsViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
    }
}