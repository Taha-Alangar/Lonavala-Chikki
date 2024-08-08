package com.trycatchprojects.lonavalachikki.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.trycatchprojects.lonavalachikki.R
import com.trycatchprojects.lonavalachikki.adapter.ImagePagerAdapter
import com.trycatchprojects.lonavalachikki.adapter.ProductAdapter
import com.trycatchprojects.lonavalachikki.databinding.FragmentProductDetailBinding
import com.trycatchprojects.lonavalachikki.models.ProductPojoItem
import com.trycatchprojects.lonavalachikki.retrofit.RetrofitInstance
import com.trycatchprojects.lonavalachikki.room.AppDatabase
import com.trycatchprojects.lonavalachikki.room.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductDetailFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailBinding
    private var productId: ProductPojoItem? = null
    private var categoryId: String? = null
    private var count = 0
    private var total = 0.0
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    private lateinit var db: AppDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ):  View {
        binding = FragmentProductDetailBinding.inflate(layoutInflater, container, false)


        db = AppDatabase.getDatabase(requireContext())
        shimmerFrameLayout = binding.PDShimmer
        startShimmerEffect()

        productId = arguments?.getSerializable("product_id") as ProductPojoItem?
        categoryId = arguments?.getString("category_id")


        fetchProductDetails(productId)

        fetchProductFromCart()

        setUpRelatedRecyclerView()
        categoryId?.let {
            fetchRelatedProducts(it)
        } ?: run {
            stopShimmerEffect()
            if (categoryId==null){

                binding.tvRelated.visibility = View.GONE
                binding.viewAllRelatedRV.visibility = View.GONE
                binding.tvViewAllRelated.visibility = View.GONE
            }

        }

        binding.llCartTotal.visibility = View.GONE

        binding.addToCartBtn.setOnClickListener {
            binding.addToCartBtn.visibility = View.GONE
            binding.llAddToCart.visibility = View.VISIBLE
            binding.llCartTotal.visibility = View.VISIBLE

            count = 1
            updateCart()
        }

        binding.tvIncCount.setOnClickListener {
            count++
            updateCart()
        }

        binding.tvDecCount.setOnClickListener {
            if (count > 1) {
                count--
                updateCart()
            } else {
                binding.addToCartBtn.visibility = View.VISIBLE
                binding.llAddToCart.visibility = View.GONE
                binding.llCartTotal.visibility = View.GONE
                removeProductFromCart()
            }
        }
        binding.tvViewAllRelated.setOnClickListener {
            navigateToProductList()
        }
        binding.llCartTotal.setOnClickListener {

            findNavController().navigate(R.id.action_productDetailFragment_to_cartFragment)

        }
        return binding.root
    }

    private fun startShimmerEffect() {
        shimmerFrameLayout.visibility = View.VISIBLE
        shimmerFrameLayout.startShimmer()
        binding.detailScrollView.visibility = View.GONE
    }
    private fun stopShimmerEffect() {
        shimmerFrameLayout.stopShimmer()
        shimmerFrameLayout.visibility = View.GONE
        binding.detailScrollView.visibility = View.VISIBLE
    }

    private fun navigateToProductList() {
        val bundle = Bundle().apply {
            putString("category_id", categoryId)
        }
        findNavController().navigate(R.id.action_productDetailFragment_to_productListFragment, bundle)


    }

    private fun setUpRelatedRecyclerView() {
        binding.viewAllRelatedRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun fetchRelatedProducts(categoryId: String?) {
        val call= RetrofitInstance.api.getProducts(categoryId!!)
        call.enqueue(object : Callback<List<ProductPojoItem>?> {
            override fun onResponse(
                call: Call<List<ProductPojoItem>?>,
                response: Response<List<ProductPojoItem>?>,
            ) {
                stopShimmerEffect()
                if (isAdded){
                    if (response.isSuccessful) {
                        val products = response.body()
                        if (products != null) {
                            val limitedProducts = if (products.size > 6) products.take(6) else products

                            val productListAdapter = ProductAdapter(limitedProducts){
                                navigateToProductDetail(it)
                            }
                            binding.viewAllRelatedRV.adapter = productListAdapter
                        }else{
                            Toast.makeText(requireContext(), "No Products Found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<ProductPojoItem>?>, t: Throwable) {
                Toast.makeText(requireContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToProductDetail(it: ProductPojoItem) {
        val bundle = Bundle().apply {
            putSerializable("product_id", it)
            putString("category_id", categoryId)
        }
        findNavController().navigate(R.id.action_productDetailFragment_self, bundle)


    }

    private fun fetchProductDetails(productId: ProductPojoItem?) {
        binding.tvTitle.text = productId?.title
        binding.tvSmallDescription.text = productId?.small_description
        binding.tvFullDescription.text = productId?.full_description
        binding.tvPrice.text = "Price: $${productId?.price}"
        binding.viewPager.adapter = ImagePagerAdapter(productId?.images ?: emptyList())
    }

    private fun fetchProductFromCart() {
        lifecycleScope.launch {
            val productEntity = withContext(Dispatchers.IO) {
                db.productDao().getProductById(productId?.id ?: "")
            }
            productEntity?.let {
                count = it.cartCount
                total = count * (productId?.price?.toDouble() ?: 0.0)
                binding.cartCount.text = count.toString()
                binding.tvCartItems.text = "$count items"
                binding.tvCartTotal.text = "Total: $${total}"

                binding.addToCartBtn.visibility = View.GONE
                binding.llAddToCart.visibility = View.VISIBLE
                binding.llCartTotal.visibility = View.VISIBLE
            }
        }
    }

    private fun updateCart() {
        binding.cartCount.text = count.toString()
        binding.tvCartItems.text = "$count items"
        total = count * (productId?.price?.toDouble() ?: 0.0)
        binding.tvCartTotal.text = "Total: $${total}"

        // Save product to Room database
        saveProductToCart()
    }

    private fun saveProductToCart() {
        productId?.let {
            val productEntity = ProductEntity(
                productId = it.id,
                title = it.title,
                price = it.price.toDouble(),
                imageUrl = it.images[0],
                description = it.full_description,
                cartCount = count
            )

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    db.productDao().insertOrUpdate(productEntity)
                }
            }
        }
    }

    private fun removeProductFromCart() {
        productId?.let {
            val productEntity = ProductEntity(
                productId = it.id,
                title = it.title,
                price = it.price.toDouble(),
                imageUrl = it.images[0],
                description = it.full_description,
                cartCount = count
            )

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    db.productDao().deleteProduct(productEntity)
                }
            }
        }
    }

}