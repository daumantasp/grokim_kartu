package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentConversationsBinding
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.main.adapters.ConversationsPagerAdapter
import com.dauma.grokimkartu.viewmodels.main.ConversationsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ConversationsFragment : Fragment() {
    private val conversationsViewModel by viewModels<ConversationsViewModel>()
    @Inject lateinit var utils: Utils

    private var _binding: FragmentConversationsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "ConversationsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConversationsBinding.inflate(inflater, container, false)
        binding.model = conversationsViewModel
        val view = binding.root

        val viewPager = binding.conversationsViewPager
        val tabLayout = binding.conversationsTabLayout

        val adapter = ConversationsPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        val tabTitles = arrayOf(
            requireContext().getString(R.string.conversations_private_title),
            requireContext().getString(R.string.conversations_thomann_title)
        )
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        conversationsViewModel.viewIsReady()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        conversationsViewModel.viewIsDiscarded()
    }
}