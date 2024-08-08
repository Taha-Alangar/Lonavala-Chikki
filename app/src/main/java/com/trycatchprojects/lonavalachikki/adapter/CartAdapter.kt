package com.trycatchprojects.lonavalachikki.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.trycatchprojects.lonavalachikki.R
import com.trycatchprojects.lonavalachikki.databinding.ItemViewCartItemsDesignBinding
import com.trycatchprojects.lonavalachikki.room.ProductEntity

class CartAdapter(
    var productList: MutableList<ProductEntity>,
    val onItemClick: (ProductEntity) -> Unit,
    val onIncrementClick: (ProductEntity) -> Unit,
    val onDecrementClick: (ProductEntity) -> Unit,
    val onDeleteClick: (ProductEntity) -> Unit,
) : RecyclerView.Adapter<CartAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ItemViewCartItemsDesignBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ItemViewCartItemsDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val products = productList[position]
        holder.binding.apply {
            cartTitle.text = products.title

            Picasso.get()
                .load(products.imageUrl)
                .placeholder(R.drawable.floating_cart_bg) // Placeholder image while loading
                .error(R.drawable.splash_logo) // Error image if loading fails
                .into(cartImg)
            cartPrice.text = "₹ ${products.price}"
            cartItemCount.text = products.cartCount.toString()
            // Handle increment click
            tvCartIncCount.setOnClickListener {
                onIncrementClick(products)
            }

            // Handle decrement click
            tvCartDecCount.setOnClickListener {
                onDecrementClick(products)
            }

            // Handle delete click
            cartDeleteIcon.setOnClickListener {
                onDeleteClick(products)
            }
        }
        holder.itemView.setOnClickListener {
            onItemClick(products)
        }
    }

    fun updateProduct(product: ProductEntity) {
        val position = productList.indexOfFirst { it.productId == product.productId }
        if (position != -1) {
            productList[position] = product
            notifyItemChanged(position)
        }
    }

    fun updateProductList(newProductList: List<ProductEntity>) {
        val diffCallback = ProductDiffCallback(productList, newProductList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        productList.clear()
        productList.addAll(newProductList)
        diffResult.dispatchUpdatesTo(this)
    }

    class ProductDiffCallback(
        private val oldList: List<ProductEntity>,
        private val newList: List<ProductEntity>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].productId == newList[newItemPosition].productId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
