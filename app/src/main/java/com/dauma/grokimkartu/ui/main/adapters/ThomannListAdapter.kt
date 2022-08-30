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
import com.dauma.grokimkartu.general.DummyCell
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDateTimeFormatPattern
import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannPlayerIconStatus
import com.dauma.grokimkartu.ui.viewelements.InitialsViewElement
import com.dauma.grokimkartu.ui.viewelements.SpinnerViewElement
import java.util.*

class ThomannListAdapter(
    val context: Context,
    var thomannListData: MutableList<Any>,
    private val utils: Utils,
    private val onItemClicked: (Int) -> Unit,
    private val loadNextPage: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val photoIconBackgroundDrawable: Drawable?

    companion object {
        private const val THOMANN = 1
        private const val LAST = 2
    }

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

    override fun getItemViewType(position: Int): Int {
        if (thomannListData[position] is DummyCell) {
            return LAST
        } else if (thomannListData[position] is Thomann) {
            return THOMANN
        }
        return THOMANN
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == THOMANN) {
            return ThomannViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.thomann_item, parent, false), utils, photoIconBackgroundDrawable, onItemClicked)
        } else if (viewType == LAST) {
            return ThomannLastViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.thomann_last_item, parent, false), loadNextPage)
        }
        return ThomannViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.thomann_item, parent, false), utils, photoIconBackgroundDrawable, onItemClicked)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = thomannListData[position]
        if (holder is ThomannLastViewHolder && itemData is DummyCell) {
            holder.bind(itemData)
        } else if (holder is ThomannViewHolder && itemData is Thomann) {
            holder.bind(itemData)
        }
    }

    override fun getItemCount(): Int {
        return thomannListData.size
    }

    class ThomannViewHolder(
        val view: View,
        private val utils: Utils,
        private val photoIconBackgroundDrawable: Drawable?,
        private val onItemClicked: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(view) {
        val thomannItemLinearLayout = view.findViewById<LinearLayout>(R.id.thomannItemLinearLayout)
        val userTextView = view.findViewById<TextView>(R.id.userTextView)
        val cityTextView = view.findViewById<TextView>(R.id.cityTextView)
        val validUntilTextView = view.findViewById<TextView>(R.id.validUntilTextView)
        val initialsViewElement = view.findViewById<InitialsViewElement>(R.id.initialsViewElement)
        val photoIcon = view.findViewById<ImageView>(R.id.thomannPlayerIconImageView)
        val spinnerViewElement = view.findViewById<SpinnerViewElement>(R.id.spinnerViewElement)
        val lockedUnlockedIconImageView = view.findViewById<ImageView>(R.id.lockedUnlockedIconImageView)

        fun bind(thomann: Thomann) {
            userTextView.text = thomann.user?.name
            cityTextView.text = thomann.city
            val validUntil = this.utils.timeUtils.format(thomann.validUntil ?: Date(), CustomDateTimeFormatPattern.yyyyMMdd)
            validUntilTextView.text = validUntil
            if (thomann.isLocked == true) {
                lockedUnlockedIconImageView.setImageResource(R.drawable.locked_icon)
            } else {
                lockedUnlockedIconImageView.setImageResource(R.drawable.unlocked_icon)
            }

            thomannItemLinearLayout.setOnClickListener {
                this.onItemClicked(thomann.id ?: -1)
            }

            // TODO: Duplicates in playerItem. Refactor
            fun bindOrUnbindPhoto() {
                if (thomann.icon?.icon != null) {
                    val ovalPhoto = utils.imageUtils.getOvalBitmap(thomann.icon!!.icon!!)
                    photoIcon.setImageBitmap(ovalPhoto)
                    spinnerViewElement.showAnimation(false)
                    initialsViewElement.visibility = View.GONE
                    photoIcon.visibility = View.VISIBLE
                }
            }
            fun bindOrUnbindInitials() {
                if (thomann.icon?.icon == null) {
                    val initials = utils.stringUtils.getInitials(thomann.user?.name ?: "")
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

            val iconStatus = thomann.icon?.status
            if (iconStatus == ThomannPlayerIconStatus.DOWNLOADED_ICON_NOT_SET || iconStatus == ThomannPlayerIconStatus.DOWNLOADED_ICON_SET) {
                bindOrUnbindPhoto()
                bindOrUnbindInitials()
            } else if (iconStatus == ThomannPlayerIconStatus.DOWNLOAD_IN_PROGRESS) {
                bindDownloadInProgress()
            } else if (iconStatus == ThomannPlayerIconStatus.NEED_TO_DOWNLOAD) {
                bindDownloadInProgress()
                thomann.icon?.loadIfNeeded { photo, e ->
                    bindOrUnbindPhoto()
                    bindOrUnbindInitials()
                }
            }
        }
    }

    class ThomannLastViewHolder(
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