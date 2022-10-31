package com.dauma.grokimkartu.general.utils.dialog

data class YesNoDialogData(
    val text: String,
    val positiveText: String,
    val negativeText: String,
    val cancelable: Boolean,
    val onPositiveButtonClick: () -> Unit,
    val onNegativeButtonClick: () -> Unit = {}
)