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
import com.dauma.grokimkartu.databinding.FragmentProfileBinding
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.models.forms.ProfileForm
import com.dauma.grokimkartu.viewmodels.main.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val profileViewModel by viewModels<ProfileViewModel>()
    @Inject lateinit var utils: Utils

    private var _binding: FragmentProfileBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.model = profileViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnClickers()
        setupObservers()
        setupScrollView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupOnClickers() {
        binding.editProfileButton.setOnClick {
            profileViewModel.editProfile()
        }
    }

    private fun setupObservers() {
        profileViewModel.getProfileForm().addOnPropertyChangedCallback(onPhotoOrNameChanged())

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    profileViewModel.uiState.collect {
                        if (it.isProfileEditStarted) {
                            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment())
                            profileViewModel.editProfileStarted()
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
                        val profileEditForm = sender as ProfileForm
                        profileEditForm.photo?.let {
                            binding.photoImageView.setImageBitmap(it)
                            binding.profileInitialsViewElement.visibility = View.GONE
                            binding.photoImageView.visibility = View.VISIBLE
                            binding.profilePhotoOrInitialsConstraintLayout.visibility = View.VISIBLE
                        } ?: run {
                            val initials = utils.stringUtils.getInitials(profileEditForm.name)
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

    private fun setupScrollView() {
        binding.profileScrollView.setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
            binding.profileHeaderViewElement.showShadow(scrollY > 0.0)
        }
    }
}