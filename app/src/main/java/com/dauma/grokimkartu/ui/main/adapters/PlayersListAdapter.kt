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
    var playersListData: MutableList<Any>,
    private val utils: Utils,
    private val onItemClicked: (Int) -> Unit,
    private val loadNextPage: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val photoIconBackgroundDrawable: Drawable?

    companion object {
        private const val PLAYER = 1
        private const val LAST = 2
    }

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

    override fun getItemViewType(position: Int): Int {
        if (playersListData[position] is PlayerLastInPageData) {
            return LAST
        } else if (playersListData[position] is PlayersListData) {
            return PLAYER
        }
        return PLAYER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == PLAYER) {
            return PlayerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.player_item, parent, false), utils, photoIconBackgroundDrawable, onItemClicked)
        } else if (viewType == LAST) {
            return PlayerLastViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.player_last_item, parent, false), loadNextPage)
        }
        return PlayerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.player_item, parent, false), utils, photoIconBackgroundDrawable, onItemClicked)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = playersListData[position]
        if (holder is PlayerLastViewHolder && itemData is PlayerLastInPageData) {
            holder.bind(itemData)
        } else if (holder is PlayerViewHolder && itemData is PlayersListData) {
            holder.bind(itemData)
        }
    }

    override fun getItemCount(): Int {
        return playersListData.size
    }

    class PlayerViewHolder(
        val view: View,
        private val utils: Utils,
        private val photoIconBackgroundDrawable: Drawable?,
        private val onItemClicked: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(view) {
        val playerItemContainer = view.findViewById<LinearLayout>(R.id.playerItemContainer)
        val nameTextView = view.findViewById<TextView>(R.id.playerName)
        val instrumentTextView = view.findViewById<TextView>(R.id.playerInstrument)
        val cityTextView = view.findViewById<TextView>(R.id.playerCity)
        val initialsViewElement = view.findViewById<InitialsViewElement>(R.id.initialsViewElement)
        val photoIcon = view.findViewById<ImageView>(R.id.playerIconImageView)
        val spinnerViewElement = view.findViewById<SpinnerViewElement>(R.id.spinnerViewElement)

        fun bind(data: PlayersListData) {
            nameTextView.text = data.player.name
            instrumentTextView.text = data.player.instrument
            cityTextView.text = data.player.city

            playerItemContainer.setOnClickListener {
                this.onItemClicked(data.player.userId ?: -1)
            }

            fun bindOrUnbindPhoto() {
                if (data.player.icon.icon != null) {
                    val ovalPhoto = utils.imageUtils.getOvalBitmap(data.player.icon.icon!!)
                    photoIcon.setImageBitmap(ovalPhoto)
                    spinnerViewElement.showAnimation(false)
                    initialsViewElement.visibility = View.GONE
                    photoIcon.visibility = View.VISIBLE
                }
            }
            fun bindOrUnbindInitials() {
                if (data.player.icon.icon == null) {
                    val initials = utils.stringUtils.getInitials(data.player.name ?: "")
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

            val iconStatus = data.player.icon.status
            if (iconStatus == PlayerIconStatus.DOWNLOADED_ICON_NOT_SET || iconStatus == PlayerIconStatus.DOWNLOADED_ICON_SET) {
                bindOrUnbindPhoto()
                bindOrUnbindInitials()
            } else if (iconStatus == PlayerIconStatus.DOWNLOAD_IN_PROGRESS) {
                bindDownloadInProgress()
            } else if (iconStatus == PlayerIconStatus.NEED_TO_DOWNLOAD) {
                bindDownloadInProgress()
                data.player.icon.loadIfNeeded { photo, e ->
                    bindOrUnbindPhoto()
                    bindOrUnbindInitials()
                }
            }
        }
    }

    class PlayerLastViewHolder(
        view: View,
        private val loadNextPage: () -> Unit
    ) : RecyclerView.ViewHolder(view) {
        val spinnerViewElement = view.findViewById<SpinnerViewElement>(R.id.spinnerViewElement)

        fun bind(data: PlayerLastInPageData) {
            spinnerViewElement.showAnimation(true)
            loadNextPage()
        }
    }
}