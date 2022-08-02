package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dauma.grokimkartu.databinding.FragmentConversationsBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.main.adapters.ConversationData
import com.dauma.grokimkartu.ui.main.adapters.ConversationsAdapter
import com.dauma.grokimkartu.viewmodels.main.ConversationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ConversationsFragment : Fragment() {
    private val conversationsViewModel by viewModels<ConversationsViewModel>()
    private var isViewSetup: Boolean = false
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
        setupObservers()
        isViewSetup = false

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            conversationsViewModel.backClicked()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            conversationsViewModel.reload()
        }

        conversationsViewModel.viewIsReady()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        conversationsViewModel.viewIsDiscarded()
    }

    private fun setupObservers() {
        conversationsViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
        conversationsViewModel.privateConversations.observe(viewLifecycleOwner, {
            val conversationsData = it.map { c -> ConversationData(c) }
            if (isViewSetup == false) {
                setupPrivateConversationsRecyclerView(conversationsData)
            } else {
                binding.privateConversationsRecyclerView.adapter?.notifyDataSetChanged()
            }
            if (binding.swipeRefreshLayout.isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun setupPrivateConversationsRecyclerView(conversationsListData: List<ConversationData>) {
        binding.privateConversationsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.privateConversationsRecyclerView.adapter = ConversationsAdapter(
            context = requireContext(),
            conversationsListData = conversationsListData.toMutableList(),
            utils = utils,
            onItemClicked = {}
        )
        isViewSetup = true
    }
}