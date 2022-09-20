package com.dauma.grokimkartu.ui.viewelements

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.Button
import android.widget.FrameLayout
import com.dauma.grokimkartu.R

class ButtonViewElement(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private val button: Button
    private val spinner: SpinnerViewElement
    private val text: String

    init {
        inflate(context, R.layout.element_button, this)

        button = findViewById(R.id.button)
        spinner = findViewById(R.id.spinnerViewElement)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ButtonViewElement)
        text = attributes.getString(R.styleable.ButtonViewElement_text) ?: ""
        setText(text)
        val type = attributes.getInt(R.styleable.ButtonViewElement_type, 0)
        attributes.recycle()
        setType(type)
    }

    override fun setEnabled(isEnabled: Boolean) {
        button.isEnabled = isEnabled
    }

    fun setText(text: String) {
        button.text = text
    }

    // https://stackoverflow.com/questions/53443784/android-data-binding-missing-return-statement-in-generated-code-when-calling-cu/53475016
    fun setOnClick(listener: OnClickListener?) {
        button.setOnClickListener {
            listener?.onClick(button)
        }
    }

    fun showAnimation(show: Boolean) {
        button.text = if (show) "" else text
        spinner.showAnimation(show)
    }

    private fun setType(type: Int) {
        if (type == 0) {
            // Default, PrimaryInAuth, change programmatically not implemented
        } else if (type == 1) {
            // Primary
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.primary_button_title_color, typedValue, true)
            button.setTextColor(typedValue.data)
            button.setBackgroundResource(R.drawable.primary_button_ripple)
        } else if (type == 2) {
            // Secondary
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.secondary_button_title_color, typedValue, true)
            button.setTextColor(typedValue.data)
            button.setBackgroundResource(R.drawable.secondary_button_ripple)
        }
    }
}