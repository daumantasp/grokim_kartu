package com.dauma.grokimkartu.ui.main.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.dauma.grokimkartu.R

class ProfileEditDialog(
    context: Context,
    themeId: Int,
    private val data: ProfileEditDialogData,
)
    : Dialog(context, themeId) {
    private var profileEditDialogLinearLayout: LinearLayout? = null
    private var titleTextView: TextView? = null
    private var editValueTextView: TextView? = null
    var onSaveClicked: () -> Unit = {}
    var onCancelClicked: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.profile_edit_dialog)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window?.setDimAmount(0.5f)
        window?.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window?.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

        profileEditDialogLinearLayout = findViewById(R.id.profileEditDialogLinearLayout)
        titleTextView = findViewById(R.id.titleTextView)
        editValueTextView = findViewById(R.id.editValueTextView)

        titleTextView?.setText(data.title)
        // https://stackoverflow.com/questions/12056054/android-how-to-make-textview-editable
        editValueTextView?.setText(data.value)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (MotionEvent.ACTION_DOWN == event.action) {
            val dialogBounds = Rect()
            profileEditDialogLinearLayout?.getHitRect(dialogBounds)
//            window?.decorView?.getHitRect(dialogBounds)
            if (dialogBounds.contains(event.getX().toInt(), event.getY().toInt()) == false) {
                onCancelClicked()
                return true
            }
        }

        return super.onTouchEvent(event)
    }
}

data class ProfileEditDialogData(
    val title: String,
    val value: String
)