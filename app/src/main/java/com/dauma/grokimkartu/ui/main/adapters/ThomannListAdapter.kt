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
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.DummyCell
import com.dauma.grokimkartu.general.IconStatus
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.time.CustomDateTimeFormatPattern
import com.dauma.grokimkartu.repositories.thomanns.entities.Thomann
import com.dauma.grokimkartu.ui.viewelements.InitialsViewElement
import com.dauma.grokimkartu.ui.viewelements.SpinnerViewElement
import java.util.*

class ThomannListAdapter(
    val context: Context,
    private val utils: Utils,
    private val onItemClicked: (Int) -> Unit,
    private val loadNextPage: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val photoIconBackgroundDrawable: Drawable?

    var data: List<Any>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val differ: AsyncListDiffer<Any> = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            if (oldItem is Thomann && newItem is Thomann)
                return oldItem.id == newItem.id
            return false
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            if (oldItem is Thomann && newItem is Thomann)
                return oldItem == newItem
            return false
        }
    })

    companion object {
        private const val THOMANN = 1
        private const val LAST = 2
    }

    init {
        // TODO: Duplicates in playerItem. Refactor
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
        if (data[position] is DummyCell) {
            return LAST
        } else if (data[position] is Thomann) {
            return THOMANN
        }
        return THOMANN
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == THOMANN) {
            return ThomannViewHolder(context, LayoutInflater.from(parent.context).inflate(R.layout.thomann_item, parent, false), utils, photoIconBackgroundDrawable, onItemClicked)
        } else if (viewType == LAST) {
            return ThomannLastViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.thomann_last_item, parent, false), loadNextPage)
        }
        return ThomannViewHolder(context, LayoutInflater.from(parent.context).inflate(R.layout.thomann_item, parent, false), utils, photoIconBackgroundDrawable, onItemClicked)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = data[position]
        if (holder is ThomannLastViewHolder && itemData is DummyCell) {
            holder.bind(itemData)
        } else if (holder is ThomannViewHolder && itemData is Thomann) {
            holder.bind(itemData)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ThomannViewHolder(
        private val context: Context,
        val view: View,
        private val utils: Utils,
        private val photoIconBackgroundDrawable: Drawable?,
        private val onItemClicked: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(view) {
        val thomannItemLinearLayout = view.findViewById<LinearLayout>(R.id.thomann_item_linear_layout)
        val userTextView = view.findViewById<TextView>(R.id.user_text_view)
        val cityTextView = view.findViewById<TextView>(R.id.city_text_view)
        val validUntilTextView = view.findViewById<TextView>(R.id.valid_until_text_view)
        val initialsViewElement = view.findViewById<InitialsViewElement>(R.id.initials_view_element)
        val photoIcon = view.findViewById<ImageView>(R.id.thomann_player_icon_image_view)
        val lockedUnlockedIconImageView = view.findViewById<ImageView>(R.id.locked_unlocked_icon_image_view)

        fun bind(thomann: Thomann) {
            userTextView.text = thomann.user?.name ?: context.getString(R.string.conversation_unknown_user)
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

            fun bindIconIfPossible() {
                thomann.iconLoader.icon?.let {
                    val ovalPhoto = utils.imageUtils.getOvalBitmap(it)
                    photoIcon.setImageBitmap(ovalPhoto)
                    initialsViewElement.visibility = View.GONE
                    photoIcon.visibility = View.VISIBLE
                }
            }
            fun bindInitials() {
                val initials = utils.stringUtils.getInitials(thomann.user?.name ?: "")
                initialsViewElement.setInitials(initials)
                photoIcon.visibility = View.GONE
                initialsViewElement.visibility = View.VISIBLE
            }

            bindInitials()
            if (thomann.iconLoader.status == IconStatus.ICON_DOWNLOADED) {
                bindIconIfPossible()
            } else if (thomann.iconLoader.status == IconStatus.NEED_TO_DOWNLOAD) {
//                thomann.iconLoader.loadIcon { icon ->
//                    if (icon != null) {
//                        bindIconIfPossible()
//                    }
//                }
            }
        }
    }

    class ThomannLastViewHolder(
        view: View,
        private val loadNextPage: () -> Unit
    ) : RecyclerView.ViewHolder(view) {
        val spinnerViewElement = view.findViewById<SpinnerViewElement>(R.id.spinner_view_element)

        fun bind(data: DummyCell) {
            spinnerViewElement.showAnimation(true)
            loadNextPage()
        }
    }
}