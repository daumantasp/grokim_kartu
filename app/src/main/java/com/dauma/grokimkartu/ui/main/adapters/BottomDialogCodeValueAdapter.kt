package com.dauma.grokimkartu.ui.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dauma.grokimkartu.R

class BottomDialogCodeValueAdapter(
    private val codeValueList: List<CodeValue>,
    private val onItemClicked: (String) -> Unit
)
    : RecyclerView.Adapter<BottomDialogCodeValueAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.code_value_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val codeValueData = codeValueList[position]
        holder.titleTextView.text = codeValueData.value
        holder.itemView.setOnClickListener {
            onItemClicked(codeValueData.code)
        }
    }

    override fun getItemCount(): Int {
        return codeValueList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
    }
}

data class CodeValue(
    val code: String,
    val value: String
)