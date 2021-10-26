package com.dauma.grokimkartu.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.databinding.FragmentProfileBinding
import com.dauma.grokimkartu.models.EventObserver
import com.dauma.grokimkartu.viewmodels.profile.ProfileViewModelImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val profileViewModel by viewModels<ProfileViewModelImpl>()

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

        profileViewModel.navigateToLogin.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().navigate(it as Int)
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}