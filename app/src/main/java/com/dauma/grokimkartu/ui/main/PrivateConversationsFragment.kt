package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dauma.grokimkartu.databinding.FragmentPrivateConversationsBinding
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.main.adapters.ConversationData
import com.dauma.grokimkartu.ui.main.adapters.ConversationsAdapter
import com.dauma.grokimkartu.viewmodels.main.PrivateConversationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PrivateConversationsFragment : Fragment() {
    private val privateConversationsViewModel by viewModels<PrivateConversationsViewModel>()
    private var isViewSetup: Boolean = false
    @Inject lateinit var utils: Utils

    private var _binding: FragmentPrivateConversationsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "PrivateConversationsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrivateConversationsBinding.inflate(inflater, container, false)
        binding.model = privateConversationsViewModel
        val view = binding.root
        setupObservers()
        isViewSetup = false

        binding.swipeRefreshLayout.setOnRefreshListener {
            privateConversationsViewModel.reload()
        }

        privateConversationsViewModel.viewIsReady()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        privateConversationsViewModel.viewIsDiscarded()
    }

    private fun setupObservers() {
        privateConversationsViewModel.privateConversations.observe(viewLifecycleOwner, {
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