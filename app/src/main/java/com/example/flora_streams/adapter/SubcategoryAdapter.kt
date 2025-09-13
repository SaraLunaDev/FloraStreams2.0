package com.example.flora_streams.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flora_streams.R
import com.example.flora_streams.files.Subcategory
import androidx.core.net.toUri
import com.example.flora_streams.files.openAceStream

class SubcategoryAdapter(private val subcategories: List<Subcategory>) :
    RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder>() {

    inner class SubcategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvSubcategoryName: TextView = view.findViewById(R.id.tvSubcategoryName)
        val llUrls: LinearLayout = view.findViewById(R.id.llUrls)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubcategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subcategory, parent, false)
        return SubcategoryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SubcategoryViewHolder,
        position: Int
    ) {
        val subcategory = subcategories[position]
        holder.tvSubcategoryName.text = subcategory.name

        holder.llUrls.removeAllViews()

        subcategory.urls.forEach { url ->
            val button = Button(holder.view.context).apply {
                text = url.name
                setOnClickListener {
                    openAceStream(holder.view.context, url.url)
                }
            }
            holder.llUrls.addView(button)
        }

    }

    override fun getItemCount() = subcategories.size

}