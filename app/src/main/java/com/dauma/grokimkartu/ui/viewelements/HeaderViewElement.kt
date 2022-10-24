package com.dauma.grokimkartu.ui.viewelements

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.core.widget.TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HeaderViewElement(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private val contentConstraintLayout: ConstraintLayout
    private val backImageButton: ImageButton
    private val titleTextView: TextView
    private val userRelativeLayout: RelativeLayout
    private val initialsViewElement: InitialsViewElement
    private val userIconImageView: ImageView
    private val photoIconBackgroundDrawable: Drawable?
    private val rightTextView: TextView
    private val notificationsRelativeLayout: RelativeLayout
    private val notificationsFrameLayout: FrameLayout
    private val unreadNotificationsCountTextView: TextView
    @Inject lateinit var utils: Utils
    private var isTranslatedZ: Boolean = false

    init {
        inflate(context, R.layout.element_header, this)

        contentConstraintLayout = findViewById(R.id.content_constraint_layout)
        backImageButton = findViewById(R.id.back_image_button)
        titleTextView = findViewById(R.id.header_title_text_view)
        userRelativeLayout = findViewById(R.id.user_relative_layout)
        initialsViewElement = findViewById(R.id.initials_view_element)
        userIconImageView = findViewById(R.id.user_icon_image_view)
        rightTextView = findViewById(R.id.right_text_view)
        notificationsRelativeLayout = findViewById(R.id.notifications_relative_layout)
        notificationsFrameLayout = findViewById(R.id.notifications_frame_layout)
        unreadNotificationsCountTextView = findViewById(R.id.unread_notifications_count_text_view)

        photoIconBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.oval_background)?.mutate()
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.photo_placeholder_color, typedValue, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            photoIconBackgroundDrawable?.colorFilter = BlendModeColorFilter(typedValue.data, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            photoIconBackgroundDrawable?.setColorFilter(typedValue.data, PorterDuff.Mode.SRC_ATOP)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            TextViewCompat.setAutoSizeTextTypeWithDefaults(titleTextView, AUTO_SIZE_TEXT_TYPE_UNIFORM)
        }

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.HeaderViewElement)
        val title = attributes.getString(R.styleable.HeaderViewElement_title)
        val isIconVisible = attributes.getBoolean(R.styleable.HeaderViewElement_show_icon, false)
        val isBottomBorderVisible = attributes.getBoolean(R.styleable.HeaderViewElement_show_bottom_border, false)
        val type = attributes.getInt(R.styleable.HeaderViewElement_header_type, 0)
        val textSize = attributes.getInt(R.styleable.HeaderViewElement_header_text_size, 0)
        val rightText = attributes.getString(R.styleable.HeaderViewElement_header_right_text)
        attributes.recycle()
        titleTextView.text = title
        userRelativeLayout.visibility = if (isIconVisible == true) View.VISIBLE else View.GONE
        notificationsRelativeLayout.visibility = if (isIconVisible == true) View.VISIBLE else View.GONE
        showShadow(isBottomBorderVisible)
        setType(type)
        setTextSize(textSize)
        setRightTextIfNeeded(rightText)
    }

    fun setTitle(title: String) {
        titleTextView.text = title
    }

    fun setInitials(initials: String) {
        initialsViewElement.setInitials(initials)
        userIconImageView.visibility = View.GONE
        initialsViewElement.visibility = View.VISIBLE
    }

    fun setPhotoIcon(photoIcon: Bitmap) {
        val ovalIcon = utils.imageUtils.getOvalBitmap(photoIcon)
        userIconImageView.setImageBitmap(ovalIcon)
        initialsViewElement.visibility = View.GONE
        userIconImageView.visibility = View.VISIBLE
    }

    fun setOnNotificationsClick(onClick: () -> Unit) {
        notificationsRelativeLayout.setOnClickListener { onClick() }
    }

    fun setUnreadNotificationsCount(count: String) {
        unreadNotificationsCountTextView.text = count
    }

    fun showUnreadNotificationsCount(show: Boolean) {
        // TODO: make animated?
        notificationsFrameLayout.visibility = if (show == true) View.VISIBLE else View.GONE
    }

    fun showShadow(show: Boolean) {
        if (isTranslatedZ == show) {
            return
        }

        isTranslatedZ = show
        if (show == true) {
            contentConstraintLayout.translationZ = 5.0f
        } else {
            contentConstraintLayout.translationZ = 0.0f
        }
    }

    fun setOnInitialsOrIconClick(onClick: () -> Unit) {
        userRelativeLayout.setOnClickListener { onClick() }
    }

    fun setOnBackClick(onClick: () -> Unit) {
        backImageButton.setOnClickListener { onClick() }
    }

    fun setOnRightTextClick(onClick: () -> Unit) {
        rightTextView.setOnClickListener { onClick() }
    }

    private fun setType(type: Int) {
        if (type == 0) {
            // Default, change programmatically not implemented
        } else {
            backImageButton.visibility = View.VISIBLE
            titleTextView.gravity = Gravity.CENTER
            val constraintSet = ConstraintSet()
            constraintSet.clone(contentConstraintLayout)
            constraintSet.connect(R.id.header_title_text_view, ConstraintSet.END, R.id.content_constraint_layout, ConstraintSet.END, 0)
            constraintSet.applyTo(contentConstraintLayout)
        }
    }

    private fun setTextSize(size: Int) {
        if (size == 0) {
            // Default, change programmatically not implemented
        } else {
            titleTextView.setTextSize(COMPLEX_UNIT_SP, 22.0f)
        }
    }

    private fun setRightTextIfNeeded(text: String?) {
        if (text == null || text == "") {
            // Default, change programmatically not implemented
        } else {
            rightTextView.setText(text!!)
            rightTextView.visibility = View.VISIBLE
            titleTextView.gravity = Gravity.START
            val marginInPxSides = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20.0f, resources.displayMetrics).toInt()
            val constraintSet = ConstraintSet()
            constraintSet.clone(contentConstraintLayout)
            constraintSet.connect(R.id.header_title_text_view, ConstraintSet.END, R.id.right_text_view, ConstraintSet.START, marginInPxSides)
            if (backImageButton.visibility == View.VISIBLE) {
                constraintSet.connect(R.id.header_title_text_view, ConstraintSet.START, R.id.back_image_button, ConstraintSet.END, marginInPxSides)
            }
            constraintSet.applyTo(contentConstraintLayout)
        }
    }
}