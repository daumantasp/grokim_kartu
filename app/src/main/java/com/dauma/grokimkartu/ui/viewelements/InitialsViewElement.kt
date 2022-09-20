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
        val textSize = attributes.getDimension(R.styleable.InitialsViewElement_text_size, 12f)
        val type = attributes.getInt(R.styleable.InitialsViewElement_initials_type, 0)
        attributes.recycle()
        initialsTextView.textSize = textSize
        setType(type)

        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.initials_background_color_1, typedValue, true)
        colorsPalette.add(typedValue.data)
        context.theme.resolveAttribute(R.attr.initials_background_color_2, typedValue, true)
        colorsPalette.add(typedValue.data)
        context.theme.resolveAttribute(R.attr.initials_background_color_3, typedValue, true)
        colorsPalette.add(typedValue.data)
        context.theme.resolveAttribute(R.attr.initials_background_color_4, typedValue, true)
        colorsPalette.add(typedValue.data)
        context.theme.resolveAttribute(R.attr.initials_background_color_5, typedValue, true)
        colorsPalette.add(typedValue.data)
        context.theme.resolveAttribute(R.attr.initials_background_color_6, typedValue, true)
        colorsPalette.add(typedValue.data)
        context.theme.resolveAttribute(R.attr.initials_background_color_7, typedValue, true)
        colorsPalette.add(typedValue.data)
    }

    fun setInitials(initials: String) {
        initialsTextView.text = initials
        val hashCode = initials.hashCode()
        val colorIdx = hashCode % colorsPalette.count()
        rootFrameLayout.backgroundTintList = ColorStateList.valueOf(colorsPalette[colorIdx])
    }

    private fun setType(type: Int) {
        if (type == 0) {
            // Default, Rectangular background, change programmatically not implemented
        } else if (type == 1) {
            rootFrameLayout.setBackgroundResource(R.drawable.oval_background)
        }
    }
}