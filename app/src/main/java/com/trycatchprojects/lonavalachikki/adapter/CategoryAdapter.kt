package com.trycatchprojects.lonavalachikki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.trycatchprojects.lonavalachikki.databinding.ItemviewCategoryBinding
import com.trycatchprojects.lonavalachikki.models.CategoryPojoItem

class CategoryAdapter(
    private val categoryList: List<CategoryPojoItem>,
    val onItemClick: (CategoryPojoItem) -> Unit,
): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    class CategoryViewHolder(val binding: ItemviewCategoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(ItemviewCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categories=categoryList[position]
        holder.binding.apply {
            categoryName.text=categories.cat_name
            Picasso.get().load(categories.cat_image).into(categoryImage)
        }
        holder.itemView.setOnClickListener {
            onItemClick(categories)
        }
    }
}