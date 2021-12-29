package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentPlayerDetailsBinding
import com.dauma.grokimkartu.viewmodels.main.PlayerDetailsViewModel

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player_details, container, false)
    }
}