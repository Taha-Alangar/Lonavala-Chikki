package com.trycatchprojects.lonavalachikki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.trycatchprojects.lonavalachikki.databinding.ItemViewProductsListBinding
import com.trycatchprojects.lonavalachikki.models.ProductPojoItem

class ProductListAdapter(private val productList: List<ProductPojoItem>
                         , val onItemClick:(ProductPojoItem)->Unit): RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {
    class ProductViewHolder(val binding: ItemViewProductsListBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ItemViewProductsListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val products=productList[position]
        holder.binding.apply {
            productListTitle.text=products.title
            Picasso.get().load(products.images[0]).into(productListImage)
            productListPrice.text="₹ ${products.price}"
        }
        holder.itemView.setOnClickListener {
            onItemClick(products)
        }
    }
}