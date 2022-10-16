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
import com.dauma.grokimkartu.general.IconStatus
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDateTimeFormatPattern
import com.dauma.grokimkartu.repositories.conversations.entities.Conversation
import com.dauma.grokimkartu.ui.viewelements.InitialsViewElement
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
        context.theme.resolveAttribute(R.attr.photo_placeholder_color, typedValue, true)
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
        val conversationItemContainer = view.findViewById<LinearLayout>(R.id.conversation_item_container)
        val nameTextView = view.findViewById<TextView>(R.id.player_name_text_view)
        val dateTextView = view.findViewById<TextView>(R.id.message_date_text_view)
        val textTextView = view.findViewById<TextView>(R.id.message_text_text_view)
        val initialsViewElement = view.findViewById<InitialsViewElement>(R.id.initials_view_element)
        val photoIcon = view.findViewById<ImageView>(R.id.player_icon_image_view)

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

            fun bindIconIfPossible() {
                conversation.lastMessage?.user?.iconLoader?.icon?.let {
                    val ovalPhoto = utils.imageUtils.getOvalBitmap(it)
                    photoIcon.setImageBitmap(ovalPhoto)
                    initialsViewElement.visibility = View.GONE
                    photoIcon.visibility = View.VISIBLE
                }
            }
            fun bindInitials() {
                val initials = utils.stringUtils.getInitials(conversation.lastMessage?.user?.name ?: "")
                initialsViewElement.setInitials(initials)
                photoIcon.visibility = View.GONE
                initialsViewElement.visibility = View.VISIBLE
            }

            bindInitials()
            if (conversation.lastMessage?.user?.iconLoader?.status == IconStatus.ICON_DOWNLOADED) {
                bindIconIfPossible()
            } else if (conversation.lastMessage?.user?.iconLoader?.status == IconStatus.NEED_TO_DOWNLOAD) {
                conversation.lastMessage?.user?.iconLoader?.loadIcon { icon ->
                    if (icon != null) {
                        bindIconIfPossible()
                    }
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