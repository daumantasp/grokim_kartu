package com.dauma.grokimkartu.ui.viewelements

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.dauma.grokimkartu.R

class RowViewElement(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private val rowConstraintLayout: ConstraintLayout
    private val titleTextView: TextView
    private val valueTextView: TextView
    private val iconImageView: ImageView
    private val switch: SwitchCompat
    private var onSwitchChecked: () -> Unit = {}

    init {
        inflate(context, R.layout.element_row, this)

        rowConstraintLayout = findViewById(R.id.row_constraint_layout)
        titleTextView = findViewById(R.id.title_text_view)
        valueTextView = findViewById(R.id.value_text_view)
        iconImageView = findViewById(R.id.icon_image_view)
        switch = findViewById(R.id.row_switch)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.RowViewElement)
        val title = attributes.getString(R.styleable.RowViewElement_row_title)
        val isArrowVisible = attributes.getBoolean(R.styleable.RowViewElement_row_show_icon, false)
        val border = attributes.getInt(R.styleable.RowViewElement_row_borders, 0)
        val isSwitchVisible = attributes.getBoolean(R.styleable.RowViewElement_row_show_switch, false)
        val isMultiline = attributes.getBoolean(R.styleable.RowViewElement_row_multiline, false)
        val customIcon = attributes.getDrawable(R.styleable.RowViewElement_row_custom_icon)
        val customIconWidth = attributes.getDimension(R.styleable.RowViewElement_row_icon_width, 0f)
        val customIconHeight = attributes.getDimension(R.styleable.RowViewElement_row_icon_height, 0f)
        val valueColor = attributes.getInt(R.styleable.RowViewElement_row_value_color, 0)
        val iconTintColor = attributes.getInt(R.styleable.RowViewElement_row_icon_tint_color, 0)
        attributes.recycle()

        setTitle(title ?: "")
        showIcon(isArrowVisible)
        setBorder(border)
        showSwitch(isSwitchVisible)
        setMultiline(isMultiline)
        setCustomIconIfNeeded(customIcon)
        setIconSizeIfNeeded(customIconWidth, customIconHeight)
        setValueColor(valueColor)
        setIconTintColor(iconTintColor)
    }

    companion object {
        // For two way binding read more at https://stackoverflow.com/questions/60679957/two-way-databinding-custom-property-on-custom-view
        @BindingAdapter("switchChecked")
        @JvmStatic fun setSwitchChecked(rowView: RowViewElement, isChecked: Boolean) {
            rowView.switch.isChecked = isChecked
        }

        @InverseBindingAdapter(attribute = "switchChecked")
        @JvmStatic fun getSwitchChecked(rowView: RowViewElement) : Boolean {
            return rowView.switch.isChecked
        }

        @BindingAdapter("switchCheckedAttrChanged")
        @JvmStatic fun setListener(rowView: RowViewElement, listener: InverseBindingListener) {
            // Do not fully understand yet how should I implement it but:
            // setListener is called initially and only once. If you do not implement it,
            // getSwitchChecked is never called and model (settingsForm) is not updated when switch check changes
            // listener.onChange() forces to call getSwitchChecked and update the model.
            // So what I did here:
            // when initial setListener is called, I add switch checked changed listener;
            // now every time switch check changes, listener.onChanged() is called and model (settingsForm) is updated
            // after model isUpdated, rowView.onSwitchChecked() is called
            rowView.switch.setOnCheckedChangeListener { switch, isChanged ->
                listener.onChange()
                rowView.onSwitchChecked()
            }
        }
    }

    fun setTitle(title: String) {
        titleTextView.setText(title)
    }

    fun setValue(value: String) {
        valueTextView.setText(value)
    }

    fun setOnClick(listener: OnClickListener?) {
        rowConstraintLayout.setOnClickListener {
            listener?.onClick(rowConstraintLayout)
        }
    }

    fun setOnSwitchChecked(onSwitchChecked: () -> Unit) {
        this.onSwitchChecked = onSwitchChecked
    }

    fun setSwitchEnabled(enabled: Boolean) {
        switch.isEnabled = enabled
    }

    fun setValueColor(color: Int) {
        if (color != 0) {
            valueTextView.setTextColor(color)
        }
    }

    fun showIcon(show: Boolean) {
        if (show) {
            iconImageView.visibility = View.VISIBLE
            val constraintSet = ConstraintSet()
            constraintSet.clone(rowConstraintLayout)
            constraintSet.connect(R.id.value_text_view, ConstraintSet.END, R.id.icon_image_view, ConstraintSet.START)
            constraintSet.applyTo(rowConstraintLayout)
        } else {
            iconImageView.visibility = View.GONE
            val constraintSet = ConstraintSet()
            constraintSet.clone(rowConstraintLayout)
            constraintSet.connect(R.id.value_text_view, ConstraintSet.END, R.id.row_constraint_layout, ConstraintSet.END)
            constraintSet.applyTo(rowConstraintLayout)
        }
    }

    fun setCustomIconIfNeeded(icon: Drawable?) {
        icon?.let {
            iconImageView.background = it
            iconImageView.rotation = 0.0f
        }
    }

    fun setIconSizeIfNeeded(width: Float, height: Float) {
        if (width > 0.0 || height > 0.0) {
            iconImageView.layoutParams.width = width.toInt()
            iconImageView.layoutParams.height = height.toInt()
            iconImageView.requestLayout()
        }
    }

    fun setIconTintColor(color: Int) {
        if (color != 0) {
            iconImageView.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    private fun showSwitch(show: Boolean) {
        if (show) {
            valueTextView.visibility = View.GONE
            switch.visibility = View.VISIBLE
        }
    }

    private fun setBorder(border: Int) {
        when (border) {
            0 -> rowConstraintLayout.setBackgroundResource(R.drawable.main_background_with_top_border)
            1 -> rowConstraintLayout.setBackgroundResource(R.drawable.main_background_with_bottom_border)
            2 -> rowConstraintLayout.setBackgroundResource(R.drawable.main_background_with_both_borders)
        }
    }

    private fun setMultiline(isMultiline: Boolean) {
        if (isMultiline == true) {
            valueTextView.maxLines = Integer.MAX_VALUE
            valueTextView.gravity = Gravity.START
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                valueTextView.justificationMode = JUSTIFICATION_MODE_INTER_WORD
            }

            val marginInPxBetweenTitleAndValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, resources.displayMetrics).toInt()
            val marginInPxSides = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20.0f, resources.displayMetrics).toInt()

            val constraintSet = ConstraintSet()
            constraintSet.clone(rowConstraintLayout)
            constraintSet.connect(R.id.title_text_view, ConstraintSet.BOTTOM, R.id.title_text_view, ConstraintSet.TOP)
            constraintSet.connect(R.id.value_text_view, ConstraintSet.TOP, R.id.title_text_view, ConstraintSet.BOTTOM, marginInPxBetweenTitleAndValue)
            constraintSet.connect(R.id.value_text_view, ConstraintSet.START, R.id.row_constraint_layout, ConstraintSet.START, marginInPxSides)
            constraintSet.connect(R.id.value_text_view, ConstraintSet.END, R.id.row_constraint_layout, ConstraintSet.END, marginInPxSides)
            constraintSet.setHorizontalBias(R.id.value_text_view, 0.0f)
            constraintSet.applyTo(rowConstraintLayout)
        }
    }
}