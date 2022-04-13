package com.dauma.grokimkartu.ui.main

import android.content.Context
import android.os.Bundle
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
            this.binding.thomannDetailsHeaderViewElement.setTitle("${it.name} Thomann")
            setupRecyclerViewData(it)
            if (isDetailsRecyclerViewSetup == false) {
                setupDetailsRecyclerView()
            } else {
                binding.thomannDetailsRecyclerView.adapter?.notifyDataSetChanged()
            }
        }
        thomannDetailsViewModel.userDetails.observe(viewLifecycleOwner, EventObserver {
            val args = Bundle()
            args.putString("userId", it)
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
            args.putString("thomannId", thomannId)
            this.findNavController().navigate(R.id.action_thomannDetailsFragment_to_thomannEditFragment, args)
        })
    }

    private fun setupDetailsRecyclerView() {
        binding.thomannDetailsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.thomannDetailsRecyclerView.adapter = ThomannDetailsAdapter(
            context = requireContext(),
            data = recyclerViewData,
            utils = utils,
            onItemClicked = { this.thomannDetailsViewModel.userClicked(it) },
            onLeaveClicked = { this.thomannDetailsViewModel.leaveClicked() },
            onKickClicked = { userId -> this.thomannDetailsViewModel.kickClicked(userId) }
        )
        isDetailsRecyclerViewSetup = true
    }

    private fun setupRecyclerViewData(details: ThomannDetails)  {
        recyclerViewData.clear()
        if (details.photo != null) {
            recyclerViewData.add(ThomannDetailsListPhotoData(details.name, details.photo, details.isLocked, {
                val isLocked = details.isLocked
                details.onLockButtonClick(isLocked == false)
            }))
        }
        recyclerViewData.add(ThomannDetailsListStatusRowData(getString(R.string.thomann_details_status), details.isLocked, {
            val isLocked = details.isLocked
            details.onLockButtonClick(isLocked == false)
        }))
        recyclerViewData.add(ThomannDetailsListRowData(getString(R.string.thomann_details_name), details.name))
        recyclerViewData.add(ThomannDetailsListRowData(getString(R.string.thomann_details_city), details.city))
        recyclerViewData.add(ThomannDetailsListRowData(getString(R.string.thomann_details_creation_date), details.creationDate))
        recyclerViewData.add(ThomannDetailsListRowData(getString(R.string.thomann_details_valid_until), details.validUntilDate))
        for (user in details.users) {
            recyclerViewData.add(ThomannDetailsListUserData(user))
        }
        if (details.users.count() > 0) {
            recyclerViewData.add(ThomannDetailsListRowData(getString(R.string.thomann_details_total_amount), details.totalAmount.toString()))
        }
        if (details.isJoinable) {
            recyclerViewData.add(ThomannDetailsListButtonData(getString(R.string.thomann_details_join), details.onJoinButtonClick))
        }
        if (details.isEditable) {
            recyclerViewData.add(ThomannDetailsListButtonData(getString(R.string.thomann_details_edit), details.onEditButtonClick))
        }
        if (details.isCancelable) {
            recyclerViewData.add(ThomannDetailsListButtonData(getString(R.string.thomann_details_cancel), details.onCancelButtonClick))
        }
    }
}