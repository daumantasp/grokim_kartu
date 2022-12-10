package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.databinding.FragmentPlayersFilterBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.viewmodels.main.PlayersFilterViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayersFilterFragment : Fragment() {
    private val playersFilterViewModel by viewModels<PlayersFilterViewModel>()
    @Inject lateinit var utils: Utils

    private var _binding: FragmentPlayersFilterBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "PlayersFilterFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayersFilterBinding.inflate(inflater, container, false)
        binding.model = playersFilterViewModel
        val view = binding.root
        setupObservers()

        binding.playersFilterHeaderViewElement.setOnBackClick {
            playersFilterViewModel.backClicked()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            playersFilterViewModel.backClicked()
        }

        playersFilterViewModel.viewIsReady()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        playersFilterViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
    }
}