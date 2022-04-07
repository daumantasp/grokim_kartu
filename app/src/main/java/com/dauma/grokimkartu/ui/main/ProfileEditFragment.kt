package com.dauma.grokimkartu.ui.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentProfileEditBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.DialogsManager
import com.dauma.grokimkartu.viewmodels.main.ProfileEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileEditFragment : Fragment() {
    private val profileEditViewModel by viewModels<ProfileEditViewModel>()
    private var dialogsManager: DialogsManager? = null
    @Inject lateinit var utils: Utils

    private var _binding: FragmentProfileEditBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "ProfileEditFragment"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogsManager = context as? DialogsManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        binding.model = profileEditViewModel
        val view = binding.root
        setupObservers()

        binding.profileEditHeaderViewElement.setOnBackClick {
            profileEditViewModel.backClicked()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            profileEditViewModel.backClicked()
        }

        return view
    }

    private fun setupObservers() {
        profileEditViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dialogsManager = null
    }
}