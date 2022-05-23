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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannPlayerIconStatus
import com.dauma.grokimkartu.ui.viewelements.ButtonViewElement
import com.dauma.grokimkartu.ui.viewelements.InitialsViewElement
import com.dauma.grokimkartu.ui.viewelements.RowViewElement
import com.dauma.grokimkartu.ui.viewelements.SpinnerViewElement
import java.sql.Date

class ThomannDetailsAdapter(
    private val context: Context,
    private val data: List<ThomannDetailsListData>,
    private val utils: Utils,
    private val onItemClicked: (Int) -> Unit,
    private val onLeaveClicked: () -> Unit,
    private val onKickClicked: (Int) -> Unit
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val PHOTO = 1
        private const val ROW = 2
        private const val STATUS_ROW = 3
        private const val USER = 4
        private const val BUTTON = 5
    }

    override fun getItemViewType(position: Int): Int {
        if (data[position] is ThomannDetailsListPhotoData) {
            return PHOTO
        } else if (data[position] is ThomannDetailsListRowData) {
            return ROW
        } else if (data[position] is ThomannDetailsListStatusRowData) {
            return STATUS_ROW
        } else if (data[position] is ThomannDetailsListUserData) {
            return USER
        } else if (data[position] is ThomannDetailsListButtonData) {
            return BUTTON
        }
        return ROW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == PHOTO) {
            return PhotoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.thomann_details_photo_item, parent, false), utils)
        } else if (viewType == ROW) {
            return RowViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.thomann_details_row_item, parent, false))
        } else if (viewType == STATUS_ROW) {
            return StatusRowViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.thomann_details_row_item, parent, false))
        } else if (viewType == USER) {
            return UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.thomann_details_user_item, parent, false), utils, onItemClicked, onLeaveClicked, onKickClicked)
        } else if (viewType == BUTTON) {
            return ButtonViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.thomann_details_button_item, parent, false))
        }
        return RowViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.thomann_details_row_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = data[position]
        if (holder is PhotoViewHolder && itemData is ThomannDetailsListPhotoData) {
            holder.bind(itemData)
        } else if (holder is RowViewHolder && itemData is ThomannDetailsListRowData) {
            holder.bind(itemData)
        } else if (holder is StatusRowViewHolder && itemData is ThomannDetailsListStatusRowData) {
            holder.bind(itemData)
        } else if (holder is UserViewHolder && itemData is ThomannDetailsListUserData) {
            holder.bind(itemData)
        } else if (holder is ButtonViewHolder && itemData is ThomannDetailsListButtonData) {
            holder.bind(itemData)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private class PhotoViewHolder(view: View, private val utils: Utils) : RecyclerView.ViewHolder(view) {
        val initialsViewElement = view.findViewById<InitialsViewElement>(R.id.initialsViewElement)
        val photoImageView = view.findViewById<ImageView>(R.id.photoImageView)
        val lockedUnlockedIconImageView = view.findViewById<ImageView>(R.id.lockedUnlockedIconImageView)

        fun bind(data: ThomannDetailsListPhotoData) {
            if (data.photo == null) {
                val initials = utils.stringUtils.getInitials(data.name ?: "")
                initialsViewElement.setInitials(initials)
                photoImageView.visibility = View.GONE
                initialsViewElement.visibility = View.VISIBLE
            } else {
                photoImageView.setImageBitmap(data.photo)
                initialsViewElement.visibility = View.GONE
                photoImageView.visibility = View.VISIBLE
            }

            if (data.isLocked == true) {
                lockedUnlockedIconImageView.setImageResource(R.drawable.locked_icon)
            } else {
                lockedUnlockedIconImageView.setImageResource(R.drawable.unlocked_icon)
            }
            lockedUnlockedIconImageView.setOnClickListener {
                data.onClick()
            }
        }
    }

    private class RowViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rowViewElement = view.findViewById<RowViewElement>(R.id.thomannDetailsRowViewElement)

        fun bind(data: ThomannDetailsListRowData) {
            rowViewElement.setTitle(data.title)
            rowViewElement.setValue(data.value ?: "")
        }
    }

    private class StatusRowViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val rowViewElement = view.findViewById<RowViewElement>(R.id.thomannDetailsRowViewElement)

        fun bind(data: ThomannDetailsListStatusRowData) {
            rowViewElement.setTitle(data.title)

            val typedValue = TypedValue()
            val statusText: String
            val statusColor: Int
            if (data.isLocked == true) {
                statusText = view.context.getText(R.string.thomann_details_status_locked).toString()
                view.context.theme.resolveAttribute(R.attr.lockedIconBackgroundColor, typedValue, true)
            } else {
                statusText = view.context.getText(R.string.thomann_details_status_unlocked).toString()
                view.context.theme.resolveAttribute(R.attr.unlockedIconBackgroundColor, typedValue, true)
            }
            statusColor = typedValue.data

            rowViewElement.setValue(statusText)
            rowViewElement.setValueColor(statusColor)
            rowViewElement.showIcon(true)
            rowViewElement.setCustomIconIfNeeded(view.context.getDrawable(R.drawable.ic_change_lock))

            rowViewElement.setOnClick(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    data.onClick()
                }
            })
        }
    }

    private class UserViewHolder(
        private val view: View,
        private val utils: Utils,
        private val onItemClicked: (Int) -> Unit,
        private val onLeaveClicked: () -> Unit,
        private val onKickClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val photoIconBackgroundDrawable: Drawable?
        val rootLayout = view.findViewById<LinearLayout>(R.id.thomannDetailsUserItemLayout)
        val initialsViewElement = view.findViewById<InitialsViewElement>(R.id.initialsViewElement)
        val userIconImageView = view.findViewById<ImageView>(R.id.userIconImageView)
        val spinnerViewElement = view.findViewById<SpinnerViewElement>(R.id.spinnerViewElement)
        val userNameTextView = view.findViewById<TextView>(R.id.userName)
        val userAmountTextView = view.findViewById<TextView>(R.id.userAmount)
        val userJoinedDateTextView = view.findViewById<TextView>(R.id.userJoinedDate)
        val leaveOrKickTextView = view.findViewById<TextView>(R.id.leaveTextView)

        init {
            // You may need to call mutate() on the drawable or else all icons are affected.
            photoIconBackgroundDrawable = ContextCompat.getDrawable(view.context, R.drawable.oval_background)?.mutate()
            val typedValue = TypedValue()
            view.context.theme.resolveAttribute(R.attr.playerItemPhotoBackgroundColor, typedValue, true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                photoIconBackgroundDrawable?.colorFilter = BlendModeColorFilter(typedValue.data, BlendMode.SRC_ATOP)
            } else {
                @Suppress("DEPRECATION")
                photoIconBackgroundDrawable?.setColorFilter(typedValue.data, PorterDuff.Mode.SRC_ATOP)
            }
        }

        fun bind(data: ThomannDetailsListUserData) {
            userNameTextView.setText(data.user.user?.name ?: "")
            val userAmountText = view.context.getText(R.string.thomann_details_user_amount).toString()
            val formattedUserAmountText = userAmountText.replace("{{amount}}", data.user.amount.toString())
            userAmountTextView.setText(formattedUserAmountText)
            if (data.user.createdAt != null) {
                val joinDate = Date(data.user.createdAt!!.time)
                val formattedJoinDate = utils.timeUtils.format(joinDate)
                val joinDateText = view.context.getText(R.string.thomann_details_user_join_date).toString()
                val formattedJoinDateText = joinDateText.replace("{{joinDate}}", formattedJoinDate)
                userJoinedDateTextView.setText(formattedJoinDateText)
            } else {
                userJoinedDateTextView.setText("")
            }
            if (data.user.isCurrentUser == true && (data.user.actions ?: listOf()).contains("QUIT")) {
                leaveOrKickTextView.setText(view.context.getString(R.string.thomann_details_leave))
                leaveOrKickTextView.visibility = View.VISIBLE

                // TODO: should not disable onClick in viewHolder, viewModel should prevent the action
                leaveOrKickTextView.setOnClickListener {
                    this.onLeaveClicked()
                }
                rootLayout.setOnClickListener {}
            } else if (data.user.isCurrentUser == false && (data.user.actions ?: listOf()).contains("KICK")) {
                leaveOrKickTextView.setText(view.context.getString(R.string.thomann_details_kick))
                leaveOrKickTextView.visibility = View.VISIBLE

                // TODO: should not disable onClick in viewHolder, viewModel should prevent the action
                leaveOrKickTextView.setOnClickListener {
                    this.onKickClicked(data.user.user?.id ?: -1)
                }
                rootLayout.setOnClickListener {
                    this.onItemClicked(data.user.user?.id ?: -1)
                }
            }
            else {
                leaveOrKickTextView.visibility = View.GONE
                leaveOrKickTextView.setOnClickListener {}
                rootLayout.setOnClickListener {
                    this.onItemClicked(data.user.id ?: -1)
                }
            }

            // TODO: refactor
            fun bindOrUnbindPhoto() {
                if (data.user.icon?.icon != null) {
                    val ovalPhoto = utils.imageUtils.getOvalBitmap(data.user.icon.icon!!)
                    userIconImageView.setImageBitmap(ovalPhoto)
                    spinnerViewElement.showAnimation(false)
                    initialsViewElement.visibility = View.GONE
                    userIconImageView.visibility = View.VISIBLE
                }
            }
            fun bindOrUnbindInitials() {
                if (data.user.icon?.icon == null) {
                    val initials = utils.stringUtils.getInitials(data.user.user?.name ?: "")
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

            val iconStatus = data.user.icon?.status
            if (iconStatus == ThomannPlayerIconStatus.DOWNLOADED_ICON_NOT_SET || iconStatus == ThomannPlayerIconStatus.DOWNLOADED_ICON_SET) {
                bindOrUnbindPhoto()
                bindOrUnbindInitials()
            } else if (iconStatus == ThomannPlayerIconStatus.DOWNLOAD_IN_PROGRESS) {
                bindDownloadInProgress()
            } else if (iconStatus == ThomannPlayerIconStatus.NEED_TO_DOWNLOAD) {
                bindDownloadInProgress()
                data.user.icon.loadIfNeeded { photo, e ->
                    bindOrUnbindPhoto()
                    bindOrUnbindInitials()
                }
            }
        }
    }

    private class ButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buttonViewElement = view.findViewById<ButtonViewElement>(R.id.thomannDetailsButtonViewElement)

        fun bind(data: ThomannDetailsListButtonData) {
            buttonViewElement.setText(data.title)
            buttonViewElement.setOnClick(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    data.onClick()
                }
            })
        }
    }
}