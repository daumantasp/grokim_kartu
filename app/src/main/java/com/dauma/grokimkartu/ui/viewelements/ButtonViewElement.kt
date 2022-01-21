package com.dauma.grokimkartu.ui.viewelements

import android.content.Context
import android.util.AttributeSet
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
        attributes.recycle()
    }

    override fun setEnabled(isEnabled: Boolean) {
        button.isEnabled = isEnabled
    }

    fun onClick(action: () -> Unit) {
        button.setOnClickListener { action() }
    }

    fun showAnimation(show: Boolean) {
        button.text = if (show) "" else text
        spinner.showAnimation(show)
    }
}