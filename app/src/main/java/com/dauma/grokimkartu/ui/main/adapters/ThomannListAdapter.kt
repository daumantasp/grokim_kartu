package com.dauma.grokimkartu.ui.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dauma.grokimkartu.R
import java.text.SimpleDateFormat
import java.util.*

class ThomannListAdapter(
    private val thomannListData: List<ThomannsListData>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<ThomannListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.thomann_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val thomannData = thomannListData[position]
        holder.userTextView.text = thomannData.thomann.name
        holder.cityTextView.text = thomannData.thomann.city
        // TODO: duplicate logic, refactor
        val simpleDate = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val validUntil = simpleDate.format(thomannData.thomann?.validUntil?.toDate() ?: Date())
        holder.validUntilTextView.text = validUntil

        holder.thomannItemLinearLayout.setOnClickListener {
            this.onItemClicked(thomannData.thomann.id ?: "")
        }
    }

    override fun getItemCount(): Int {
        return thomannListData.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thomannItemLinearLayout = view.findViewById<LinearLayout>(R.id.thomannItemLinearLayout)
        val statusIconImageView = view.findViewById<ImageView>(R.id.statusIconImageView)
        val userTextView = view.findViewById<TextView>(R.id.userTextView)
        val cityTextView = view.findViewById<TextView>(R.id.cityTextView)
        val validUntilTextView = view.findViewById<TextView>(R.id.validUntilTextView)
    }
}