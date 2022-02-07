package com.dauma.grokimkartu.ui.viewelements

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
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
        button.text = text
        val type = attributes.getInt(R.styleable.ButtonViewElement_type, 0)
        attributes.recycle()
        setType(type)
    }

    override fun setEnabled(isEnabled: Boolean) {
        button.isEnabled = isEnabled
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
            // Default
        } else if (type == 1) {
            // Secondary
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.secondaryButtonTextColor, typedValue, true)
            button.setBackgroundResource(R.drawable.secondary_button_ripple)
            button.setTextColor(typedValue.data)
        }
    }
}