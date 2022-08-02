package com.dauma.grokimkartu.ui.main.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDateTimeFormatPattern
import com.dauma.grokimkartu.repositories.players.entities.PlayerIconStatus
import com.dauma.grokimkartu.ui.viewelements.InitialsViewElement
import com.dauma.grokimkartu.ui.viewelements.SpinnerViewElement
import java.sql.Date

class ConversationsAdapter(
    var conversationsListData: MutableList<ConversationsData>,
    private val utils: Utils,
    private val onItemClicked: (Int) -> Unit
) : RecyclerView.Adapter<ConversationsAdapter.ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        return ConversationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.conversation_item, parent, false), utils, onItemClicked)
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
        private val onItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        val conversationItemContainer = view.findViewById<LinearLayout>(R.id.conversationItemContainer)
        val nameTextView = view.findViewById<TextView>(R.id.playerName)
        val dateTextView = view.findViewById<TextView>(R.id.messageDate)
        val textTextView = view.findViewById<TextView>(R.id.messageText)
        val initialsViewElement = view.findViewById<InitialsViewElement>(R.id.initialsViewElement)
        val photoIcon = view.findViewById<ImageView>(R.id.playerIconImageView)
        val spinnerViewElement = view.findViewById<SpinnerViewElement>(R.id.spinnerViewElement)

        fun bind(data: ConversationsData) {
            val lastMessagecreatedAtFormatted = data.conversation.lastMessage?.createdAt?.time?.let {
                utils.timeUtils.format(Date(it), CustomDateTimeFormatPattern.yyyyMMdd)
            }

            nameTextView.text = data.conversation.lastMessage?.user?.name ?: ""
            dateTextView.text = lastMessagecreatedAtFormatted
            textTextView.text = data.conversation.lastMessage?.text ?: ""

            if (data.conversation.isRead == false) {
                textTextView.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            } else {
                textTextView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
            }

            conversationItemContainer.setOnClickListener {
                this.onItemClicked(data.conversation.id ?: -1)
            }

//            fun bindOrUnbindPhoto() {
//                if (data.player.icon.icon != null) {
//                    val ovalPhoto = utils.imageUtils.getOvalBitmap(data.player.icon.icon!!)
//                    photoIcon.setImageBitmap(ovalPhoto)
//                    spinnerViewElement.showAnimation(false)
//                    initialsViewElement.visibility = View.GONE
//                    photoIcon.visibility = View.VISIBLE
//                }
//            }
//            fun bindOrUnbindInitials() {
//                if (data.player.icon.icon == null) {
//                    val initials = utils.stringUtils.getInitials(data.player.name ?: "")
//                    initialsViewElement.setInitials(initials)
//                    spinnerViewElement.showAnimation(false)
//                    photoIcon.visibility = View.GONE
//                    initialsViewElement.visibility = View.VISIBLE
//                }
//            }
//            fun bindDownloadInProgress() {
//                photoIcon.setImageDrawable(photoIconBackgroundDrawable)
//                initialsViewElement.visibility = View.GONE
//                photoIcon.visibility = View.VISIBLE
//                spinnerViewElement.showAnimation(true)
//            }
//
//            val iconStatus = data.player.icon.status
//            if (iconStatus == PlayerIconStatus.DOWNLOADED_ICON_NOT_SET || iconStatus == PlayerIconStatus.DOWNLOADED_ICON_SET) {
//                bindOrUnbindPhoto()
//                bindOrUnbindInitials()
//            } else if (iconStatus == PlayerIconStatus.DOWNLOAD_IN_PROGRESS) {
//                bindDownloadInProgress()
//            } else if (iconStatus == PlayerIconStatus.NEED_TO_DOWNLOAD) {
//                bindDownloadInProgress()
//                data.player.icon.loadIfNeeded { photo, e ->
//                    bindOrUnbindPhoto()
//                    bindOrUnbindInitials()
//                }
//            }
        }
    }
}