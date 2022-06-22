package com.dauma.grokimkartu.ui.viewelements

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.dauma.grokimkartu.R

enum class NotificationViewElementState {
    INACTIVE,
    ACTIVE,
    UNREAD
}

class NotificationViewElement(context: Context, attrs: AttributeSet)
    : FrameLayout(context, attrs) {
    private val notificationCardView: CardView
    private val nameTextView: TextView
    private val descriptionTextView: TextView
    private val dateTextView: TextView
    private var onClick: () -> Unit = {}

    private val unreadNotificationBackgroundColor: Int
    private val unreadNotificationNameColor: Int
    private val unreadNotificationDescriptionColor: Int
    private val unreadNotificationDateColor: Int
    private val inactiveNotificationBackgroundColor: Int
    private val inactiveNotificationNameColor: Int
    private val inactiveNotificationDescriptionColor: Int
    private val inactiveNotificationDateColor: Int
    private val activeNotificationBackgroundColor: Int
    private val activeNotificationNameColor: Int
    private val activeNotificationDescriptionColor: Int
    private val activeNotificationDateColor: Int

    init {
        inflate(context, R.layout.element_notification, this)

        notificationCardView = findViewById(R.id.notificationCardView)
        nameTextView = findViewById(R.id.notificationName)
        descriptionTextView = findViewById(R.id.notificationDescription)
        dateTextView = findViewById(R.id.notificationDate)

        notificationCardView.setOnClickListener { onClick() }

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.NotificationViewElement)
        unreadNotificationBackgroundColor = attributes.getInt(
            R.styleable.NotificationViewElement_unreadNotificationBackgroundColor,
            R.color.columbiaBlue
        )
        unreadNotificationNameColor = attributes.getInt(
            R.styleable.NotificationViewElement_unreadNotificationNameColor,
            R.color.black
        )
        unreadNotificationDescriptionColor = attributes.getInt(
            R.styleable.NotificationViewElement_unreadNotificationDescriptionColor,
            R.color.black50
        )
        unreadNotificationDateColor = attributes.getInt(
            R.styleable.NotificationViewElement_unreadNotificationDateColor,
            R.color.violetBlue
        )
        inactiveNotificationBackgroundColor = attributes.getInt(
            R.styleable.NotificationViewElement_inactiveNotificationBackgroundColor,
            R.color.lightGrey
        )
        inactiveNotificationNameColor = attributes.getInt(
            R.styleable.NotificationViewElement_inactiveNotificationNameColor,
            R.color.black50
        )
        inactiveNotificationDescriptionColor = attributes.getInt(
            R.styleable.NotificationViewElement_inactiveNotificationDescriptionColor,
            R.color.black50
        )
        inactiveNotificationDateColor = attributes.getInt(
            R.styleable.NotificationViewElement_inactiveNotificationDateColor,
            R.color.black50
        )
        activeNotificationBackgroundColor = attributes.getInt(
            R.styleable.NotificationViewElement_activeNotificationBackgroundColor,
            R.color.white
        )
        activeNotificationNameColor = attributes.getInt(
            R.styleable.NotificationViewElement_activeNotificationNameColor,
            R.color.black
        )
        activeNotificationDescriptionColor = attributes.getInt(
            R.styleable.NotificationViewElement_activeNotificationDescriptionColor,
            R.color.black
        )
        activeNotificationDateColor = attributes.getInt(
            R.styleable.NotificationViewElement_activeNotificationDateColor,
            R.color.violetBlue
        )
        attributes.recycle()
    }

    fun setName(name: String) {
        nameTextView.text = name
    }

    fun setDescription(description: String) {
        descriptionTextView.text = description
    }

    fun setDate(date: String) {
        dateTextView.text = date
    }

    fun setOnClick(onClick: () -> Unit) {
        this.onClick = onClick
    }

    fun setState(state: NotificationViewElementState) {
        when (state) {
            NotificationViewElementState.INACTIVE -> {
                notificationCardView.setBackgroundColor(inactiveNotificationBackgroundColor)
                nameTextView.setTextColor(inactiveNotificationNameColor)
                dateTextView.setTextColor(inactiveNotificationDateColor)
                descriptionTextView.setTextColor(inactiveNotificationDescriptionColor)
                notificationCardView.cardElevation = 0.0f
            }
            NotificationViewElementState.ACTIVE -> {
                notificationCardView.setBackgroundColor(activeNotificationBackgroundColor)
                nameTextView.setTextColor(activeNotificationNameColor)
                dateTextView.setTextColor(activeNotificationDateColor)
                descriptionTextView.setTextColor(activeNotificationDescriptionColor)
                notificationCardView.cardElevation = 10.0f
            }
            NotificationViewElementState.UNREAD -> {
                notificationCardView.setBackgroundColor(unreadNotificationBackgroundColor)
                nameTextView.setTextColor(unreadNotificationNameColor)
                dateTextView.setTextColor(unreadNotificationDateColor)
                descriptionTextView.setTextColor(unreadNotificationDescriptionColor)
                notificationCardView.cardElevation = 0.0f
            }
        }
    }
}