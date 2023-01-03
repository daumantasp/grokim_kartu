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
import com.dauma.grokimkartu.ui.viewelements.InitialsViewElement
import java.sql.Date
import java.sql.Timestamp

class ConversationsAdapter(
    val context: Context,
    var conversationsListData: MutableList<Any>,
    private val utils: Utils,
    private val onItemClicked: (Int, String) -> Unit
) : RecyclerView.Adapter<ConversationsAdapter.ConversationViewHolder>() {
    private val photoIconBackgroundDrawable: Drawable?

    companion object {
        private const val PRIVATE = 1
        private const val THOMANN = 2
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
        if (conversationsListData[position] is PrivateConversationData) {
            return PRIVATE
        } else if (conversationsListData[position] is ThomannConversationData) {
            return THOMANN
        }
        return PRIVATE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        if (viewType == PRIVATE) {
            return PrivateConversationViewHolder(context, LayoutInflater.from(parent.context).inflate(R.layout.conversation_item, parent, false), utils, photoIconBackgroundDrawable, onItemClicked)
        } else if (viewType == THOMANN) {
            return ThomannConversationViewHolder(context, LayoutInflater.from(parent.context).inflate(R.layout.conversation_item, parent, false), utils, photoIconBackgroundDrawable, onItemClicked)
        }
        return PrivateConversationViewHolder(context, LayoutInflater.from(parent.context).inflate(R.layout.conversation_item, parent, false), utils, photoIconBackgroundDrawable, onItemClicked)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val itemData = conversationsListData[position]
        if (holder is PrivateConversationViewHolder && itemData is PrivateConversationData) {
            holder.bind(itemData)
        }
        else if (holder is ThomannConversationViewHolder && itemData is ThomannConversationData) {
            holder.bind(itemData)
        }
    }

    override fun getItemCount(): Int {
        return conversationsListData.size
    }

    abstract class ConversationViewHolder(
        val view: View,
        protected val utils: Utils,
    ) : RecyclerView.ViewHolder(view) {
        val conversationItemContainer = view.findViewById<LinearLayout>(R.id.conversation_item_container)
        val nameTextView = view.findViewById<TextView>(R.id.player_name_text_view)
        val dateTextView = view.findViewById<TextView>(R.id.message_date_text_view)
        val textTextView = view.findViewById<TextView>(R.id.message_text_text_view)
        val initialsViewElement = view.findViewById<InitialsViewElement>(R.id.initials_view_element)
        val photoIcon = view.findViewById<ImageView>(R.id.player_icon_image_view)
        val rightTextAttentionerView = view.findViewById<ImageView>(R.id.right_text_attentioner_view)

        // TODO: refactor
        protected fun getDateTimeFormat(timestamp: Timestamp): CustomDateTimeFormatPattern {
            val currentDateTime = utils.timeUtils.getCurrentDateTime()
            val customDateTime = utils.timeUtils.convertToCustomDateTime(Date(timestamp.time))
            if (currentDateTime.isSameDay(customDateTime)) {
                return CustomDateTimeFormatPattern.HHmmss
            } else {
                return CustomDateTimeFormatPattern.yyyyMMddHHmmss
            }
        }
    }

    private class PrivateConversationViewHolder(
        private val context: Context,
        view: View,
        utils: Utils,
        private val photoIconBackgroundDrawable: Drawable?,
        private val onItemClicked: (Int, String) -> Unit
    ) : ConversationViewHolder(view, utils) {
        fun bind(privateConversationData: PrivateConversationData) {
            val conversation = privateConversationData.conversation
            nameTextView.text = conversation.partner?.name ?: ""
            conversation.lastMessage?.createdAt?.let {
                val createdAtFormatted = utils.timeUtils.format(Date(it.time), getDateTimeFormat(it))
                dateTextView.text = createdAtFormatted
            }
            val isLastMessageYours = conversation.lastMessage?.user?.isCurrent ?: false
            val message = if (isLastMessageYours) {
                "${context.getString(R.string.conversations_you)}: ${conversation.lastMessage?.text}"
            } else {
                conversation.lastMessage?.text
            }
            textTextView.text = message

            if (conversation.isRead == false) {
                nameTextView.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                textTextView.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                dateTextView.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                rightTextAttentionerView.visibility = View.VISIBLE
            } else {
                nameTextView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
                textTextView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
                dateTextView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
                rightTextAttentionerView.visibility = View.GONE
            }

            conversationItemContainer.setOnClickListener {
                this.onItemClicked(
                    conversation.partner?.id ?: -1,
                    conversation.partner?.name ?: ""
                )
            }

            fun bindIconIfPossible() {
                conversation.partner?.iconLoader?.icon?.let {
                    val ovalPhoto = utils.imageUtils.getOvalBitmap(it)
                    photoIcon.setImageBitmap(ovalPhoto)
                    initialsViewElement.visibility = View.GONE
                    photoIcon.visibility = View.VISIBLE
                }
            }
            fun bindInitials() {
                val initials = utils.stringUtils.getInitials(conversation.partner?.name ?: "")
                initialsViewElement.setInitials(initials)
                photoIcon.visibility = View.GONE
                initialsViewElement.visibility = View.VISIBLE
            }

            bindInitials()
            if (conversation.partner?.iconLoader?.status == IconStatus.ICON_DOWNLOADED) {
                bindIconIfPossible()
            } else if (conversation.partner?.iconLoader?.status == IconStatus.NEED_TO_DOWNLOAD) {
                conversation.partner?.iconLoader?.loadIcon { icon ->
                    if (icon != null) {
                        bindIconIfPossible()
                    }
                }
            }
        }
    }

    private class ThomannConversationViewHolder(
        private val context: Context,
        view: View,
        utils: Utils,
        private val photoIconBackgroundDrawable: Drawable?,
        private val onItemClicked: (Int, String) -> Unit
    ) : ConversationViewHolder(view, utils) {
        fun bind(thomannConversationData: ThomannConversationData) {
            val conversation = thomannConversationData.conversation
            val name = "#${conversation.thomannId ?: -1}"
            nameTextView.text = name
            conversation.lastMessage?.createdAt?.let {
                val createdAtFormatted = utils.timeUtils.format(Date(it.time), getDateTimeFormat(it))
                dateTextView.text = createdAtFormatted
            }
            val isLastMessageYours = conversation.lastMessage?.user?.isCurrent ?: false
            val message = if (isLastMessageYours) {
                "${context.getString(R.string.conversations_you)}: ${conversation.lastMessage?.text}"
            } else {
                val userName = conversation.lastMessage?.user?.name ?: context.getString(R.string.conversation_unknown_user)
                "${userName}: ${conversation.lastMessage?.text}"
            }
            textTextView.text = message

            if (conversation.isRead == false) {
                nameTextView.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                textTextView.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                dateTextView.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                rightTextAttentionerView.visibility = View.VISIBLE
            } else {
                nameTextView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
                textTextView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
                dateTextView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
                rightTextAttentionerView.visibility = View.GONE
            }

            conversationItemContainer.setOnClickListener {
                this.onItemClicked(
                    conversation.thomannId ?: -1,
                    name
                )
            }

            initialsViewElement.setInitials("${conversation.thomannId ?: -1}")
            photoIcon.visibility = View.GONE
            initialsViewElement.visibility = View.VISIBLE
        }
    }
}