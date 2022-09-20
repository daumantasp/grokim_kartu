package com.dauma.grokimkartu.ui.viewelements

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.dauma.grokimkartu.R

class LanguageViewElement(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private val languageConstraintLayout: ConstraintLayout
    private val flagImageView: ImageView
    private val languageTextView: TextView
    private val selectedImageView: ImageView

    init {
        inflate(context, R.layout.element_language, this)

        languageConstraintLayout = findViewById(R.id.languageConstraintLayout)
        flagImageView = findViewById(R.id.flagImageView)
        languageTextView = findViewById(R.id.languageTextView)
        selectedImageView = findViewById(R.id.selectedImageView)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.LanguageViewElement)
        val languageCode = attributes.getInt(R.styleable.LanguageViewElement_language_code, 0)
        val isSelected = attributes.getBoolean(R.styleable.LanguageViewElement_language_is_selected, false)
        attributes.recycle()

        setLanguage(languageCode)
        setSelected(isSelected)
    }

    fun setLanguage(code: Int) {
        when (code) {
            0 -> {
                languageTextView.setText(resources.getString(R.string.language_en))
                flagImageView.setImageResource(R.drawable.ic_language_uk)
            }
            else -> {
                languageTextView.setText(resources.getString(R.string.language_lt))
                flagImageView.setImageResource(R.drawable.ic_language_lt)
            }
        }
    }

    override fun setSelected(isSelected: Boolean) {
        selectedImageView.visibility = if (isSelected) View.VISIBLE else View.GONE
    }

    fun setOnClick(listener: OnClickListener?) {
        languageConstraintLayout.setOnClickListener {
            listener?.onClick(languageConstraintLayout)
        }
    }
}