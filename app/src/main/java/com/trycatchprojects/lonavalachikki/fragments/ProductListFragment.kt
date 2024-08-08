package com.trycatchprojects.lonavalachikki.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.trycatchprojects.lonavalachikki.R
import com.trycatchprojects.lonavalachikki.adapter.ProductListAdapter
import com.trycatchprojects.lonavalachikki.databinding.FragmentProductListBinding
import com.trycatchprojects.lonavalachikki.models.ProductPojoItem
import com.trycatchprojects.lonavalachikki.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductListFragment : Fragment() {
    private lateinit var binding: FragmentProductListBinding
    private var categoryId:String?=null
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ):View {
        binding=FragmentProductListBinding.inflate(layoutInflater)

        shimmerFrameLayout = binding.shimmerViewContainer
        startShimmerEffect()

        setUpCategoryListRV()
        categoryId=arguments?.getString("category_id")

        binding.tvPL.text=if (categoryId=="1") "Chikkis" else if (categoryId=="2") "Fudge" else if (categoryId=="4") "Dry Fruits" else if (categoryId=="5") "Special Namkeen" else ""

        fetchProducts(categoryId)

        return binding.root
    }
    private fun startShimmerEffect() {
        shimmerFrameLayout.visibility = View.VISIBLE
        shimmerFrameLayout.startShimmer()
        binding.productListRV.visibility = View.GONE
        binding.tvPL.visibility = View.GONE
    }
    private fun stopShimmerEffect() {
        shimmerFrameLayout.stopShimmer()
        shimmerFrameLayout.visibility = View.GONE
        binding.productListRV.visibility = View.VISIBLE
        binding.tvPL.visibility = View.VISIBLE
    }

    private fun fetchProducts(categoryId: String?) {
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
                            val productListAdapter = ProductListAdapter(products){
                                navigateToProductDetail(it)
                            }
                            binding.productListRV.adapter = productListAdapter
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

    private fun navigateToProductDetail(id: ProductPojoItem) {
        val bundle = Bundle().apply {
            putSerializable("product_id", id)
            putString("category_id", categoryId)
        }
        findNavController().navigate(R.id.action_productListFragment_to_productDetailFragment, bundle)
    }

    private fun setUpCategoryListRV() {
        binding.productListRV.layoutManager= GridLayoutManager(requireContext(),2)
    }

}