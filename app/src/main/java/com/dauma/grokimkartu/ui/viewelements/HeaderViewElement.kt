package com.dauma.grokimkartu.ui.viewelements

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
    private val titleTextView: TextView
    private val userRelativeLayout: RelativeLayout
    private val initialsViewElement: InitialsViewElement
    private val userIconImageView: ImageView
    private val spinnerViewElement: SpinnerViewElement
    private val photoIconBackgroundDrawable: Drawable?
    @Inject lateinit var utils: Utils
    private var isTranslatedZ: Boolean = false

    init {
        inflate(context, R.layout.element_header, this)

        contentConstraintLayout = findViewById(R.id.contentConstraintLayout)
        titleTextView = findViewById(R.id.headerTitleTextView)
        userRelativeLayout = findViewById(R.id.userRelativeLayout)
        initialsViewElement = findViewById(R.id.initialsViewElement)
        userIconImageView = findViewById(R.id.userIconImageView)
        spinnerViewElement = findViewById(R.id.spinnerViewElement)

        photoIconBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.oval_background)?.mutate()
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.playerItemPhotoBackgroundColor, typedValue, true)
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
        val isIconVisible = attributes.getBoolean(R.styleable.HeaderViewElement_showIcon, false)
        val isBottomBorderVisible = attributes.getBoolean(R.styleable.HeaderViewElement_showBottomBorder, false)
        attributes.recycle()
        titleTextView.text = title
        userRelativeLayout.visibility = if (isIconVisible == true) View.VISIBLE else View.GONE
        showShadow(isBottomBorderVisible)
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

    fun showIconLoading(show: Boolean) {
        if (show == true) {
            userIconImageView.setImageDrawable(photoIconBackgroundDrawable)
            initialsViewElement.visibility = View.GONE
            userIconImageView.visibility = View.VISIBLE
        }
        spinnerViewElement.showAnimation(show)
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
}