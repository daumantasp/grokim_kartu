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
import com.dauma.grokimkartu.general.utils.dialog.YesNoDialogData
import com.dauma.grokimkartu.general.utils.time.CustomDateTime
import com.dauma.grokimkartu.general.utils.time.CustomDateTimeFormatPattern
import com.dauma.grokimkartu.ui.BottomDialogCodeValueData
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dialogsManager = null
    }

    private fun setupOnClickListeners() {
        binding.thomannEditHeaderViewElement.setOnBackClick {
            thomannEditViewModel.backClicked(false)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            thomannEditViewModel.backClicked(false)
        }

        binding.cityInputEditText.setOnClickListener {
            thomannEditViewModel.cityClicked()
        }

        binding.validUntilInputEditText.setOnClickListener {
            thomannEditViewModel.validUntilClicked()
        }

        binding.saveThomannButton.setOnClick(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                binding.saveThomannButton.showAnimation(true)
                thomannEditViewModel.saveChanges {
                    binding.saveThomannButton.showAnimation(false)
                }
            }
        })
    }

    private fun setupObservers() {
        thomannEditViewModel.navigateBackConfirmation.observe(viewLifecycleOwner, EventObserver {
            if (isDialogShown == true) {
                dialogsManager?.hideBottomDialog()
                isDialogShown = false
            } else {
                utils.dialogUtils.showYesNoDialog(requireContext(), YesNoDialogData(
                    getString(R.string.thomann_edit_navigate_back_confirmation_text),
                    getString(R.string.thomann_edit_navigate_back_confirmation_positive),
                    getString(R.string.thomann_edit_navigate_back_confirmation_negative),
                    true,
                    { thomannEditViewModel.backClicked(true) }
                ))
            }
        })
        thomannEditViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            if (isDialogShown == true) {
                dialogsManager?.hideBottomDialog()
                isDialogShown = false
            } else {
                this.findNavController().popBackStack()
            }
        })
        thomannEditViewModel.city.observe(viewLifecycleOwner, EventObserver {
            this.isDialogShown = true
            this.dialogsManager?.let { manager ->
                val pickableCitiesAsCodeValues = thomannEditViewModel
                    .thomannEditForm()
                    .filteredPickableCities
                    .map { pc -> pc.toCodeValue() }

                manager.showBottomCodeValueDialog(BottomDialogCodeValueData(
                    title = getString(R.string.thomann_edit_city),
                    codeValues = pickableCitiesAsCodeValues,
                    onSearchValueChanged = { value ->
                        this.thomannEditViewModel.searchCity(value) {
                            val pickableCitiesAsCodeValues = thomannEditViewModel
                                .thomannEditForm()
                                .filteredPickableCities
                                .map { pc -> pc.toCodeValue() }
                            manager.setCodeValues(pickableCitiesAsCodeValues)
                        }
                    },
                    onCodeValueClicked = { code ->
                        val id = code.toIntOrNull()
                        if (id != null) {
                            this.thomannEditViewModel.citySelected(id)
                            manager.hideBottomDialog()
                            this.isDialogShown = false
                        }
                    },
                    onCancelClicked = {}
                ))
            }
        })
        thomannEditViewModel.validUntil.observe(viewLifecycleOwner, EventObserver {
            this.isDialogShown = true
            val currentDate = it[0] as CustomDateTime
            val minDate = it[1] as CustomDateTime
            val maxDate = it[2] as CustomDateTime
            val isSaveButtonEnabled = it[3] as Boolean
            this.dialogsManager?.let { manager ->
                manager.showBottomDatePickerDialog(BottomDialogDatePickerData(
                    title = getString(R.string.thomann_edit_valid_until),
                    selectedDate = currentDate,
                    minDate = minDate,
                    maxDate = maxDate,
                    isSaveButtonEnabled = isSaveButtonEnabled,
                    onSaveClicked = { selectedDate ->
                        val formattedDate = this.utils.timeUtils.format(selectedDate, CustomDateTimeFormatPattern.yyyyMMdd)
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
    }
}