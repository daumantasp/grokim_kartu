package com.dauma.grokimkartu.ui

interface DialogsManager {
    fun showBottomDialog(data: BottomDialogData)
    fun showBottomDatePickerDialog(data: BottomDialogDatePickerData)
    fun hideBottomDialog()
    fun enableBottomDialogSaveButton(isEnabled: Boolean)
    fun showBottomDialogLoading(show: Boolean)
}

data class BottomDialogData(
    val title: String,
    val value: String,
    val valueLimit: Int,
    val onSaveClicked: (String) -> Unit,
    val onValueChanged: (String) -> Unit,
    val onCancelClicked: () -> Unit
)

data class BottomDialogDatePickerData (
    val title: String,
    val selectedDate: DatePickerDate,
    val minDate: DatePickerDate?,
    val maxDate: DatePickerDate?,
    val onSaveClicked: (DatePickerDate) -> Unit,
    val onSelectedDateChanged: (DatePickerDate) -> Unit,
    val onCancelClicked: () -> Unit
)

data class DatePickerDate (
    val year: Int,
    val month: Int,
    val day: Int
)