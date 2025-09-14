package com.example.flora_streams.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flora_streams.R
import com.example.flora_streams.data.JsonUrlEntity

class JsonUrlAdapter (
    private val list: MutableList<JsonUrlEntity>,
    private val onEdit: (JsonUrlEntity) -> Unit,
    private val onDelete: (JsonUrlEntity) -> Unit
) : RecyclerView.Adapter<JsonUrlAdapter.JsonUrlViewHolder>() {

    inner class JsonUrlViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvJsonName: TextView = view.findViewById(R.id.tvJsonName)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JsonUrlViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_json_url, parent, false)
        return JsonUrlViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: JsonUrlViewHolder,
        position: Int
    ) {
        val item = list[position]
        holder.tvJsonName.text = item.name

        holder.btnEdit.setOnClickListener { onEdit(item) }
        holder.btnDelete.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount() = list.size

    fun updateList(newList: List<JsonUrlEntity>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

}