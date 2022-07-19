package com.dauma.grokimkartu.ui.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDateTimeFormatPattern
import com.dauma.grokimkartu.ui.viewelements.NotificationViewElement
import com.dauma.grokimkartu.repositories.notifications.entities.NotificationState
import com.dauma.grokimkartu.ui.viewelements.SpinnerViewElement
import java.sql.Date

class NotificationsListAdapter(
    var notificationsListData: MutableList<Any>,
    private val utils: Utils,
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
            return NotificationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false), utils, onItemClicked)
        } else if (viewType == LAST) {
            return NotificationLastViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notification_last_item, parent, false), loadNextPage)
        }
        return NotificationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false), utils, onItemClicked)
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
        private val utils: Utils,
        private val onItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        val notificationViewElement = view.findViewById<NotificationViewElement>(R.id.notificationViewElement)

        fun bind(data: NotificationsListData) {
            val createdAtFormatted = data.notification.createdAt?.time?.let {
                utils.timeUtils.format(Date(it), CustomDateTimeFormatPattern.yyyyMMdd)
            }

            notificationViewElement.apply {
                setName(data.notification.name ?: "")
                setDate(createdAtFormatted ?: "")
                setDescription(data.notification.description ?: "")
                setState(data.notification.state ?: NotificationState.INACTIVE)
                setOnClick { onItemClicked(data.notification.id ?: -1) }
            }

            // https://www.colorhexa.com/394989
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