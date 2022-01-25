package com.dauma.grokimkartu.ui.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.general.utils.image.ImageUtils
import com.dauma.grokimkartu.repositories.players.entities.Player
import com.dauma.grokimkartu.repositories.players.entities.PlayerIconStatus
import com.dauma.grokimkartu.ui.viewelements.SpinnerViewElement

class PlayersListAdapter(
    private val playersData: List<Player>,
    private val imageUtils: ImageUtils,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<PlayersListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.player_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = playersData[position]
        holder.nameTextView.text = player.name
        holder.instrumentTextView.text = player.instrument
        holder.cityTextView.text = player.city

        holder.playerItemContainer.setOnClickListener {
            this.onItemClicked(player.userId ?: "")
        }

        if (player.icon.status == PlayerIconStatus.DOWNLOADED_ICON_SET) {
            holder.photoIcon.setImageBitmap(player.icon.icon)
        } else if (player.icon.status == PlayerIconStatus.DOWNLOADED_ICON_NOT_SET) {
            holder.photoIcon.setImageResource(R.drawable.user)
        } else if (player.icon.status == PlayerIconStatus.DOWNLOAD_IN_PROGRESS) {
            holder.photoIcon.visibility = View.GONE
            holder.spinnerViewElement.showAnimation(true)
        } else if (player.icon.status == PlayerIconStatus.NEED_TO_DOWNLOAD) {
            holder.photoIcon.visibility = View.GONE
            holder.spinnerViewElement.showAnimation(true)
            player.icon.loadIfNeeded { photo, e ->
                if (player.icon.status == PlayerIconStatus.DOWNLOADED_ICON_SET) {
                    if (photo != null) {
                        holder.photoIcon.setImageBitmap(imageUtils.getCircularBitmap(photo))
                    } else {
                        holder.photoIcon.setImageResource(R.drawable.user)
                    }
                } else if (player.icon.status == PlayerIconStatus.DOWNLOADED_ICON_NOT_SET) {
                    holder.photoIcon.setImageResource(R.drawable.user)
                }
                holder.spinnerViewElement.showAnimation(false)
                holder.photoIcon.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return playersData.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerItemContainer = view.findViewById<LinearLayout>(R.id.playerItemContainer)
        val idTextView = view.findViewById<TextView>(R.id.userId)
        val nameTextView = view.findViewById<TextView>(R.id.playerName)
        val instrumentTextView = view.findViewById<TextView>(R.id.playerInstrument)
        val cityTextView = view.findViewById<TextView>(R.id.playerCity)
        val photoIcon = view.findViewById<ImageView>(R.id.playerIconImageView)
        val spinnerViewElement = view.findViewById<SpinnerViewElement>(R.id.spinnerViewElement)
    }
}