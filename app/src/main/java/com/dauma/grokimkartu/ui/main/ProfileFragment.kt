package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentProfileBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.viewmodels.main.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
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
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.model = profileViewModel
        val view = binding.root
        setupObservers()
        setupScrollView()

        profileViewModel.viewIsReady()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        profileViewModel.profileLoaded.observe(viewLifecycleOwner, EventObserver {
            if (profileViewModel.getProfileForm().photo == null) {
                val initials = utils.stringUtils.getInitials(profileViewModel.getProfileForm().name ?: "")
                binding.profileInitialsViewElement.setInitials(initials)
                binding.photoImageView.visibility = View.GONE
                binding.profileInitialsViewElement.visibility = View.VISIBLE
            } else {
                binding.profileInitialsViewElement.visibility = View.GONE
                binding.photoImageView.visibility = View.VISIBLE
            }
            binding.profilePhotoOrInitialsConstraintLayout.visibility = View.VISIBLE
        })
        profileViewModel.profileEdit.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(R.id.action_profileFragment_to_profileEditFragment)
        })
    }

    private fun setupScrollView() {
        binding.profileScrollView.setOnScrollChangeListener { view, scrollX, scrollY, oldScrollX, oldScrollY ->
            binding.profileHeaderViewElement.showShadow(scrollY > 0.0)
        }
    }
}