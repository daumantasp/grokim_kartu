package com.dauma.grokimkartu.ui.main

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentNotificationsBinding
import com.dauma.grokimkartu.general.DummyCell
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationsPage
import com.dauma.grokimkartu.ui.main.adapters.NotificationsListAdapter
import com.dauma.grokimkartu.viewmodels.main.NotificationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        binding.model = notificationsViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewSetup = false
        setupOnClickers()
        setupObservers()
        setupSwipeToRefresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupOnClickers() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            notificationsViewModel.reload()
        }
        binding.notificationsHeaderViewElement.setOnBackClick {
            notificationsViewModel.back()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            notificationsViewModel.back()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    notificationsViewModel.uiState.collect {
                        val data = getAllNotificationsFromPages(it.notificationsPages)
                        if (!isViewSetup) {
                            setupNotificationsRecyclerView()
                        }
                        reloadRecyclerViewWithNewData(data)
                        if (binding.swipeRefreshLayout.isRefreshing) {
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                        if (it.close) {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun setupSwipeToRefresh() {
        val typedValue = TypedValue()
        context?.theme?.resolveAttribute(R.attr.swipe_to_refresh_progress_spinner_color, typedValue, true)
        binding.swipeRefreshLayout.setColorSchemeColors(typedValue.data)
    }

    private fun getAllNotificationsFromPages(pages: List<NotificationsPage>) : List<Any> {
        val data: MutableList<Any> = mutableListOf()
        for (page in pages) {
            for (notification in page.notifications) {
                data.add(notification)
            }
        }
        if (pages.lastOrNull()?.isLast == false) {
            data.add(DummyCell())
        }
        return data
    }

    private fun setupNotificationsRecyclerView() {
        binding.notificationsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.notificationsRecyclerView.adapter = NotificationsListAdapter(
            utils = utils,
            onItemClicked = { notificationId -> this.notificationsViewModel.notificationExpand(notificationId)},
            loadNextPage = { this.notificationsViewModel.loadNextNotificationsPage() }
        )
        isViewSetup = true
    }

    private fun reloadRecyclerViewWithNewData(newData: List<Any>) {
        val adapter = binding.notificationsRecyclerView.adapter
        if (adapter is NotificationsListAdapter) {
            adapter.data = newData
        }
    }
}