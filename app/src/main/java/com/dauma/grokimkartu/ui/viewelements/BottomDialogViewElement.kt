package com.dauma.grokimkartu.ui.viewelements

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDate
import com.dauma.grokimkartu.ui.BottomDialogCodeValueData
import com.dauma.grokimkartu.ui.BottomDialogData
import com.dauma.grokimkartu.ui.BottomDialogDatePickerData
import com.dauma.grokimkartu.ui.main.adapters.BottomDialogCodeValueAdapter
import com.dauma.grokimkartu.ui.main.adapters.CodeValue
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BottomDialogViewElement(context: Context, attrs: AttributeSet)
    : RelativeLayout(context, attrs) {
    private val rootRelativeLayout: RelativeLayout
    private val backgroundFrameLayout: FrameLayout
    private val contentLinearLayout: LinearLayout
    private val titleTextView: TextView
    private val valueEditText: EditText
    private val valueLimitTextView: TextView
    private val datePicker: DatePicker
    private val codeValueLinearLayout: LinearLayout
    private val codeValueSearchEditText: TextInputEditText
    private val codeValueRecyclerView: RecyclerView
    private val saveButton: ButtonViewElement
    private var onValueChanged: (String) -> Unit = {}
    private var valueCharsLimit: Int? = null
    private var codeValues: MutableList<CodeValue> = mutableListOf()
    @Inject lateinit var utils: Utils

    companion object {
        private const val DEFAULT_ANIMATION_DURATION: Long = 300L
    }

    init {
        inflate(context, R.layout.element_bottom_dialog, this)

        rootRelativeLayout = findViewById(R.id.bottomDialogRootRelativeLayout)
        backgroundFrameLayout = findViewById(R.id.bottomDialogBackgroundFrameLayout)
        contentLinearLayout = findViewById(R.id.dialogContentLinearLayout)
        titleTextView = findViewById(R.id.titleTextView)
        valueEditText = findViewById(R.id.valueEditText)
        valueLimitTextView = findViewById(R.id.valueLimitTextView)
        datePicker = findViewById(R.id.datePicker)
        codeValueLinearLayout = findViewById(R.id.codeValueLinearLayout)
        codeValueSearchEditText = findViewById(R.id.codeValueTextInputEditText)
        codeValueRecyclerView = findViewById(R.id.codeValueRecyclerView)
        saveButton = findViewById(R.id.saveButton)

        contentLinearLayout.setOnClickListener {}
        valueEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                onValueChanged(p0.toString())
                if (valueLimitTextView.visibility == View.VISIBLE) {
                    valueLimitTextView.text = getValueCharLimitsText()
                }
            }
        })
        codeValueSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                onValueChanged(p0.toString())
            }
        })

        // Read More: https://stackoverflow.com/questions/21926644/get-height-and-width-of-a-layout-programmatically
        rootRelativeLayout.doOnLayout { hide(animated = false) }
    }

    fun bindValueData(data: BottomDialogData) {
        reset()
        setTitle(data.title)
        setEditableValue(data.value)
        setValueCharsLimit(data.valueLimit)
        setOnSaveClick { value -> data.onSaveClicked(value) }
        setOnValueChanged { value -> data.onValueChanged(value) }
        setOnCancelClick { data.onCancelClicked() }

        datePicker.visibility = View.GONE
        codeValueLinearLayout.visibility = View.GONE
        valueEditText.visibility = View.VISIBLE
        saveButton.visibility = View.VISIBLE
    }

    fun bindDatePickerData(data: BottomDialogDatePickerData) {
        reset()
        setTitle(data.title)
        datePicker.updateDate(data.selectedDate.year, data.selectedDate.month - 1, data.selectedDate.dayOfMonth)
        datePicker.setOnDateChangedListener { _, year, month, dayOfMonth ->
            val datePickerDate = CustomDate(year, month - 1, dayOfMonth)
            data.onSelectedDateChanged(datePickerDate)
        }
        if (data.minDate != null) {
            datePicker.minDate = utils.timeUtils.convertToTimeInMillis(data.minDate!!)
        }
        if (data.maxDate != null) {
            datePicker.maxDate = utils.timeUtils.convertToTimeInMillis(data.maxDate!!)
        }
        setOnCancelClick { data.onCancelClicked() }
        setSaveButtonEnabled(data.isSaveButtonEnabled)
        saveButton.setOnClick(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val selectedDate = CustomDate(
                    datePicker.year,
                    datePicker.month + 1,
                    datePicker.dayOfMonth
                )
                data.onSaveClicked(selectedDate)
            }
        })

        valueEditText.visibility = View.GONE
        codeValueLinearLayout.visibility = View.GONE
        datePicker.visibility = View.VISIBLE
        saveButton.visibility = View.VISIBLE
    }

    fun bindCodeValueData(data: BottomDialogCodeValueData) {
        reset()
        setTitle(data.title)
        codeValues.clear()
        codeValues.addAll(data.codeValues)
        setupCodeValueRecyclerView(codeValues, data.onCodeValueClicked)
        setOnValueChanged { value ->
                data.onSearchValueChanged(value)
        }

        valueEditText.visibility = View.GONE
        datePicker.visibility = View.GONE
        saveButton.visibility = View.GONE
        codeValueLinearLayout.visibility = View.VISIBLE
    }

    fun setCodeValues(codeValues: List<CodeValue>) {
        this.codeValues.clear()
        this.codeValues.addAll(codeValues)
        codeValueRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun setTitle(title: String) {
        titleTextView.setText(title)
    }

    private fun setEditableValue(value: String) {
        valueEditText.setText(value)
        valueEditText.setSelection(valueEditText.length())
    }

    fun setSaveButtonEnabled(isEnabled: Boolean) {
        saveButton.isEnabled = isEnabled
    }

    fun showLoading(isLoading: Boolean) {
        saveButton.showAnimation(isLoading)
    }

    private fun setOnSaveClick(onClick: (String) -> Unit) {
        saveButton.setOnClick(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                onClick(valueEditText.text.toString())
            }
        })
    }

    private fun setOnCancelClick(onClick: () -> Unit) {
        backgroundFrameLayout.setOnClickListener { onClick() }
    }

    private fun setOnValueChanged(onValueChanged: (String) -> Unit) {
        this.onValueChanged = onValueChanged
    }

    private fun setValueCharsLimit(limit: Int?) {
        this.valueCharsLimit = limit
        if (limit != null) {
            valueEditText.filters = arrayOf(InputFilter.LengthFilter(limit!!))
            valueLimitTextView.text = getValueCharLimitsText()
            valueLimitTextView.visibility = View.VISIBLE
        } else {
            valueLimitTextView.visibility = View.GONE
            valueEditText.filters = arrayOf()
            valueLimitTextView.text = ""
        }
    }

    private fun setupCodeValueRecyclerView(codeValues: List<CodeValue>, onItemClicked: (String) -> Unit) {
        codeValueRecyclerView.layoutManager = LinearLayoutManager(context)
        codeValueRecyclerView.adapter = BottomDialogCodeValueAdapter(codeValues, onItemClicked)
    }

    private fun reset() {
        setOnValueChanged {}
        setOnCancelClick {}
        setOnCancelClick {}
        titleTextView.text = ""
        valueEditText.setText("")
        valueEditText.filters = arrayOf()
        valueLimitTextView.text = ""
        saveButton.isEnabled = true
        onValueChanged = {}
        valueCharsLimit = null
        valueEditText.visibility = View.GONE
        datePicker.visibility = View.GONE
        codeValues.clear()
        codeValueSearchEditText.setText("")
        hide(animated = false)
    }

    // Read more about animations https://www.raywenderlich.com/2785491-android-animation-tutorial-with-kotlin
    fun show(animated: Boolean, onComplete: () -> Unit = {}) {
        val height = contentLinearLayout.height
        if (animated == true) {
            val backgroundLayoutAlphaAnimator = ObjectAnimator.ofFloat(backgroundFrameLayout, "alpha", 0.5f, 1.0f)
            backgroundLayoutAlphaAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                    backgroundFrameLayout.alpha = 0.5f
                    rootRelativeLayout.visibility = View.VISIBLE
                }
                override fun onAnimationEnd(p0: Animator?) {
                    onComplete()
                }
                override fun onAnimationCancel(p0: Animator?) {}
                override fun onAnimationRepeat(p0: Animator?) {}
            })
            val contentLayoutTranslationYAnimator = ObjectAnimator.ofFloat(contentLinearLayout, "translationY", height.toFloat(), 0.0f)
            val animatorSet = AnimatorSet()
            animatorSet.play(backgroundLayoutAlphaAnimator).with(contentLayoutTranslationYAnimator)
            animatorSet.duration = DEFAULT_ANIMATION_DURATION
            animatorSet.start()
        } else {
            contentLinearLayout.translationY = 0.0f
            backgroundFrameLayout.alpha = 1.0f
            rootRelativeLayout.visibility = View.VISIBLE
        }
    }

    fun hide(animated: Boolean, onComplete: () -> Unit = {}) {
        val height = contentLinearLayout.height
        if (animated == true) {
            val backgroundLayoutAlphaAnimator = ObjectAnimator.ofFloat(backgroundFrameLayout, "alpha", 1.0f, 0.5f)
            backgroundLayoutAlphaAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {}
                override fun onAnimationEnd(p0: Animator?) {
                    backgroundFrameLayout.alpha = 0.5f
                    rootRelativeLayout.visibility = View.GONE
                    onComplete()
                }
                override fun onAnimationCancel(p0: Animator?) {}
                override fun onAnimationRepeat(p0: Animator?) {}
            })
            val contentLayoutTranslationYAnimator = ObjectAnimator.ofFloat(contentLinearLayout, "translationY", 0.0f, height.toFloat())
            val animatorSet = AnimatorSet()
            animatorSet.play(backgroundLayoutAlphaAnimator).with(contentLayoutTranslationYAnimator)
            animatorSet.duration = DEFAULT_ANIMATION_DURATION
            animatorSet.start()
        } else {
            rootRelativeLayout.visibility = View.GONE
            backgroundFrameLayout.alpha = 0.0f
            contentLinearLayout.translationY = height.toFloat()
        }
    }

    private fun getValueCharLimitsText() : String {
        val currentValueCharCount = valueEditText.text.toString().length
        if (valueCharsLimit != null) {
            return "${currentValueCharCount} / ${valueCharsLimit!!}"
        } else {
            return currentValueCharCount.toString()
        }
    }
}