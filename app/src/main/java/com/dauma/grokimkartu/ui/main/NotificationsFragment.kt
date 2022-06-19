package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dauma.grokimkartu.databinding.FragmentNotificationsBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.viewmodels.main.NotificationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    private val notificationsViewModel by viewModels<NotificationsViewModel>()
    private var isViewSetup: Boolean = false
    @Inject lateinit var utils: Utils

    private var _binding: FragmentNotificationsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "NotificationsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        binding.model = notificationsViewModel
        val view = binding.root
        setupObservers()
        isViewSetup = false

        binding.notificationsHeaderViewElement.setOnBackClick {
            notificationsViewModel.backClicked()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            notificationsViewModel.backClicked()
        }

        notificationsViewModel.viewIsReady()
        return view
    }

    private fun setupObservers() {
        notificationsViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
    }
}