package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.dauma.grokimkartu.databinding.FragmentPlayerDetailsBinding
import com.dauma.grokimkartu.viewmodels.main.PlayerDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerDetailsFragment : Fragment() {
    private val playerDetailsViewModel by viewModels<PlayerDetailsViewModel>()

    private var _binding: FragmentPlayerDetailsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPlayerDetailsBinding.inflate(inflater, container, false)
        binding.model = playerDetailsViewModel
        val view = binding.root
        if (savedInstanceState == null) {
            // TODO: Still reloads on device rotate, probably need to save state instance
            playerDetailsViewModel.loadDetails()
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}