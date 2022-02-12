package com.dauma.grokimkartu.ui.viewelements

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
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
    private val arrowImageView: ImageView
    private val switch: SwitchCompat
    private var onSwitchChecked: () -> Unit = {}

    init {
        inflate(context, R.layout.element_row, this)

        rowConstraintLayout = findViewById(R.id.rowConstraintLayout)
        titleTextView = findViewById(R.id.titleTextView)
        valueTextView = findViewById(R.id.valueTextView)
        arrowImageView = findViewById(R.id.arrowImageView)
        switch = findViewById(R.id.rowSwitch)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.RowViewElement)
        val title = attributes.getString(R.styleable.RowViewElement_rowTitle)
        val isArrowVisible = attributes.getBoolean(R.styleable.RowViewElement_rowShowArrow, false)
        val border = attributes.getInt(R.styleable.RowViewElement_rowBorders, 0)
        val isSwitchVisible = attributes.getBoolean(R.styleable.RowViewElement_rowShowSwitch, false)
        attributes.recycle()

        titleTextView.setText(title)

        showArrow(isArrowVisible)
        setBorder(border)
        showSwitch(isSwitchVisible)
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

    private fun showArrow(show: Boolean) {
        if (show) {
            arrowImageView.visibility = View.VISIBLE
            val constraintSet = ConstraintSet()
            constraintSet.clone(rowConstraintLayout)
            constraintSet.connect(R.id.valueTextView, ConstraintSet.END, R.id.arrowImageView, ConstraintSet.START)
            constraintSet.applyTo(rowConstraintLayout)
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
            0 -> rowConstraintLayout.setBackgroundResource(R.drawable.row_background_with_top_border)
            1 -> rowConstraintLayout.setBackgroundResource(R.drawable.row_background_with_bottom_border)
            2 -> rowConstraintLayout.setBackgroundResource(R.drawable.row_background_with_both_borders)
        }
    }
}