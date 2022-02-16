package com.dauma.grokimkartu.ui

interface DialogsManager {
    fun showBottomDialog(data: BottomDialogData)
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

