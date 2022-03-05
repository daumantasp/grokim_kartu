package com.dauma.grokimkartu.ui.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dauma.grokimkartu.R

class ThomannListAdapter(
    private val thomannListData: List<ThomannListData>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<ThomannListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.thomann_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return thomannListData.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thomannItemLinearLayout = view.findViewById<LinearLayout>(R.id.thomannItemLinearLayout)
        val statusIconImageView = view.findViewById<ImageView>(R.id.statusIconImageView)
        val userTextView = view.findViewById<TextView>(R.id.userTextView)
    }
}