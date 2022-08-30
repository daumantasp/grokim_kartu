package com.dauma.grokimkartu.ui.main.adapters

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDateTimeFormatPattern
import com.dauma.grokimkartu.repositories.conversations.entities.Conversation
import com.dauma.grokimkartu.repositories.conversations.entities.MessageUserIconStatus
import com.dauma.grokimkartu.ui.viewelements.InitialsViewElement
import com.dauma.grokimkartu.ui.viewelements.SpinnerViewElement
import java.sql.Date
import java.sql.Timestamp

class ConversationsAdapter(
    val context: Context,
    var conversationsListData: MutableList<Conversation>,
    private val utils: Utils,
    private val onItemClicked: (Int) -> Unit
) : RecyclerView.Adapter<ConversationsAdapter.ConversationViewHolder>() {
    private val photoIconBackgroundDrawable: Drawable?

    init {
        // You may need to call mutate() on the drawable or else all icons are affected.
        photoIconBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.oval_background)?.mutate()
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.playerItemPhotoBackgroundColor, typedValue, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            photoIconBackgroundDrawable?.colorFilter = BlendModeColorFilter(typedValue.data, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            photoIconBackgroundDrawable?.setColorFilter(typedValue.data, PorterDuff.Mode.SRC_ATOP)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        return ConversationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.conversation_item, parent, false), utils, photoIconBackgroundDrawable, onItemClicked)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val itemData = conversationsListData[position]
        holder.bind(itemData)
    }

    override fun getItemCount(): Int {
        return conversationsListData.size
    }

    class ConversationViewHolder(
        val view: View,
        private val utils: Utils,
        private val photoIconBackgroundDrawable: Drawable?,
        private val onItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        val conversationItemContainer = view.findViewById<LinearLayout>(R.id.conversationItemContainer)
        val nameTextView = view.findViewById<TextView>(R.id.playerName)
        val dateTextView = view.findViewById<TextView>(R.id.messageDate)
        val textTextView = view.findViewById<TextView>(R.id.messageText)
        val initialsViewElement = view.findViewById<InitialsViewElement>(R.id.initialsViewElement)
        val photoIcon = view.findViewById<ImageView>(R.id.playerIconImageView)
        val spinnerViewElement = view.findViewById<SpinnerViewElement>(R.id.spinnerViewElement)

        fun bind(conversation: Conversation) {
            nameTextView.text = conversation.lastMessage?.user?.name ?: ""
            conversation.lastMessage?.createdAt?.let {
                val createdAtFormatted = utils.timeUtils.format(Date(it.time), getDateTimeFormat(it))
                dateTextView.text = createdAtFormatted
            }
            textTextView.text = conversation.lastMessage?.text ?: ""

            if (conversation.isRead == false) {
                nameTextView.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                textTextView.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                dateTextView.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            } else {
                nameTextView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
                textTextView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
                dateTextView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
            }

            conversationItemContainer.setOnClickListener {
                this.onItemClicked(conversation.id ?: -1)
            }

            fun bindOrUnbindPhoto() {
                if (conversation.lastMessage?.user?.userIcon?.icon != null) {
                    val ovalPhoto = utils.imageUtils.getOvalBitmap(conversation.lastMessage?.user?.userIcon?.icon!!)
                    photoIcon.setImageBitmap(ovalPhoto)
                    spinnerViewElement.showAnimation(false)
                    initialsViewElement.visibility = View.GONE
                    photoIcon.visibility = View.VISIBLE
                }
            }
            fun bindOrUnbindInitials() {
                if (conversation.lastMessage?.user?.userIcon?.icon  == null) {
                    val initials = utils.stringUtils.getInitials(conversation.lastMessage?.user?.name ?: "")
                    initialsViewElement.setInitials(initials)
                    spinnerViewElement.showAnimation(false)
                    photoIcon.visibility = View.GONE
                    initialsViewElement.visibility = View.VISIBLE
                }
            }
            fun bindDownloadInProgress() {
                photoIcon.setImageDrawable(photoIconBackgroundDrawable)
                initialsViewElement.visibility = View.GONE
                photoIcon.visibility = View.VISIBLE
                spinnerViewElement.showAnimation(true)
            }

            val iconStatus = conversation.lastMessage?.user?.userIcon?.status
            if (iconStatus == MessageUserIconStatus.DOWNLOADED_ICON_NOT_SET || iconStatus == MessageUserIconStatus.DOWNLOADED_ICON_SET) {
                bindOrUnbindPhoto()
                bindOrUnbindInitials()
            } else if (iconStatus == MessageUserIconStatus.DOWNLOAD_IN_PROGRESS) {
                bindDownloadInProgress()
            } else if (iconStatus == MessageUserIconStatus.NEED_TO_DOWNLOAD) {
                bindDownloadInProgress()
                conversation.lastMessage?.user?.userIcon?.loadIfNeeded { photo, e ->
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
}