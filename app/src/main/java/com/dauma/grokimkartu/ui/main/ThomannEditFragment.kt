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
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.databinding.FragmentThomannEditBinding
import com.dauma.grokimkartu.general.event.EventObserver
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDate
import com.dauma.grokimkartu.ui.BottomDialogDatePickerData
import com.dauma.grokimkartu.ui.DialogsManager
import com.dauma.grokimkartu.viewmodels.main.ThomannEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ThomannEditFragment : Fragment() {
    private val thomannEditViewModel by viewModels<ThomannEditViewModel>()
    private var dialogsManager: DialogsManager? = null
    @Inject lateinit var utils: Utils
    private var isDialogShown: Boolean = false

    private var _binding: FragmentThomannEditBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private var TAG = "ThomannEditFragment"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogsManager = context as? DialogsManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThomannEditBinding.inflate(inflater, container, false)
        binding.model = thomannEditViewModel
        val view = binding.root
        setupObservers()
        setupOnClickListeners()

        thomannEditViewModel.viewIsReady()
        return view
    }

    private fun setupOnClickListeners() {
        binding.thomannEditHeaderViewElement.setOnBackClick {
            thomannEditViewModel.backClicked()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            thomannEditViewModel.backClicked()
        }

        binding.validUntilInputEditText.setOnClickListener {
            thomannEditViewModel.validUntilClicked()
        }
    }

    private fun setupObservers() {
        thomannEditViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            if (isDialogShown == true) {
                dialogsManager?.hideBottomDialog()
                isDialogShown = false
            } else {
                this.findNavController().popBackStack()
            }
        })
        thomannEditViewModel.validUntil.observe(viewLifecycleOwner, EventObserver {
            val currentDate = it[0] as CustomDate
            val minDate = it[1] as CustomDate
            val maxDate = it[2] as CustomDate
            val isSaveButtonEnabled = it[3] as Boolean
            this.dialogsManager?.let { manager ->
                this.isDialogShown = true
                manager.showBottomDatePickerDialog(BottomDialogDatePickerData(
                    title = getString(R.string.thomann_edit_valid_until),
                    selectedDate = currentDate,
                    minDate = minDate,
                    maxDate = maxDate,
                    isSaveButtonEnabled = isSaveButtonEnabled,
                    onSaveClicked = { selectedDate ->
                        val formattedDate = this.utils.timeUtils.format(selectedDate)
                        thomannEditViewModel.thomannEditForm().validUntil = formattedDate
                        manager.hideBottomDialog()
                        this.isDialogShown = false
                    },
                    onSelectedDateChanged = { selectedDate ->
                        manager.enableBottomDialogSaveButton(true)
                    },
                    onCancelClicked = {
                        manager.hideBottomDialog()
                        this.isDialogShown = false
                    }
                ))
            }
        })
        thomannEditViewModel.thomannEditForm().getFormFields().observe(viewLifecycleOwner) {
            this.thomannEditViewModel.saveClicked(it[0], it[1])
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dialogsManager = null
    }
}