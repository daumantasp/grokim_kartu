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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.viewmodels.main.ThomannDetailsViewModel
import com.dauma.grokimkartu.databinding.FragmentThomannDetailsBinding
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.ui.BottomDialogData
import com.dauma.grokimkartu.ui.DialogsManager
import com.dauma.grokimkartu.ui.main.adapters.*
import com.dauma.grokimkartu.viewmodels.main.ThomannDetails
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Date
import javax.inject.Inject

@AndroidEntryPoint
class ThomannDetailsFragment : Fragment() {
    private val thomannDetailsViewModel by viewModels<ThomannDetailsViewModel>()
    private var isDetailsRecyclerViewSetup: Boolean = false
    @Inject lateinit var utils: Utils
    private val recyclerViewData: MutableList<ThomannDetailsListData> = mutableListOf()
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
            thomannDetailsViewModel.backClicked()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            thomannDetailsViewModel.loadDetails()
        }
        val typedValue = TypedValue()
        context?.theme?.resolveAttribute(R.attr.swipeRefreshProgressSpinnerColor, typedValue, true)
        binding.swipeRefreshLayout.setColorSchemeColors(typedValue.data)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dialogsManager = null
    }

    private fun setupObservers() {
        thomannDetailsViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
        thomannDetailsViewModel.detailsLoaded.observe(viewLifecycleOwner) {
            this.binding.thomannDetailsHeaderViewElement.setTitle("${it.user} Thomann")
            setupRecyclerViewData(it)
            if (isDetailsRecyclerViewSetup == false) {
                setupDetailsRecyclerView()
            } else {
                binding.thomannDetailsRecyclerView.adapter?.notifyDataSetChanged()
            }
            if (binding.swipeRefreshLayout.isRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
        thomannDetailsViewModel.userDetails.observe(viewLifecycleOwner, EventObserver { userId ->
            val args = Bundle()
            args.putInt("userId", userId)
            this.findNavController().navigate(R.id.action_thomannDetailsFragment_to_playerDetailsFragment, args)
        })
        thomannDetailsViewModel.join.observe(viewLifecycleOwner, EventObserver {
            this.dialogsManager?.let { manager ->
                val dialogData = BottomDialogData(
                    title = getString(R.string.thomann_details_join_dialog_title),
                    value = "",
                    valueLimit = null,
                    onSaveClicked = { value ->
                        manager.showBottomDialogLoading(true)
                        val valueAsDouble = value.toDoubleOrNull()
                        if (valueAsDouble != null) {
                            this.thomannDetailsViewModel.joinClicked(valueAsDouble) {
                                manager.showBottomDialogLoading(false)
                                manager.hideBottomDialog()
                                this.utils.keyboardUtils.hideKeyboardFrom(requireActivity(), requireView())
                            }
                        } else {
                            manager.showBottomDialogLoading(false)
                        }
                    },
                    onValueChanged = { value ->
                        manager.enableBottomDialogSaveButton(value.length > 0)
                    },
                    onCancelClicked = {
                        manager.hideBottomDialog()
                        utils.keyboardUtils.hideKeyboardFrom(requireActivity(), requireView())
                    }
                )
                manager.showBottomDialog(dialogData)
                isJoinDialogShown = true
            }
        })
        thomannDetailsViewModel.edit.observe(viewLifecycleOwner, EventObserver { thomannId ->
            val args = Bundle()
            args.putInt("thomannId", thomannId)
            this.findNavController().navigate(R.id.action_thomannDetailsFragment_to_thomannEditFragment, args)
        })
    }

    private fun setupDetailsRecyclerView() {
        binding.thomannDetailsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.thomannDetailsRecyclerView.adapter = ThomannDetailsAdapter(
            context = requireContext(),
            data = recyclerViewData,
            utils = utils,
            onItemClicked = { userId -> this.thomannDetailsViewModel.userClicked(userId) },
            onLeaveClicked = { this.thomannDetailsViewModel.quitClicked() },
            onKickClicked = { userId -> this.thomannDetailsViewModel.kickClicked(userId) }
        )
        isDetailsRecyclerViewSetup = true
    }

    private fun setupRecyclerViewData(details: ThomannDetails)  {
        recyclerViewData.clear()
        if (details.photo != null) {
            recyclerViewData.add(ThomannDetailsListPhotoData(details.user, details.photo, details.isLocked, {
                val isLocked = details.isLocked
                details.onLockClicked(isLocked == false)
            }))
        }
        recyclerViewData.add(ThomannDetailsListStatusRowData(getString(R.string.thomann_details_status), details.isLocked, {
            val isLocked = details.isLocked
            details.onLockClicked(isLocked == false)
        }))
        recyclerViewData.add(ThomannDetailsListRowData(getString(R.string.thomann_details_name), details.user))
        recyclerViewData.add(ThomannDetailsListRowData(getString(R.string.thomann_details_city), details.city))

        if (details.createdAt != null) {
            val createdAtFormatted = utils.timeUtils.format(Date(details.createdAt.time))
            recyclerViewData.add(ThomannDetailsListRowData(getString(R.string.thomann_details_creation_date), createdAtFormatted))
        }

        if (details.validUntil != null) {
            val validUntilFormatted = utils.timeUtils.format(Date(details.validUntil.time))
            recyclerViewData.add(ThomannDetailsListRowData(getString(R.string.thomann_details_valid_until), validUntilFormatted))
        }

        if (details.users != null) {
            for (user in details.users) {
                recyclerViewData.add(ThomannDetailsListUserData(user))
            }
            if (details.users.count() > 0) {
                recyclerViewData.add(ThomannDetailsListRowData(getString(R.string.thomann_details_total_amount), details.totalAmount.toString()))
            }
        }

        if (details.isJoinable == true) {
            recyclerViewData.add(ThomannDetailsListButtonData(getString(R.string.thomann_details_join), details.onJoinClicked))
        }
        if (details.isOwner == true) {
            recyclerViewData.add(ThomannDetailsListButtonData(getString(R.string.thomann_details_edit), details.onEditClicked))
            recyclerViewData.add(ThomannDetailsListButtonData(getString(R.string.thomann_details_cancel), details.onCancelClicked))
        }
    }
}