package com.dauma.grokimkartu.general.utils.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

class DialogUtilsImpl : DialogUtils {
    override fun showYesNoDialog(context: Context, data: YesNoDialogData) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        val dialogClickListener = DialogInterface.OnClickListener { dialogInterface, which ->
            when(which) {
                DialogInterface.BUTTON_POSITIVE -> data.onPositiveButtonClick()
                DialogInterface.BUTTON_NEGATIVE -> data.onNegativeButtonClick()
            }
            dialogInterface.dismiss()
        }
        alertDialogBuilder.setMessage(data.text)
            .setCancelable(data.cancelable)
            .setPositiveButton(data.positiveText, dialogClickListener)
            .setNegativeButton(data.negativeText, dialogClickListener)
            .show()
    }
}