package com.dauma.grokimkartu.ui.viewelements

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationState

class NotificationViewElement(context: Context, attrs: AttributeSet)
    : FrameLayout(context, attrs) {
    private val notificationCardView: CardView
    private val nameTextView: TextView
    private val descriptionTextView: TextView
    private val dateTextView: TextView
    private var onClick: () -> Unit = {}

    private val activeNotificationCardView: CardView
    private val activeNameTextView: TextView
    private val activeDescriptionTextView: TextView
    private val activeDateTextView: TextView

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

        activeNotificationCardView = findViewById(R.id.activeNotificationCardView)
        activeNameTextView = findViewById(R.id.activeNotificationName)
        activeDescriptionTextView = findViewById(R.id.activeNotificationDescription)
        activeDateTextView = findViewById(R.id.activeNotificationDate)

        notificationCardView.setOnClickListener { onClick() }
        activeNotificationCardView.setOnClickListener { onClick() }

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.NotificationViewElement)
        unreadNotificationBackgroundColor = attributes.getInt(
            R.styleable.NotificationViewElement_unreadNotificationBackgroundColor,
            R.color.sky_blue
        )
        unreadNotificationNameColor = attributes.getInt(
            R.styleable.NotificationViewElement_unreadNotificationNameColor,
            R.color.black
        )
        unreadNotificationDescriptionColor = attributes.getInt(
            R.styleable.NotificationViewElement_unreadNotificationDescriptionColor,
            R.color.black_50
        )
        unreadNotificationDateColor = attributes.getInt(
            R.styleable.NotificationViewElement_unreadNotificationDateColor,
            R.color.chambray
        )
        inactiveNotificationBackgroundColor = attributes.getInt(
            R.styleable.NotificationViewElement_inactiveNotificationBackgroundColor,
            R.color.light_grey
        )
        inactiveNotificationNameColor = attributes.getInt(
            R.styleable.NotificationViewElement_inactiveNotificationNameColor,
            R.color.black_50
        )
        inactiveNotificationDescriptionColor = attributes.getInt(
            R.styleable.NotificationViewElement_inactiveNotificationDescriptionColor,
            R.color.black_50
        )
        inactiveNotificationDateColor = attributes.getInt(
            R.styleable.NotificationViewElement_inactiveNotificationDateColor,
            R.color.black_50
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
            R.color.chambray
        )
        attributes.recycle()
    }

    fun setName(name: String) {
        nameTextView.text = name
        activeNameTextView.text = name
    }

    fun setDescription(description: String) {
        descriptionTextView.text = description
        activeDescriptionTextView.text = description
    }

    fun setDate(date: String) {
        dateTextView.text = date
        activeDateTextView.text = date
    }

    fun setOnClick(onClick: () -> Unit) {
        this.onClick = onClick
    }

    fun setState(state: NotificationState) {
        when (state) {
            NotificationState.INACTIVE -> {
                notificationCardView.setCardBackgroundColor(inactiveNotificationBackgroundColor)
                nameTextView.setTextColor(inactiveNotificationNameColor)
                dateTextView.setTextColor(inactiveNotificationDateColor)
                descriptionTextView.setTextColor(inactiveNotificationDescriptionColor)

                activeNotificationCardView.visibility = View.GONE
                notificationCardView.visibility = View.VISIBLE
            }
            NotificationState.ACTIVE -> {
                notificationCardView.visibility = View.GONE
                activeNotificationCardView.visibility = View.VISIBLE
            }
            NotificationState.UNREAD -> {
                notificationCardView.setCardBackgroundColor(unreadNotificationBackgroundColor)
                nameTextView.setTextColor(unreadNotificationNameColor)
                dateTextView.setTextColor(unreadNotificationDateColor)
                descriptionTextView.setTextColor(unreadNotificationDescriptionColor)

                activeNotificationCardView.visibility = View.GONE
                notificationCardView.visibility = View.VISIBLE
            }
        }
    }
}