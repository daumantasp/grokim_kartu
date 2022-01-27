package com.dauma.grokimkartu.ui.viewelements

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import android.widget.TextView
import com.dauma.grokimkartu.R

class InitialsViewElement(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private val rootFrameLayout: FrameLayout
    private val initialsTextView: TextView
    private val colorsPalette: MutableList<Int> = mutableListOf()

    init {
        inflate(context, R.layout.element_initials, this)

        rootFrameLayout = findViewById(R.id.rootFrameLayout)
        initialsTextView = findViewById(R.id.initialsTextView)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.InitialsViewElement)
        val textSize = attributes.getDimension(R.styleable.InitialsViewElement_textSize, 12f)
        initialsTextView.textSize = textSize
        attributes.recycle()

        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.initialsBackgroundColor1, typedValue, true)
        colorsPalette.add(typedValue.data)
        context.theme.resolveAttribute(R.attr.initialsBackgroundColor2, typedValue, true)
        colorsPalette.add(typedValue.data)
        context.theme.resolveAttribute(R.attr.initialsBackgroundColor3, typedValue, true)
        colorsPalette.add(typedValue.data)
        context.theme.resolveAttribute(R.attr.initialsBackgroundColor4, typedValue, true)
        colorsPalette.add(typedValue.data)
        context.theme.resolveAttribute(R.attr.initialsBackgroundColor5, typedValue, true)
        colorsPalette.add(typedValue.data)
        context.theme.resolveAttribute(R.attr.initialsBackgroundColor6, typedValue, true)
        colorsPalette.add(typedValue.data)
        context.theme.resolveAttribute(R.attr.initialsBackgroundColor7, typedValue, true)
        colorsPalette.add(typedValue.data)
    }

    fun setInitials(initials: String) {
        initialsTextView.text = initials
        val hashCode = initials.hashCode()
        val colorIdx = hashCode % colorsPalette.count()
        rootFrameLayout.backgroundTintList = ColorStateList.valueOf(colorsPalette[colorIdx])
    }
}