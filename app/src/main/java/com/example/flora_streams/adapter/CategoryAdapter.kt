package com.example.flora_streams.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flora_streams.files.Category
import com.example.flora_streams.R

class CategoryAdapter(private val categories: List<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)
        val rvSubcategories: RecyclerView = view.findViewById(R.id.rvSubcategories)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {
        val category = categories[position]
        holder.tvCategoryName.text = category.name

        holder.rvSubcategories.layoutManager = LinearLayoutManager(holder.view.context)
        holder.rvSubcategories.adapter = SubcategoryAdapter(category.subcategories)
    }

    override fun getItemCount() = categories.size

}