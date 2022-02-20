package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.databinding.FragmentPlayerDetailsBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.viewmodels.main.PlayerDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayerDetailsFragment : Fragment() {
    private val playerDetailsViewModel by viewModels<PlayerDetailsViewModel>()
    @Inject lateinit var utils: Utils

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
        setupObservers()

        if (savedInstanceState == null) {
            // TODO: Still reloads on device rotate, probably need to save state instance
            playerDetailsViewModel.loadDetails()
        }

        binding.playerDetailsHeaderViewElement.setOnBackClick {
            playerDetailsViewModel.backClicked()
        }
        binding.playerDetailsHeaderViewElement.setOnRightTextClick {
            playerDetailsViewModel.reportClicked()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        playerDetailsViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
        playerDetailsViewModel.detailsLoaded.observe(viewLifecycleOwner, EventObserver {
            if (playerDetailsViewModel.getPlayerDetailsForm().photo == null) {
                val initials = utils.stringUtils.getInitials(playerDetailsViewModel.getPlayerDetailsForm().name)
                binding.profileInitialsViewElement.setInitials(initials)
                binding.photoImageView.visibility = View.GONE
                binding.profileInitialsViewElement.visibility = View.VISIBLE
            } else {
                binding.profileInitialsViewElement.visibility = View.GONE
                binding.photoImageView.visibility = View.VISIBLE
            }
        })
    }
}