package com.dauma.grokimkartu.ui.main

import android.content.Context
import android.os.Build
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
import com.dauma.grokimkartu.ui.BottomDialogDatePickerData
import com.dauma.grokimkartu.ui.DatePickerDate
import com.dauma.grokimkartu.ui.DialogsManager
import com.dauma.grokimkartu.viewmodels.main.ThomannEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.*

@AndroidEntryPoint
class ThomannEditFragment : Fragment() {
    private val thomannEditViewModel by viewModels<ThomannEditViewModel>()
    private var dialogsManager: DialogsManager? = null

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

        binding.thomannEditHeaderViewElement.setOnBackClick {
            thomannEditViewModel.backClicked()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            thomannEditViewModel.backClicked()
        }

        binding.validUntilInputEditText.setOnClickListener {
            thomannEditViewModel.validUntilClicked()
        }

        thomannEditViewModel.viewIsReady()
        return view
    }

    private fun setupObservers() {
        thomannEditViewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
        thomannEditViewModel.validUndtil.observe(viewLifecycleOwner, EventObserver {
            val currentDate: DatePickerDate
            val minDate: DatePickerDate
            val maxDate: DatePickerDate
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val currentLocalDateTime = LocalDateTime.now()
                val minLocalDateTime = currentLocalDateTime.plusDays(1L)
                val maxLocalDateTime = currentLocalDateTime.plusYears(1L)
                currentDate = DatePickerDate(
                    currentLocalDateTime.year,
                    currentLocalDateTime.monthValue,
                    currentLocalDateTime.dayOfMonth)
                minDate = DatePickerDate(
                    minLocalDateTime.year,
                    minLocalDateTime.monthValue,
                    minLocalDateTime.dayOfMonth)
                maxDate = DatePickerDate(
                    maxLocalDateTime.year,
                    maxLocalDateTime.monthValue,
                    maxLocalDateTime.dayOfMonth)
            } else {
                val date = Date()
                val calendar = Calendar.getInstance()
                calendar.time = date
                currentDate = DatePickerDate(date.year, date.month + 1, date.day)
                calendar.add(Calendar.DATE, 1)
                minDate = DatePickerDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                calendar.time = date
                calendar.add(Calendar.YEAR, 1)
                maxDate = DatePickerDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            }

            this.dialogsManager?.let { manager ->
                manager.showBottomDatePickerDialog(BottomDialogDatePickerData(
                    title = getString(R.string.thomann_edit_valid_until),
                    selectedDate = currentDate,
                    minDate = minDate,
                    maxDate = maxDate,
                    onSaveClicked = { selectedDate ->
                        // temporary
                        val dateString = "${selectedDate.year}-${selectedDate.month}-${selectedDate.day}"
                        thomannEditViewModel.thomannEditForm().validUntil = dateString
                        manager.hideBottomDialog()
                    },
                    onSelectedDateChanged = { selectedDate ->
                        manager.enableBottomDialogSaveButton(true)
                    },
                    onCancelClicked = { manager.hideBottomDialog() }
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