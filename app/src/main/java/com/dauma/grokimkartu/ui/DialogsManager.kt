package com.dauma.grokimkartu.ui

import com.dauma.grokimkartu.general.utils.time.CustomDateTime
import com.dauma.grokimkartu.general.CodeValue

interface DialogsManager {
    fun showBottomDialog(data: BottomDialogData)
    fun showBottomDatePickerDialog(data: BottomDialogDatePickerData)
    fun showBottomCodeValueDialog(data: BottomDialogCodeValueData)
    fun showBottomAmountDialog(data: BottomDialogAmountData)
    fun setCodeValues(codeValues: List<CodeValue>)
    fun hideBottomDialog()
    fun enableBottomDialogSaveButton(isEnabled: Boolean)
    fun showBottomDialogLoading(show: Boolean)
    fun showBlockingDialog(show: Boolean)
    fun showYesNoDialog(data: YesNoDialogData)
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
    val selectedDate: CustomDateTime,
    val minDate: CustomDateTime?,
    val maxDate: CustomDateTime?,
    val isSaveButtonEnabled: Boolean,
    val onSaveClicked: (CustomDateTime) -> Unit,
    val onSelectedDateChanged: (CustomDateTime) -> Unit,
    val onCancelClicked: () -> Unit
)

data class BottomDialogCodeValueData (
    val title: String,
    val codeValues: List<CodeValue>,
    val onCodeValueClicked: (String) -> Unit,
    val onSearchValueChanged: (String) -> Unit,
    val onCancelClicked: () -> Unit
)

data class BottomDialogAmountData(
    val title: String,
    val amount: Int,
    val onSaveClicked: (Int) -> Unit,
    val onCancelClicked: () -> Unit
)

data class YesNoDialogData(
    val text: String,
    val positiveText: String,
    val negativeText: String,
    val cancelable: Boolean,
    val onPositiveButtonClick: () -> Unit,
    val onNegativeButtonClick: () -> Unit = {}
)