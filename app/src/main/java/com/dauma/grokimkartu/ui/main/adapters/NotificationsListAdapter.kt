package com.dauma.grokimkartu.ui.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.ui.viewelements.SpinnerViewElement

class NotificationsListAdapter(
    var notificationsListData: MutableList<Any>,
    private val onItemClicked: (Int) -> Unit,
    private val loadNextPage: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val NOTIFICATION = 1
        private const val LAST = 2
    }

    override fun getItemViewType(position: Int): Int {
        if (notificationsListData[position] is NotificationLastInPageData) {
            return LAST
        } else if (notificationsListData[position] is NotificationsListData) {
            return NOTIFICATION
        }
        return NOTIFICATION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == NOTIFICATION) {
            return NotificationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false), onItemClicked)
        } else if (viewType == LAST) {
            return NotificationLastViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notification_last_item, parent, false), loadNextPage)
        }
        return return NotificationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false), onItemClicked)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = notificationsListData[position]
        if (holder is NotificationLastViewHolder && itemData is NotificationLastInPageData) {
            holder.bind(itemData)
        } else if (holder is NotificationViewHolder && itemData is NotificationsListData) {
            holder.bind(itemData)
        }
    }

    override fun getItemCount(): Int {
        return notificationsListData.size
    }

    class NotificationViewHolder(
        val view: View,
        private val onItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        val nameTextView = view.findViewById<TextView>(R.id.notificationName)
        val descriptionTextView = view.findViewById<TextView>(R.id.notificationDescription)

        fun bind(data: NotificationsListData) {
            nameTextView.text = data.notification.name
            descriptionTextView.text = data.notification.description
        }
    }

    class NotificationLastViewHolder(
        view: View,
        private val loadNextPage: () -> Unit
    ) : RecyclerView.ViewHolder(view) {
        val spinnerViewElement = view.findViewById<SpinnerViewElement>(R.id.spinnerViewElement)

        fun bind(data: NotificationLastInPageData) {
            spinnerViewElement.showAnimation(true)
            loadNextPage()
        }
    }
}