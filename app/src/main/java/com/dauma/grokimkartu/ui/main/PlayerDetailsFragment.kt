package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.BR
import com.dauma.grokimkartu.databinding.FragmentPlayerDetailsBinding
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.models.forms.PlayerDetailsForm
import com.dauma.grokimkartu.viewmodels.main.PlayerDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
    ): View {
        _binding = FragmentPlayerDetailsBinding.inflate(inflater, container, false)
        binding.model = playerDetailsViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnClickers()
        setupObservers()
    }

    private fun setupOnClickers() {
        binding.playerDetailsHeaderViewElement.setOnBackClick {
            playerDetailsViewModel.back()
        }
        binding.playerDetailsHeaderViewElement.setOnRightTextClick {
            playerDetailsViewModel.report()
        }
        binding.messageButtonViewElement.setOnClick {
            playerDetailsViewModel.message()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        playerDetailsViewModel.getPlayerDetailsForm().addOnPropertyChangedCallback(onPhotoOrNameChanged())

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    playerDetailsViewModel.uiState.collect {
                        if (it.messageStarted) {
                            it.messageUserId?.let { userId ->
                                findNavController().navigate(PlayerDetailsFragmentDirections.actionPlayerDetailsFragmentToConversationFragment(userId, -1, it.messageTitle ?: ""))
                                playerDetailsViewModel.messageStarted()
                            }
                        } else if (it.close) {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun onPhotoOrNameChanged() : Observable.OnPropertyChangedCallback {
        return object : Observable.OnPropertyChangedCallback() {
            var isProfileLoaded = false
            var isPhotoLoaded = false
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                fun checkIfFullProfileLoaded() {
                    if (isProfileLoaded == true && isPhotoLoaded == true) {
                        val playerDetailsForm = sender as PlayerDetailsForm
                        playerDetailsForm.photo?.let {
                            binding.photoImageView.setImageBitmap(it)
                            binding.profileInitialsViewElement.visibility = View.GONE
                            binding.photoImageView.visibility = View.VISIBLE
                            binding.profilePhotoOrInitialsConstraintLayout.visibility = View.VISIBLE
                        } ?: run {
                            val initials = utils.stringUtils.getInitials(playerDetailsForm.name)
                            binding.profileInitialsViewElement.setInitials(initials)
                            binding.photoImageView.visibility = View.GONE
                            binding.profileInitialsViewElement.visibility = View.VISIBLE
                            binding.profilePhotoOrInitialsConstraintLayout.visibility = View.VISIBLE
                        }
                    }
                }
                if (propertyId == BR.photo) {
                    isPhotoLoaded = true
                    checkIfFullProfileLoaded()
                } else if (propertyId == BR.name) {
                    isProfileLoaded = true
                    checkIfFullProfileLoaded()
                }
            }
        }
    }
}