package com.trycatchprojects.lonavalachikki.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.trycatchprojects.lonavalachikki.R
import com.trycatchprojects.lonavalachikki.adapter.CartAdapter
import com.trycatchprojects.lonavalachikki.databinding.FragmentCartBinding
import com.trycatchprojects.lonavalachikki.models.ProductPojoItem
import com.trycatchprojects.lonavalachikki.room.AppDatabase
import com.trycatchprojects.lonavalachikki.room.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var db: AppDatabase
    private lateinit var cartAdapter: CartAdapter
    private var  cartSubTotal = 0.0
    private var  cartTotal = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCartBinding.inflate(layoutInflater)
        db = AppDatabase.getDatabase(requireContext())
        cartAdapter = CartAdapter(
            mutableListOf(),
            onItemClick = { product -> navigateToDetail(product) },
            onIncrementClick = { product -> incrementCartCount(product) },
            onDecrementClick = { product -> decrementCartCount(product) },
            onDeleteClick = { product -> deleteCartItem(product) }
        )
        binding.cartRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
        binding.textView13.text="Standard Shipping ₹ 49"


        fetchCartItems()

        return binding.root
    }
    private fun navigateToDetail(productEntity: ProductEntity) {
        val productPojoItem = ProductPojoItem(
            full_description = productEntity.description,
            id = productEntity.productId,
            images = listOf(productEntity.imageUrl),
            price = productEntity.price.toString(),
            small_description = "", // Add appropriate data if available
            title = productEntity.title
        )
        val bundle = Bundle().apply {
            putSerializable("product_id", productPojoItem)
        }
        findNavController().navigate(R.id.action_cartFragment_to_productDetailFragment, bundle)
    }

    private fun incrementCartCount(product: ProductEntity) {
        product.cartCount++
        updateProduct(product)
    }

    private fun decrementCartCount(product: ProductEntity) {
        if (product.cartCount > 1) {
            product.cartCount--
            updateProduct(product)
        }
    }

    private fun deleteCartItem(product: ProductEntity) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.productDao().deleteProduct(product)
            }
            fetchCartItems() // Refresh list after deletion
        }
    }

    private fun fetchCartItems() {
        lifecycleScope.launch {
            val cartItems = withContext(Dispatchers.IO) {
                db.productDao().getAllProducts()
            }
            cartSubTotal = cartItems.sumOf { it.price * it.cartCount }
            cartTotal = cartSubTotal + 49

            binding.cartSubTotal.text = "Subtotal: ₹ $cartSubTotal"
            binding.cartTotal.text = "Total: ₹ $cartTotal"

            cartAdapter.updateProductList(cartItems)
        }
    }

    private fun updateProduct(product: ProductEntity) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.productDao().updateProduct(product)
            }
            fetchCartItems() // Refresh list after update
            cartAdapter.updateProduct(product)
        }
    }
}