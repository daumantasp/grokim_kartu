package com.dauma.grokimkartu.ui.viewelements

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.dauma.grokimkartu.R

class RowViewElement(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private val rowConstraintLayout: ConstraintLayout
    private val titleTextView: TextView
    private val valueTextView: TextView
    private val arrowImageView: ImageView

    init {
        inflate(context, R.layout.element_row, this)

        rowConstraintLayout = findViewById(R.id.rowConstraintLayout)
        titleTextView = findViewById(R.id.titleTextView)
        valueTextView = findViewById(R.id.valueTextView)
        arrowImageView = findViewById(R.id.arrowImageView)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.RowViewElement)
        val title = attributes.getString(R.styleable.RowViewElement_rowTitle)
        val value = attributes.getString(R.styleable.RowViewElement_rowValue)
        val isArrowVisible = attributes.getBoolean(R.styleable.RowViewElement_rowShowArrow, false)
        attributes.recycle()

        titleTextView.setText(title)
        valueTextView.setText(value)

        showArrow(isArrowVisible)
    }

    private fun showArrow(show: Boolean) {
        if (show) {
            arrowImageView.visibility = View.VISIBLE
            val constraintSet = ConstraintSet()
            constraintSet.clone(rowConstraintLayout)
            constraintSet.connect(R.id.valueTextView, ConstraintSet.END, R.id.arrowImageView, ConstraintSet.START)
            constraintSet.applyTo(rowConstraintLayout)
        }
    }
}