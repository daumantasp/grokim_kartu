package com.dauma.grokimkartu.ui.viewelements

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.doOnLayout
import com.dauma.grokimkartu.R

class BottomDialogViewElement(context: Context, attrs: AttributeSet)
    : RelativeLayout(context, attrs) {
    private val rootRelativeLayout: RelativeLayout
    private val contentLinearLayout: LinearLayout
    private val titleTextView: TextView
    private val valueEditText: EditText
    private val valueLimitTextView: TextView
    private val saveButton: ButtonViewElement
    private var onValueChanged: (String) -> Unit = {}
    private var valueCharsLimit: Int? = null

    companion object {
        private const val DEFAULT_ANIMATION_DURATION: Long = 300L
    }

    init {
        inflate(context, R.layout.element_bottom_dialog, this)

        rootRelativeLayout = findViewById(R.id.bottomDialogRelativeLayout)
        contentLinearLayout = findViewById(R.id.dialogContentLinearLayout)
        titleTextView = findViewById(R.id.titleTextView)
        valueEditText = findViewById(R.id.valueEditText)
        valueLimitTextView = findViewById(R.id.valueLimitTextView)
        saveButton = findViewById(R.id.saveButton)

        contentLinearLayout.setOnClickListener {}
        valueEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                onValueChanged(p0.toString())
                if (valueLimitTextView.visibility == View.VISIBLE) {
                    valueLimitTextView.text = getValueCharLimitsText()
                }
            }
        })

        // Read More: https://stackoverflow.com/questions/21926644/get-height-and-width-of-a-layout-programmatically
        rootRelativeLayout.doOnLayout { hide(animated = false) }
    }

    fun setTitle(title: String) {
        titleTextView.setText(title)
    }

    fun setEditableValue(value: String) {
        valueEditText.setText(value)
        valueEditText.setSelection(valueEditText.length())
    }

    fun getValue() : String {
        return valueEditText.text.toString()
    }

    fun setSaveButtonEnabled(isEnabled: Boolean) {
        saveButton.isEnabled = isEnabled
    }

    fun showLoading(isLoading: Boolean) {
        saveButton.showAnimation(isLoading)
    }

    fun setOnSaveClick(onClick: () -> Unit) {
        saveButton.setOnClick(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                onClick()
            }
        })
    }

    fun setOnCancelClick(onClick: () -> Unit) {
        rootRelativeLayout.setOnClickListener { onClick() }
    }

    fun setOnValueChanged(onValueChanged: (String) -> Unit) {
        this.onValueChanged = onValueChanged
    }

    fun setValueCharsLimit(limit: Int?) {
        this.valueCharsLimit = limit
        if (limit != null) {
            valueEditText.filters = arrayOf(InputFilter.LengthFilter(limit!!))
            valueLimitTextView.text = getValueCharLimitsText()
            valueLimitTextView.visibility = View.VISIBLE
        } else {
            valueLimitTextView.visibility = View.GONE
            valueEditText.filters = arrayOf()
            valueLimitTextView.text = ""
        }
    }

    fun reset() {
        titleTextView.text = ""
        valueEditText.setText("")
        valueEditText.filters = arrayOf()
        valueLimitTextView.text = ""
        saveButton.isEnabled = true
        onValueChanged = {}
        valueCharsLimit = null
        hide(animated = false)
    }

    // Read more about animations https://www.raywenderlich.com/2785491-android-animation-tutorial-with-kotlin
    fun show(animated: Boolean, onComplete: () -> Unit = {}) {
        val height = contentLinearLayout.height
        if (animated == true) {
            val rootLayoutAlphaAnimator = ObjectAnimator.ofFloat(rootRelativeLayout, "alpha", 0.75f, 1.0f)
            rootLayoutAlphaAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                    rootRelativeLayout.visibility = View.VISIBLE
                }
                override fun onAnimationEnd(p0: Animator?) {
                    onComplete()
                }
                override fun onAnimationCancel(p0: Animator?) {}
                override fun onAnimationRepeat(p0: Animator?) {}
            })
            val contentLayoutTranslationYAnimator = ObjectAnimator.ofFloat(contentLinearLayout, "translationY", height.toFloat(), 0.0f)
            val animatorSet = AnimatorSet()
            animatorSet.play(rootLayoutAlphaAnimator).with(contentLayoutTranslationYAnimator)
            animatorSet.duration = DEFAULT_ANIMATION_DURATION
            animatorSet.start()
        } else {
            contentLinearLayout.translationY = 0.0f
            rootRelativeLayout.visibility = View.VISIBLE
        }
    }

    fun hide(animated: Boolean, onComplete: () -> Unit = {}) {
        val height = contentLinearLayout.height
        if (animated == true) {
            val rootLayoutAlphaAnimator = ObjectAnimator.ofFloat(rootRelativeLayout, "alpha", 1.0f, 0.75f)
            rootLayoutAlphaAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {}
                override fun onAnimationEnd(p0: Animator?) {
                    rootRelativeLayout.visibility = View.GONE
                    onComplete()
                }
                override fun onAnimationCancel(p0: Animator?) {}
                override fun onAnimationRepeat(p0: Animator?) {}
            })
            val contentLayoutTranslationYAnimator = ObjectAnimator.ofFloat(contentLinearLayout, "translationY", 0.0f, height.toFloat())
            val animatorSet = AnimatorSet()
            animatorSet.play(rootLayoutAlphaAnimator).with(contentLayoutTranslationYAnimator)
            animatorSet.duration = DEFAULT_ANIMATION_DURATION
            animatorSet.start()
        } else {
            rootRelativeLayout.visibility = View.GONE
            contentLinearLayout.translationY = height.toFloat()
        }
    }

    private fun getValueCharLimitsText() : String {
        val currentValueCharCount = valueEditText.text.toString().length
        if (valueCharsLimit != null) {
            return "${currentValueCharCount} / ${valueCharsLimit!!}"
        } else {
            return currentValueCharCount.toString()
        }
    }
}