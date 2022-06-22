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
import com.dauma.grokimkartu.databinding.FragmentNotificationsBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationsPage
import com.dauma.grokimkartu.ui.main.adapters.*
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
        notificationsViewModel.notificationsPages.observe(viewLifecycleOwner, { notificationsPages ->
            val data = getAllNotificationsFromPages(notificationsPages)
            if (isViewSetup == false) {
                setupNotificationsRecyclerView(data)
            } else {
                reloadRecyclerViewWithNewData(data)
            }
        })
        notificationsViewModel.notificationsUpdated.observe(viewLifecycleOwner, { notificationsPages ->
            val data = getAllNotificationsFromPages(notificationsPages)
            reloadRecyclerViewWithNewData(data)
        })
    }

    private fun getAllNotificationsFromPages(pages: List<NotificationsPage>) : List<Any> {
        val data: MutableList<Any> = mutableListOf()
        for (page in pages) {
            if (page.notifications != null) {
                for (notification in page.notifications) {
                    data.add(NotificationsListData(notification))
                }
            }
        }
        if (pages.lastOrNull()?.isLast == false) {
            data.add(NotificationLastInPageData())
        }
        return data
    }

    private fun setupNotificationsRecyclerView(notificationsListData: List<Any>) {
        binding.notificationsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.notificationsRecyclerView.adapter = NotificationsListAdapter(
            notificationsListData = notificationsListData.toMutableList(),
            utils = utils,
            onItemClicked = { notificationId -> this.notificationsViewModel.notificationClicked(notificationId)},
            loadNextPage = { this.notificationsViewModel.loadNextNotificationsPage() }
        )
        isViewSetup = true
    }

    private fun reloadRecyclerViewWithNewData(newData: List<Any>) {
        val adapter = binding.notificationsRecyclerView.adapter
        if (adapter is NotificationsListAdapter) {
            val previousData = adapter.notificationsListData

            val changedItems: MutableList<Int> = mutableListOf()
            val insertedItems: MutableList<Int> = mutableListOf()
            val removedItems: MutableList<Int> = mutableListOf()

            if (previousData.count() <= newData.count()) {
                for (i in 0 until previousData.count()) {
                    val previousItem = previousData[i]
                    val newItem = newData[i]
                    if (previousItem is NotificationsListData && newItem is NotificationsListData) {
                        if (previousItem.notification.id != newItem.notification.id) {
                            changedItems.add(i)
                        } else if (previousItem.notification.isRead != newItem.notification.isRead) {
                            changedItems.add(i)
                        }
                    } else if (previousItem is NotificationLastInPageData && newItem is NotificationLastInPageData) {
                        // DO NOTHING
                    } else {
                        changedItems.add(i)
                    }
                }
                for (i in previousData.count() until newData.count()) {
                    insertedItems.add(i)
                }
            } else {
                for (i in 0 until newData.count()) {
                    val previousItem = previousData[i]
                    val newItem = newData[i]
                    if (previousItem is NotificationsListData && newItem is NotificationsListData) {
                        if (previousItem.notification.id != newItem.notification.id) {
                            changedItems.add(i)
                        } else if (previousItem.notification.isRead != newItem.notification.isRead) {
                            changedItems.add(i)
                        }
                    } else if (previousItem is NotificationLastInPageData && newItem is NotificationLastInPageData) {
                        // DO NOTHING
                    } else {
                        changedItems.add(i)
                    }
                }
                for (i in newData.count() until previousData.count()) {
                    removedItems.add(i)
                }
            }

            val sortedChangedItems = changedItems.sorted()
            val sortedInsertedItems = insertedItems.sorted()
            val sortedRemovedItems = removedItems.sorted()

            val sortedChangedRanges = utils.otherUtils.getRanges(sortedChangedItems)
            val sortedInsertedRanges = utils.otherUtils.getRanges(sortedInsertedItems)
            val sortedRemovedRanges = utils.otherUtils.getRanges(sortedRemovedItems)

            adapter.notificationsListData = newData.toMutableList()
            for (range in sortedRemovedRanges.reversed()) {
                adapter.notifyItemRangeRemoved(range[0], range[1])
            }
            for (range in sortedInsertedRanges) {
                adapter.notifyItemRangeInserted(range[0], range[1])
            }
            for (range in sortedChangedRanges) {
                adapter.notifyItemRangeChanged(range[0], range[1])
            }
        }
    }
}