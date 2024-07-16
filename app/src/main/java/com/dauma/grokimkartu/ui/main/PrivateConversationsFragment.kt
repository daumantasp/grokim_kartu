package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dauma.grokimkartu.databinding.FragmentPrivateConversationsBinding
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.main.adapters.ConversationsAdapter
import com.dauma.grokimkartu.ui.main.adapters.PrivateConversationData
import com.dauma.grokimkartu.viewmodels.main.PrivateConversationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
    ): View {
        _binding = FragmentPrivateConversationsBinding.inflate(inflater, container, false)
        binding.model = privateConversationsViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnClickers()
        setupObservers()
        isViewSetup = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupOnClickers() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            privateConversationsViewModel.reload()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    privateConversationsViewModel.uiState.collect {
                        val privateConversationData = it.conversations.map { c -> PrivateConversationData(c) }
                        if (isViewSetup == false) {
                            setupPrivateConversationsRecyclerView(privateConversationData)
                        } else {
                            val conversationsAdapter = binding.privateConversationsRecyclerView.adapter as? ConversationsAdapter
                            conversationsAdapter?.conversationsListData?.clear()
                            conversationsAdapter?.conversationsListData?.addAll(privateConversationData)
                            binding.privateConversationsRecyclerView.adapter?.notifyDataSetChanged()
                        }
                        if (binding.swipeRefreshLayout.isRefreshing) {
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                    }
                }
            }
        }
    }

    private fun setupPrivateConversationsRecyclerView(conversations: List<PrivateConversationData>) {
        binding.privateConversationsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.privateConversationsRecyclerView.adapter = ConversationsAdapter(
            context = requireContext(),
            conversationsListData = conversations.toMutableList(),
            utils = utils,
            onItemClicked = { userId, name ->
                findNavController().navigate(ConversationsFragmentDirections.actionConversationsFragmentToConversationFragment2(
                    userId = userId,
                    thomannId = -1,
                    userName = name
                ))
            }
        )
        isViewSetup = true
    }
}