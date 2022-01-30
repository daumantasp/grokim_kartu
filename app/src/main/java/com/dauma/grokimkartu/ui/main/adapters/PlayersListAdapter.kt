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
import com.dauma.grokimkartu.repositories.players.entities.PlayerIconStatus
import com.dauma.grokimkartu.ui.viewelements.InitialsViewElement
import com.dauma.grokimkartu.ui.viewelements.SpinnerViewElement

class PlayersListAdapter(
    val context: Context,
    private val playersListData: List<PlayersListData>,
    private val utils: Utils,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<PlayersListAdapter.ViewHolder>() {
    private val photoIconBackgroundDrawable: Drawable?

    init {
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
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.player_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playerData = playersListData[position]
        holder.nameTextView.text = playerData.player.name
        holder.instrumentTextView.text = playerData.player.instrument
        holder.cityTextView.text = playerData.player.city

        holder.playerItemContainer.setOnClickListener {
            this.onItemClicked(playerData.player.userId ?: "")
        }

        fun bindOrUnbindPhoto() {
            if (playerData.player.icon.icon != null) {
                val ovalPhoto = utils.imageUtils.getOvalBitmap(playerData.player.icon.icon!!)
                holder.photoIcon.setImageBitmap(ovalPhoto)
                holder.spinnerViewElement.showAnimation(false)
                holder.initialsViewElement.visibility = View.GONE
                holder.photoIcon.visibility = View.VISIBLE
            }
        }
        fun bindOrUnbindInitials() {
            if (playerData.player.icon.icon == null) {
                val initials = utils.stringUtils.getInitials(playerData.player.name ?: "")
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

        val iconStatus = playerData.player.icon.status
        if (iconStatus == PlayerIconStatus.DOWNLOADED_ICON_NOT_SET || iconStatus == PlayerIconStatus.DOWNLOADED_ICON_SET) {
            bindOrUnbindPhoto()
            bindOrUnbindInitials()
        } else if (iconStatus == PlayerIconStatus.DOWNLOAD_IN_PROGRESS) {
            bindDownloadInProgress()
        } else if (iconStatus == PlayerIconStatus.NEED_TO_DOWNLOAD) {
            bindDownloadInProgress()
            playerData.player.icon.loadIfNeeded { photo, e ->
                bindOrUnbindPhoto()
                bindOrUnbindInitials()
            }
        }
    }

    override fun getItemCount(): Int {
        return playersListData.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerItemContainer = view.findViewById<LinearLayout>(R.id.playerItemContainer)
        val nameTextView = view.findViewById<TextView>(R.id.playerName)
        val instrumentTextView = view.findViewById<TextView>(R.id.playerInstrument)
        val cityTextView = view.findViewById<TextView>(R.id.playerCity)
        val initialsViewElement = view.findViewById<InitialsViewElement>(R.id.initialsViewElement)
        val photoIcon = view.findViewById<ImageView>(R.id.playerIconImageView)
        val spinnerViewElement = view.findViewById<SpinnerViewElement>(R.id.spinnerViewElement)
    }
}