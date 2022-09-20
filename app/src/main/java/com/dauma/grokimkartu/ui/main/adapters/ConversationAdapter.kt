package com.dauma.grokimkartu.ui.main.adapters

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.DummyCell
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDateTimeFormatPattern
import com.dauma.grokimkartu.repositories.conversations.entities.Message
import com.dauma.grokimkartu.repositories.conversations.entities.MessageUserIconStatus
import com.dauma.grokimkartu.ui.viewelements.InitialsViewElement
import com.dauma.grokimkartu.ui.viewelements.SpinnerViewElement
import java.sql.Date
import java.sql.Timestamp

class ConversationAdapter(
    val context: Context,
    var conversation: MutableList<Any>,
    private val utils: Utils,
    private val loadNextPage: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val photoIconBackgroundDrawable: Drawable?

    companion object {
        private const val MY_MESSAGE = 1
        private const val PARTNER_MESSAGE = 2
        private const val LAST = 3
    }

    init {
        // You may need to call mutate() on the drawable or else all icons are affected.
        photoIconBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.oval_background)?.mutate()
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.photo_placeholder_color, typedValue, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            photoIconBackgroundDrawable?.colorFilter = BlendModeColorFilter(typedValue.data, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            photoIconBackgroundDrawable?.setColorFilter(typedValue.data, PorterDuff.Mode.SRC_ATOP)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val data = conversation[position]
        if (data is Message && data.user?.isCurrent == true) {
            return MY_MESSAGE
        } else if (data is Message && data.user?.isCurrent == false) {
            return PARTNER_MESSAGE
        } else if (data is DummyCell) {
            return LAST
        }
        return MY_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MY_MESSAGE) {
            return MyMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.my_message_conversation_item, parent, false), utils)
        } else if (viewType == PARTNER_MESSAGE) {
            return PartnerMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.partner_message_conversation_item, parent, false), utils, photoIconBackgroundDrawable)
        } else if (viewType == LAST) {
            return MessageLastViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_last_item, parent, false), loadNextPage)
        }
        return MyMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.my_message_conversation_item, parent, false), utils)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = conversation[position]
        if (holder is MyMessageViewHolder && itemData is Message) {
            holder.bind(itemData)
        } else if (holder is PartnerMessageViewHolder && itemData is Message) {
            holder.bind(itemData)
        } else if (holder is MessageLastViewHolder && itemData is DummyCell) {
            holder.bind(itemData)
        }
    }

    override fun getItemCount(): Int {
        return conversation.size
    }

    class MyMessageViewHolder(
        view: View,
        private val utils: Utils
    ) : RecyclerView.ViewHolder(view) {
        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
        val dateTextView = view.findViewById<TextView>(R.id.dateTextView)
        val textView = view.findViewById<TextView>(R.id.textView)

        fun bind(message: Message) {
            nameTextView.text = message.user?.name
            message.createdAt?.let {
                val createdAtFormatted = utils.timeUtils.format(Date(it.time), getDateTimeFormat(it))
                dateTextView.text = createdAtFormatted
            }
            textView.text = message.text
        }

        // TODO: refactor
        private fun getDateTimeFormat(timestamp: Timestamp): CustomDateTimeFormatPattern {
            val currentDateTime = utils.timeUtils.getCurrentDateTime()
            val customDateTime = utils.timeUtils.convertToCustomDateTime(Date(timestamp.time))
            if (currentDateTime.isSameDay(customDateTime)) {
                return CustomDateTimeFormatPattern.HHmmss
            } else {
                return CustomDateTimeFormatPattern.yyyyMMddHHmmss
            }
        }
    }

    class PartnerMessageViewHolder(
        view: View,
        private val utils: Utils,
        private val photoIconBackgroundDrawable: Drawable?,
    ) : RecyclerView.ViewHolder(view) {
        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
        val dateTextView = view.findViewById<TextView>(R.id.dateTextView)
        val textView = view.findViewById<TextView>(R.id.textView)
        val userIconImageView = view.findViewById<ImageView>(R.id.playerIconImageView)
        val initialsViewElement = view.findViewById<InitialsViewElement>(R.id.initialsViewElement)
        val spinnerViewElement = view.findViewById<SpinnerViewElement>(R.id.spinnerViewElement)

        fun bind(message: Message) {
            nameTextView.text = message.user?.name
            message.createdAt?.let {
                val createdAtFormatted = utils.timeUtils.format(Date(it.time), getDateTimeFormat(it))
                dateTextView.text = createdAtFormatted
            }
            textView.text = message.text

            fun bindOrUnbindPhoto() {
                if (message.user?.userIcon?.icon != null) {
                    val ovalPhoto = utils.imageUtils.getOvalBitmap(message.user?.userIcon?.icon!!)
                    userIconImageView.setImageBitmap(ovalPhoto)
                    spinnerViewElement.showAnimation(false)
                    initialsViewElement.visibility = View.GONE
                    userIconImageView.visibility = View.VISIBLE
                }
            }
            fun bindOrUnbindInitials() {
                if (message.user?.userIcon?.icon == null) {
                    val initials = utils.stringUtils.getInitials(message.user?.name ?: "")
                    initialsViewElement.setInitials(initials)
                    spinnerViewElement.showAnimation(false)
                    userIconImageView.visibility = View.GONE
                    initialsViewElement.visibility = View.VISIBLE
                }
            }
            fun bindDownloadInProgress() {
                userIconImageView.setImageDrawable(photoIconBackgroundDrawable)
                initialsViewElement.visibility = View.GONE
                userIconImageView.visibility = View.VISIBLE
                spinnerViewElement.showAnimation(true)
            }

            val iconStatus = message.user?.userIcon?.status
            if (iconStatus == MessageUserIconStatus.DOWNLOADED_ICON_NOT_SET || iconStatus == MessageUserIconStatus.DOWNLOADED_ICON_SET) {
                bindOrUnbindPhoto()
                bindOrUnbindInitials()
            } else if (iconStatus == MessageUserIconStatus.DOWNLOAD_IN_PROGRESS) {
                bindDownloadInProgress()
            } else if (iconStatus == MessageUserIconStatus.NEED_TO_DOWNLOAD) {
                bindDownloadInProgress()
                message.user?.userIcon?.loadIfNeeded { photo, e ->
                    bindOrUnbindPhoto()
                    bindOrUnbindInitials()
                }
            }
        }

        // TODO: refactor
        private fun getDateTimeFormat(timestamp: Timestamp): CustomDateTimeFormatPattern {
            val currentDateTime = utils.timeUtils.getCurrentDateTime()
            val customDateTime = utils.timeUtils.convertToCustomDateTime(Date(timestamp.time))
            if (currentDateTime.isSameDay(customDateTime)) {
                return CustomDateTimeFormatPattern.HHmmss
            } else {
                return CustomDateTimeFormatPattern.yyyyMMddHHmmss
            }
        }
    }

    class MessageLastViewHolder(
        view: View,
        private val loadNextPage: () -> Unit
    ) : RecyclerView.ViewHolder(view) {
        val spinnerViewElement = view.findViewById<SpinnerViewElement>(R.id.spinnerViewElement)

        fun bind(data: DummyCell) {
            spinnerViewElement.showAnimation(true)
            loadNextPage()
        }
    }
}