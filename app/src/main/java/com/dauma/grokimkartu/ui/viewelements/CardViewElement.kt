package com.dauma.grokimkartu.ui.viewelements

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.dauma.grokimkartu.R

class CardViewElement(context: Context, attrs: AttributeSet)
    : FrameLayout(context, attrs) {
    private val cardView: CardView
    private val titleTextView: TextView
    private val descriptionTextView: TextView
    private var onClick: () -> Unit = {}

    init {
        inflate(context, R.layout.element_card, this)

        cardView = findViewById(R.id.cardView)
        titleTextView = findViewById(R.id.titleTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)

        cardView.setOnClickListener { onClick() }

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CardViewElement)
        val title = attributes.getString(R.styleable.CardViewElement_card_title)
        val description = attributes.getString(R.styleable.CardViewElement_card_description)
        val startColor = attributes.getInt(R.styleable.CardViewElement_card_start_color, R.color.white)
        val endColor = attributes.getInt(R.styleable.CardViewElement_card_end_color, R.color.white)
        attributes.recycle()

        setTitle(title ?: "")
        setDescription(description ?: "")
        setBackgroundGradient(startColor, endColor)
    }

    fun setTitle(title: String) {
        titleTextView.setText(title)
    }

    fun setDescription(description: String) {
        descriptionTextView.setText(description)
    }

    fun setBackgroundGradient(startColor: Int, endColor: Int) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(startColor, endColor)
        )
        gradientDrawable.cornerRadius = cardView.radius
        cardView.background = gradientDrawable
    }

    fun setOnClick(onClick: () -> Unit) {
        this.onClick = onClick
    }
}