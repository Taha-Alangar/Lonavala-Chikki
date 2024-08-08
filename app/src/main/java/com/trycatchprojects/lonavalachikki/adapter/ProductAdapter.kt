package com.trycatchprojects.lonavalachikki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.trycatchprojects.lonavalachikki.databinding.ItemViewProductsBinding
import com.trycatchprojects.lonavalachikki.models.ProductPojoItem

class ProductAdapter(private val productList: List<ProductPojoItem>,
                     val onItemClick:(ProductPojoItem)->Unit): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    class ProductViewHolder(val binding: ItemViewProductsBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ItemViewProductsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val products=productList[position]
        holder.binding.apply {
            productTitle.text=products.title
            Picasso.get().load(products.images[0]).into(productImage)
            productPrice.text="₹ ${products.price}"
        }
        holder.itemView.setOnClickListener {
            onItemClick(products)
        }
    }

}