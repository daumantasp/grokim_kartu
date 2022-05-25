package com.dauma.grokimkartu.ui

import com.dauma.grokimkartu.general.utils.time.CustomDate
import com.dauma.grokimkartu.ui.main.adapters.CodeValue

interface DialogsManager {
    fun showBottomDialog(data: BottomDialogData)
    fun showBottomDatePickerDialog(data: BottomDialogDatePickerData)
    fun showBottomCodeValueDialog(data: BottomDialogCodeValueData)
    fun setCodeValues(codeValues: List<CodeValue>)
    fun hideBottomDialog()
    fun enableBottomDialogSaveButton(isEnabled: Boolean)
    fun showBottomDialogLoading(show: Boolean)
}

data class BottomDialogData(
    val title: String,
    val value: String,
    val valueLimit: Int?,
    val onSaveClicked: (String) -> Unit,
    val onValueChanged: (String) -> Unit,
    val onCancelClicked: () -> Unit
)

data class BottomDialogDatePickerData (
    val title: String,
    val selectedDate: CustomDate,
    val minDate: CustomDate?,
    val maxDate: CustomDate?,
    val isSaveButtonEnabled: Boolean,
    val onSaveClicked: (CustomDate) -> Unit,
    val onSelectedDateChanged: (CustomDate) -> Unit,
    val onCancelClicked: () -> Unit
)

data class BottomDialogCodeValueData (
    val title: String,
    val codeValues: List<CodeValue>,
    val onCodeValueClicked: (String) -> Unit,
    val onSearchValueChanged: (String) -> Unit,
    val onCancelClicked: () -> Unit
)