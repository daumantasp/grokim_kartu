package com.dauma.grokimkartu.ui.main

import android.content.Context
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
import com.dauma.grokimkartu.databinding.FragmentThomannDetailsBinding
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDateTimeFormatPattern
import com.dauma.grokimkartu.ui.BottomDialogAmountData
import com.dauma.grokimkartu.ui.DialogsManager
import com.dauma.grokimkartu.ui.main.adapters.ThomannDetailsAdapter
import com.dauma.grokimkartu.ui.main.adapters.ThomannDetailsButtonData
import com.dauma.grokimkartu.ui.main.adapters.ThomannDetailsPhotoData
import com.dauma.grokimkartu.ui.main.adapters.ThomannDetailsRowData
import com.dauma.grokimkartu.ui.main.adapters.ThomannDetailsStatusData
import com.dauma.grokimkartu.ui.main.adapters.ThomannDetailsUserData
import com.dauma.grokimkartu.viewmodels.main.ThomannDetails
import com.dauma.grokimkartu.viewmodels.main.ThomannDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.sql.Date
import javax.inject.Inject

@AndroidEntryPoint
class ThomannDetailsFragment : Fragment() {
    private val thomannDetailsViewModel by viewModels<ThomannDetailsViewModel>()
    private var isDetailsRecyclerViewSetup: Boolean = false
    @Inject lateinit var utils: Utils
    private val recyclerViewData: MutableList<Any> = mutableListOf()
    private var dialogsManager: DialogsManager? = null
    private var isJoinDialogShown: Boolean = false

    private var _binding: FragmentThomannDetailsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogsManager = context as? DialogsManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentThomannDetailsBinding.inflate(inflater, container, false)
        binding.model = thomannDetailsViewModel
        val view = binding.root
        setupObservers()
        isDetailsRecyclerViewSetup = false

