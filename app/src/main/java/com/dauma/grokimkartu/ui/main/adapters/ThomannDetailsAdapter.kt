package com.dauma.grokimkartu.ui.main.adapters

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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.IconStatus
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDateTimeFormatPattern
import com.dauma.grokimkartu.ui.DialogsManager
import com.dauma.grokimkartu.ui.YesNoDialogData
import com.dauma.grokimkartu.ui.viewelements.ButtonViewElement
import com.dauma.grokimkartu.ui.viewelements.InitialsViewElement
import com.dauma.grokimkartu.ui.viewelements.RowViewElement
import java.sql.Date

class ThomannDetailsAdapter(
    private val data: List<Any>,
    private val utils: Utils,
    private val dialogsManager: DialogsManager?,
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
        if (data[position] is ThomannDetailsPhotoData) {
            return PHOTO
        } else if (data[position] is ThomannDetailsRowData) {
            return ROW
        } else if (data[position] is ThomannDetailsStatusData) {
            return STATUS_ROW
        } else if (data[position] is ThomannDetailsUserData) {
            return USER
        } else if (data[position] is ThomannDetailsButtonData) {
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
            return UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.thomann_details_user_item, parent, false), utils, dialogsManager, onItemClicked, onLeaveClicked, onKickClicked)
        } else if (viewType == BUTTON) {
            return ButtonViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.thomann_details_button_item, parent, false), utils, dialogsManager)
        }
        return RowViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.thomann_details_row_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = data[position]
        if (holder is PhotoViewHolder && itemData is ThomannDetailsPhotoData) {
            holder.bind(itemData)
        } else if (holder is RowViewHolder && itemData is ThomannDetailsRowData) {
            holder.bind(itemData)
        } else if (holder is StatusRowViewHolder && itemData is ThomannDetailsStatusData) {
            holder.bind(itemData)
        } else if (holder is UserViewHolder && itemData is ThomannDetailsUserData) {
            holder.bind(itemData)
        } else if (holder is ButtonViewHolder && itemData is ThomannDetailsButtonData) {
            holder.bind(itemData)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private class PhotoViewHolder(view: View, private val utils: Utils) : RecyclerView.ViewHolder(view) {
        val initialsViewElement = view.findViewById<InitialsViewElement>(R.id.initials_view_element)
        val photoImageView = view.findViewById<ImageView>(R.id.photo_image_view)
        val lockedUnlockedIconImageView = view.findViewById<ImageView>(R.id.locked_unlocked_icon_image_view)
        val profilePhotoOrInitialsConstraintLayout = view.findViewById<ConstraintLayout>(R.id.profile_photo_or_initials_constraint_layout)

        fun bind(data: ThomannDetailsPhotoData) {
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
            profilePhotoOrInitialsConstraintLayout.visibility = View.VISIBLE

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
        val rowViewElement = view.findViewById<RowViewElement>(R.id.thomann_details_row_view_element)

        fun bind(data: ThomannDetailsRowData) {
            rowViewElement.setTitle(data.title)
            rowViewElement.setValue(data.value ?: "")
        }
    }

    private class StatusRowViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val rowViewElement = view.findViewById<RowViewElement>(R.id.thomann_details_row_view_element)

        fun bind(data: ThomannDetailsStatusData) {
            rowViewElement.setTitle(data.title)

            val typedValue = TypedValue()
            val statusText: String
            val statusColor: Int
            if (data.isLocked == true) {
                statusText = view.context.getText(R.string.thomann_details_status_locked).toString()
                view.context.theme.resolveAttribute(R.attr.locked_icon_background_color, typedValue, true)
            } else {
                statusText = view.context.getText(R.string.thomann_details_status_unlocked).toString()
                view.context.theme.resolveAttribute(R.attr.unlocked_icon_background_color, typedValue, true)
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
        private val dialogsManager: DialogsManager?,
        private val onItemClicked: (Int) -> Unit,
        private val onLeaveClicked: () -> Unit,
        private val onKickClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val photoIconBackgroundDrawable: Drawable?
        val rootLayout = view.findViewById<LinearLayout>(R.id.thomann_details_user_item_layout)
        val initialsViewElement = view.findViewById<InitialsViewElement>(R.id.initials_view_element)
        val userIconImageView = view.findViewById<ImageView>(R.id.user_icon_image_view)
        val userNameTextView = view.findViewById<TextView>(R.id.user_name)
        val userAmountTextView = view.findViewById<TextView>(R.id.user_amount)
        val userJoinedDateTextView = view.findViewById<TextView>(R.id.user_joined_date)
        val leaveOrKickTextView = view.findViewById<TextView>(R.id.leave_text_view)

        init {
            // You may need to call mutate() on the drawable or else all icons are affected.
            photoIconBackgroundDrawable = ContextCompat.getDrawable(view.context, R.drawable.oval_background)?.mutate()
            val typedValue = TypedValue()
            view.context.theme.resolveAttribute(R.attr.photo_placeholder_color, typedValue, true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                photoIconBackgroundDrawable?.colorFilter = BlendModeColorFilter(typedValue.data, BlendMode.SRC_ATOP)
            } else {
                @Suppress("DEPRECATION")
                photoIconBackgroundDrawable?.setColorFilter(typedValue.data, PorterDuff.Mode.SRC_ATOP)
            }
        }

        fun bind(data: ThomannDetailsUserData) {
            userNameTextView.setText(data.user.user?.name ?: "")
            val userAmountText = view.context.getText(R.string.thomann_details_user_amount).toString()
            val formattedUserAmountText = userAmountText.replace("{{amount}}", data.user.amount.toString())
            userAmountTextView.setText(formattedUserAmountText)
            if (data.user.createdAt != null) {
                val joinDate = Date(data.user.createdAt!!.time)
                val formattedJoinDate = utils.timeUtils.format(joinDate, CustomDateTimeFormatPattern.yyyyMMdd)
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
                    dialogsManager?.showYesNoDialog(YesNoDialogData(
                        text = view.context.getString(R.string.thomann_details_leave_confirmation_text),
                        positiveText = view.context.getString(R.string.thomann_details_leave_confirmation_positive),
                        negativeText = view.context.getString(R.string.thomann_details_leave_confirmation_negative),
                        cancelable = true,
                        onPositiveButtonClick = { this.onLeaveClicked() }
                    ))
                }
                rootLayout.setOnClickListener {}
            } else if (data.user.isCurrentUser == false && (data.user.actions ?: listOf()).contains("KICK")) {
                leaveOrKickTextView.setText(view.context.getString(R.string.thomann_details_kick))
                leaveOrKickTextView.visibility = View.VISIBLE

                // TODO: should not disable onClick in viewHolder, viewModel should prevent the action
                leaveOrKickTextView.setOnClickListener {
                    val kickText = view.context.getText(R.string.thomann_details_kick_confirmation_text).toString()
                    val formattedKickText = kickText.replace("{{userName}}", data.user.user?.name ?: "")
                    dialogsManager?.showYesNoDialog(YesNoDialogData(
                        text = formattedKickText,
                        positiveText = view.context.getString(R.string.thomann_details_kick_confirmation_positive),
                        negativeText = view.context.getString(R.string.thomann_details_kick_confirmation_negative),
                        cancelable = true,
                        onPositiveButtonClick = { this.onKickClicked(data.user.user?.id ?: -1) }
                    ))
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

            fun bindIconIfPossible() {
                data.user.iconLoader.icon?.let {
                    val ovalPhoto = utils.imageUtils.getOvalBitmap(it)
                    userIconImageView.setImageBitmap(ovalPhoto)
                    initialsViewElement.visibility = View.GONE
                    userIconImageView.visibility = View.VISIBLE
                }
            }
            fun bindInitials() {
                val initials = utils.stringUtils.getInitials(data.user.user?.name ?: "")
                initialsViewElement.setInitials(initials)
                userIconImageView.visibility = View.GONE
                initialsViewElement.visibility = View.VISIBLE
            }

            bindInitials()
            if (data.user.iconLoader.status == IconStatus.ICON_DOWNLOADED) {
                bindIconIfPossible()
            } else if (data.user.iconLoader.status == IconStatus.NEED_TO_DOWNLOAD) {
                data.user.iconLoader.loadIcon { icon ->
                    if (icon != null) {
                        bindIconIfPossible()
                    }
                }
            }
        }
    }

    private class ButtonViewHolder(
        private val view: View,
        private val utils: Utils,
        private val dialogsManager: DialogsManager?,
    ) : RecyclerView.ViewHolder(view) {
        val buttonViewElement = view.findViewById<ButtonViewElement>(R.id.thomann_details_button_view_element)

        fun bind(data: ThomannDetailsButtonData) {
            buttonViewElement.setText(data.title)
            buttonViewElement.setOnClick(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    if (data.isCancelAction) {
                        dialogsManager?.showYesNoDialog(YesNoDialogData(
                            text = view.context.getString(R.string.thomann_details_cancel_confirmation_text),
                            positiveText = view.context.getString(R.string.thomann_details_cancel_confirmation_positive),
                            negativeText = view.context.getString(R.string.thomann_details_cancel_confirmation_negative),
                            cancelable = true,
                            onPositiveButtonClick = { data.onClick() }
                        ))
                    } else {
                        data.onClick()
                    }
                }
            })
        }
    }
}