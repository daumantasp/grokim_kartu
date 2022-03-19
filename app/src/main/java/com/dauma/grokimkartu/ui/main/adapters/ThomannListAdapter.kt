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
import com.dauma.grokimkartu.ui.viewelements.InitialsViewElement
import com.dauma.grokimkartu.ui.viewelements.SpinnerViewElement
import java.util.*

class ThomannListAdapter(
    val context: Context,
    private val thomannListData: List<ThomannsListData>,
    private val utils: Utils,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<ThomannListAdapter.ViewHolder>() {
    private val photoIconBackgroundDrawable: Drawable?

    init {
        // TODO: Duplicates in playerItem. Refactor
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.thomann_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val thomannData = thomannListData[position]
        holder.userTextView.text = thomannData.thomann.name
        holder.cityTextView.text = thomannData.thomann.city
        val validUntil = this.utils.timeUtils.format(thomannData.thomann.validUntil?.toDate() ?: Date())
        holder.validUntilTextView.text = validUntil
        if (thomannData.thomann.isLocked == true) {
            holder.lockedUnlockedIconImageView.setImageResource(R.drawable.locked_icon)
        } else {
            holder.lockedUnlockedIconImageView.setImageResource(R.drawable.unlocked_icon)
        }

        holder.thomannItemLinearLayout.setOnClickListener {
            this.onItemClicked(thomannData.thomann.id ?: "")
        }

        // TODO: Duplicates in playerItem. Refactor
        fun bindOrUnbindPhoto() {
            if (thomannData.thomann.icon?.icon != null) {
                val ovalPhoto = utils.imageUtils.getOvalBitmap(thomannData.thomann.icon.icon!!)
                holder.photoIcon.setImageBitmap(ovalPhoto)
                holder.spinnerViewElement.showAnimation(false)
                holder.initialsViewElement.visibility = View.GONE
                holder.photoIcon.visibility = View.VISIBLE
            }
        }
        fun bindOrUnbindInitials() {
            if (thomannData.thomann.icon?.icon == null) {
                val initials = utils.stringUtils.getInitials(thomannData.thomann.name ?: "")
                holder.initialsViewElement.setInitials(initials)
                holder.spinnerViewElement.showAnimation(false)
                holder.photoIcon.visibility = View.GONE
                holder.initialsViewElement.visibility = View.VISIBLE
            }
        }
        fun bindDownloadInProgress() {
            holder.photoIcon.setImageDrawable(photoIconBackgroundDrawable)
            holder.initialsViewElement.visibility = View.GONE
            holder.photoIcon.visibility = View.VISIBLE
            holder.spinnerViewElement.showAnimation(true)
        }

        val iconStatus = thomannData.thomann.icon?.status
        if (iconStatus == ThomannPlayerIconStatus.DOWNLOADED_ICON_NOT_SET || iconStatus == ThomannPlayerIconStatus.DOWNLOADED_ICON_SET) {
            bindOrUnbindPhoto()
            bindOrUnbindInitials()
        } else if (iconStatus == ThomannPlayerIconStatus.DOWNLOAD_IN_PROGRESS) {
            bindDownloadInProgress()
        } else if (iconStatus == ThomannPlayerIconStatus.NEED_TO_DOWNLOAD) {
            bindDownloadInProgress()
            thomannData.thomann.icon.loadIfNeeded { photo, e ->
                bindOrUnbindPhoto()
                bindOrUnbindInitials()
            }
        }
    }

    override fun getItemCount(): Int {
        return thomannListData.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thomannItemLinearLayout = view.findViewById<LinearLayout>(R.id.thomannItemLinearLayout)
        val userTextView = view.findViewById<TextView>(R.id.userTextView)
        val cityTextView = view.findViewById<TextView>(R.id.cityTextView)
        val validUntilTextView = view.findViewById<TextView>(R.id.validUntilTextView)
        val initialsViewElement = view.findViewById<InitialsViewElement>(R.id.initialsViewElement)
        val photoIcon = view.findViewById<ImageView>(R.id.thomannPlayerIconImageView)
        val spinnerViewElement = view.findViewById<SpinnerViewElement>(R.id.spinnerViewElement)
        val lockedUnlockedIconImageView = view.findViewById<ImageView>(R.id.lockedUnlockedIconImageView)
    }
}