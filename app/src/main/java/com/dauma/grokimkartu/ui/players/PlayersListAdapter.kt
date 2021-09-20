package com.dauma.grokimkartu.ui.players

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dauma.grokimkartu.R
import com.dauma.grokimkartu.models.Player

class PlayersListAdapter(
    private var playersData: List<Player>
) : RecyclerView.Adapter<PlayersListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.player_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = playersData[position]
        holder.idTextView.text = player.id.toString()
        holder.nameTextView.text = player.name
        holder.instrumentTextView.text = player.instrument
    }

    override fun getItemCount(): Int {
        return playersData.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idTextView = view.findViewById<TextView>(R.id.playerId)
        val nameTextView = view.findViewById<TextView>(R.id.playerName)
        val instrumentTextView = view.findViewById<TextView>(R.id.playerInstrument)
    }
}