        if (savedInstanceState == null) {
            // TODO: Still reloads on device rotate, probably need to save state instance
            thomannDetailsViewModel.loadDetails()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (isJoinDialogShown == true) {
                dialogsManager?.hideBottomDialog()
                isJoinDialogShown = false
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }

        binding.thomannDetailsHeaderViewElement.setOnBackClick {
            thomannDetailsViewModel.back()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            thomannDetailsViewModel.loadDetails()
        }
        val typedValue = TypedValue()
        context?.theme?.resolveAttribute(R.attr.swipe_to_refresh_progress_spinner_color, typedValue, true)
        binding.swipeRefreshLayout.setColorSchemeColors(typedValue.data)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        dialogsManager = null
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    thomannDetailsViewModel.uiState.collect {
                        binding.thomannDetailsHeaderViewElement.setTitle(getString(R.string.thomann_details_title))
                        it.details?.let { details ->
                            setupRecyclerViewData(details)
                            if (isDetailsRecyclerViewSetup == false) {
                                setupDetailsRecyclerView()
                            } else {
                                binding.thomannDetailsRecyclerView.adapter?.notifyDataSetChanged()
                            }
                            if (binding.swipeRefreshLayout.isRefreshing) {
                                binding.swipeRefreshLayout.isRefreshing = false
                            }
                        }
                        if (it.isJoinStarted) {
                            dialogsManager?.let { manager ->
                                val dialogAmountData = BottomDialogAmountData(
                                    title = getString(R.string.thomann_details_join_dialog_title),
                                    amount = 0,
                                    onSaveClicked = { amount ->
                                        thomannDetailsViewModel.joinClicked(amount) {
                                            manager.showBottomDialogLoading(false)
                                            manager.hideBottomDialog()
                                            utils.keyboardUtils.hideKeyboard(requireView())
                                            isJoinDialogShown = false
                                        }
                                    },
                                    onCancelClicked = { thomannDetailsViewModel.cancelDialogClicked() }
                                )
                                manager.showBottomAmountDialog(dialogAmountData)
                                isJoinDialogShown = true
                            }
                        } else if (it.isEditStarted) {
                            findNavController().navigate(ThomannDetailsFragmentDirections.actionThomannDetailsFragmentToThomannEditFragment(it.details?.id ?: -1))
                        } else if (it.isPostMessageStarted) {
                            findNavController().navigate(ThomannDetailsFragmentDirections.actionThomannDetailsFragmentToConversationFragment(it.details?.id ?: -1))
                        } else {
                            showForm()
                        }
                        if (it.close) {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun showForm() {
        dialogsManager?.hideBottomDialog()
        utils.keyboardUtils.hideKeyboard(requireView())
        isJoinDialogShown = false
    }

    private fun setupDetailsRecyclerView() {
        binding.thomannDetailsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.thomannDetailsRecyclerView.adapter = ThomannDetailsAdapter(
            data = recyclerViewData,
            utils = utils,
            dialogsManager = dialogsManager,
            onItemClicked = { userId ->
                findNavController().navigate(ThomannDetailsFragmentDirections.actionThomannDetailsFragmentToPlayerDetailsFragment(userId))
            },
            onLeaveClicked = { this.thomannDetailsViewModel.quitClicked() },
            onKickClicked = { userId -> this.thomannDetailsViewModel.kickClicked(userId) }
        )
        isDetailsRecyclerViewSetup = true
    }

    private fun setupRecyclerViewData(details: ThomannDetails)  {
        recyclerViewData.clear()
        if (details.photo != null) {
            recyclerViewData.add(ThomannDetailsPhotoData(details.user, details.photo, details.isLocked, {
                val isLocked = details.isLocked
                details.onLockClicked(isLocked == false)
            }))
        }
        recyclerViewData.add(ThomannDetailsStatusData(getString(R.string.thomann_details_status), details.isLocked, {
            val isLocked = details.isLocked
            details.onLockClicked(isLocked == false)
        }))
        recyclerViewData.add(ThomannDetailsRowData(getString(R.string.thomann_details_name), details.user))
        recyclerViewData.add(ThomannDetailsRowData(getString(R.string.thomann_details_city), details.city))

        if (details.createdAt != null) {
            val createdAtFormatted = utils.timeUtils.format(Date(details.createdAt.time), CustomDateTimeFormatPattern.yyyyMMdd)
            recyclerViewData.add(ThomannDetailsRowData(getString(R.string.thomann_details_creation_date), createdAtFormatted))
        }

        if (details.validUntil != null) {
            val validUntilFormatted = utils.timeUtils.format(Date(details.validUntil.time), CustomDateTimeFormatPattern.yyyyMMdd)
            recyclerViewData.add(ThomannDetailsRowData(getString(R.string.thomann_details_valid_until), validUntilFormatted))
        }

        var isUserParticipating = details.isOwner ?: false
        if (details.users != null) {
            for (user in details.users) {
                recyclerViewData.add(ThomannDetailsUserData(user))
                if (user.isCurrentUser == true) {
                    isUserParticipating = true
                }
            }
            if (details.users.count() > 0) {
                recyclerViewData.add(ThomannDetailsRowData(getString(R.string.thomann_details_total_amount), details.totalAmount.toString()))
            }
        }

        if (details.isJoinable == true) {
            recyclerViewData.add(ThomannDetailsButtonData(getString(R.string.thomann_details_join), false, details.onJoinClicked))
        }
        if (details.isOwner == true) {
            recyclerViewData.add(ThomannDetailsButtonData(getString(R.string.thomann_details_edit), false, details.onEditClicked))
            recyclerViewData.add(ThomannDetailsButtonData(getString(R.string.thomann_details_cancel), true, details.onCancelClicked))
        }
        if (isUserParticipating == true) {
            recyclerViewData.add(ThomannDetailsButtonData(getString(R.string.thomann_details_post_message), false, details.onPostMessageClicked))
        }
    }
}