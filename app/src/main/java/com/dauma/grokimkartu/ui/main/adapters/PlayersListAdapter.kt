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
import com.dauma.grokimkartu.general.IconStatus
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.players.entities.Player
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
        context.theme.resolveAttribute(R.attr.photo_placeholder_color, typedValue, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            photoIconBackgroundDrawable?.colorFilter = BlendModeColorFilter(typedValue.data, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            photoIconBackgroundDrawable?.setColorFilter(typedValue.data, PorterDuff.Mode.SRC_ATOP)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (playersListData[position] is DummyCell) {
            return LAST
        } else if (playersListData[position] is Player) {
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
        if (holder is PlayerLastViewHolder && itemData is DummyCell) {
            holder.bind(itemData)
        } else if (holder is PlayerViewHolder && itemData is Player) {
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
        val playerItemContainer = view.findViewById<LinearLayout>(R.id.player_item_container)
        val nameTextView = view.findViewById<TextView>(R.id.player_name_text_view)
        val instrumentTextView = view.findViewById<TextView>(R.id.player_instrument)
        val cityTextView = view.findViewById<TextView>(R.id.player_city)
        val initialsViewElement = view.findViewById<InitialsViewElement>(R.id.initials_view_element)
        val photoIcon = view.findViewById<ImageView>(R.id.player_icon_image_view)

        fun bind(player: Player) {
            nameTextView.text = player.name
            instrumentTextView.text = player.instrument
            cityTextView.text = player.city

            playerItemContainer.setOnClickListener {
                this.onItemClicked(player.userId ?: -1)
            }

            fun bindIconIfPossible() {
                player.iconLoader.icon?.let {
                    val ovalPhoto = utils.imageUtils.getOvalBitmap(it)
                    photoIcon.setImageBitmap(ovalPhoto)
                    initialsViewElement.visibility = View.GONE
                    photoIcon.visibility = View.VISIBLE
                }
            }
            fun bindInitials() {
                val initials = utils.stringUtils.getInitials(player.name ?: "")
                initialsViewElement.setInitials(initials)
                photoIcon.visibility = View.GONE
                initialsViewElement.visibility = View.VISIBLE
            }

            bindInitials()
            if (player.iconLoader.status == IconStatus.ICON_DOWNLOADED) {
                bindIconIfPossible()
            } else if (player.iconLoader.status == IconStatus.NEED_TO_DOWNLOAD) {
                player.iconLoader.loadIcon { icon ->
                    if (icon != null) {
                        bindIconIfPossible()
                    }
                }
            }
        }
    }

    class PlayerLastViewHolder(
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