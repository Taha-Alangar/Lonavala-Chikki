package com.trycatchprojects.lonavalachikki.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.trycatchprojects.lonavalachikki.R
import com.trycatchprojects.lonavalachikki.adapter.CategoryAdapter
import com.trycatchprojects.lonavalachikki.adapter.ProductAdapter
import com.trycatchprojects.lonavalachikki.databinding.FragmentHomeBinding
import com.trycatchprojects.lonavalachikki.models.CategoryPojoItem
import com.trycatchprojects.lonavalachikki.models.HomeImagePojoItem
import com.trycatchprojects.lonavalachikki.models.ProductPojoItem
import com.trycatchprojects.lonavalachikki.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var rightToLeft: Animation
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding=FragmentHomeBinding.inflate(layoutInflater)

        shimmerFrameLayout = binding.shimmerViewContainer
        startShimmerEffect()

        rightToLeft = android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.right_to_left)
        binding.llChikki.startAnimation(rightToLeft)
        binding.categoryRV.startAnimation(rightToLeft)

        binding.sADryFurits.setOnClickListener {
            navigateToProductList("4",)
        }
        binding.sANamkeens.setOnClickListener {
            navigateToProductList("5")
        }
        binding.sAChikki.setOnClickListener {
            navigateToProductList("1")
        }
        binding.sAFudge.setOnClickListener {
            navigateToProductList("2")
        }

        //imageSlider
        setUpImageSlider()
        //categories
        setUpCategoryRV()
        fetchCategories()

        //Dry Fruits
        setUpDryFruitsRecyclerView()
        fetchDryFruits()

//        //specialNamkeens
        setUpSpecialNamkeenRecyclerView()
        fetchSpecialNamkeen()
//
//        //FUDGE
        setUpFudgeRecyclerView()
        fetchFudge()

//        //chikkis
        setUpChikkiRecyclerView()
        fetchChikkis()


        return binding.root
    }


    private fun startShimmerEffect() {
        shimmerFrameLayout.visibility = View.VISIBLE
        shimmerFrameLayout.startShimmer()
        binding.homeScrollView.visibility = View.GONE
    }
    private fun stopShimmerEffect() {
        shimmerFrameLayout.stopShimmer()
        shimmerFrameLayout.visibility = View.GONE
        binding.homeScrollView.visibility = View.VISIBLE
    }


    private fun setUpCategoryRV() {
        binding.categoryRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
    }
    private fun fetchCategories() {
        val call = RetrofitInstance.api.getCategories()
        call.enqueue(object : Callback<List<CategoryPojoItem>?> {
            override fun onResponse(
                call: Call<List<CategoryPojoItem>?>,
                response: Response<List<CategoryPojoItem>?>,
            ) {
                if (isAdded) {
                    if (response.isSuccessful) {
                        val categories = response.body()
                        if (categories != null) {
                            val categoryAdapter = CategoryAdapter(categories) { category ->
                                navigateToProductList(category.id)
                            }
                            binding.categoryRV.adapter = categoryAdapter
                            stopShimmerEffect()

                        } else {
                            Toast.makeText(requireContext(), "No categories Found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<CategoryPojoItem>?>, t: Throwable) {

                Toast.makeText(requireContext(), "Checking Internet Connection....!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchDryFruits() {
        val categoryId="4"
        val call = RetrofitInstance.api.getProducts(categoryId)
        call.enqueue(object : Callback<List<ProductPojoItem>?> {
            override fun onResponse(
                call: Call<List<ProductPojoItem>?>,
                response: Response<List<ProductPojoItem>?>,
            ) {
                if (response.isSuccessful) {
                    val products = response.body()
                    if (products != null) {
                        val limitedProducts = if (products.size > 6) products.take(6) else products
                        val productAdapter = ProductAdapter(limitedProducts) {
                            navigateToProductDetail(it, categoryId)
                        }

                        binding.dryFruitsRollsRV.adapter = productAdapter
                        stopShimmerEffect()
                    } else {
                        Toast.makeText(requireContext(), "No Dry Fruits Found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<ProductPojoItem>?>, t: Throwable) {

//                Toast.makeText(requireContext(), "Checking Internet Connection....!", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun setUpDryFruitsRecyclerView() {
        binding.dryFruitsRollsRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun fetchSpecialNamkeen() {
        val categoryId="5"
        val call = RetrofitInstance.api.getProducts(categoryId)
        call.enqueue(object : Callback<List<ProductPojoItem>?> {
            override fun onResponse(
                call: Call<List<ProductPojoItem>?>,
                response: Response<List<ProductPojoItem>?>,
            ) {
                if (response.isSuccessful) {
                    val products = response.body()
                    if (products != null) {
                        val limitedProducts = if (products.size > 6) products.take(6) else products
                        val productAdapter = ProductAdapter(limitedProducts) {
                            navigateToProductDetail(it, categoryId)
                        }
                        binding.specialNamkeensRV.adapter = productAdapter
                        stopShimmerEffect()

                    } else {
                        Toast.makeText(requireContext(), "No Special Namkeen Found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<ProductPojoItem>?>, t: Throwable) {

//                Toast.makeText(requireContext(), "Checking Internet Connection....!", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun setUpSpecialNamkeenRecyclerView() {
        binding.specialNamkeensRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun fetchFudge() {
        val categoryId="2"
        val call = RetrofitInstance.api.getProducts(categoryId)
        call.enqueue(object : Callback<List<ProductPojoItem>?> {
            override fun onResponse(
                call: Call<List<ProductPojoItem>?>,
                response: Response<List<ProductPojoItem>?>,
            ) {
                if (response.isSuccessful) {
                    val products = response.body()
                    if (products != null) {
                        val limitedProducts = if (products.size > 6) products.take(6) else products
                        val productAdapter = ProductAdapter(limitedProducts) {
                            navigateToProductDetail(it, categoryId)
                        }
                        binding.fudgeRV.adapter = productAdapter
                        stopShimmerEffect()

                    } else {
                        Toast.makeText(requireContext(), "No Fudge Found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<ProductPojoItem>?>, t: Throwable) {

//                Toast.makeText(requireContext(), "Checking Internet Connection....!", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun setUpFudgeRecyclerView() {
        binding.fudgeRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun fetchChikkis() {
        val categoryId="1"
        val call = RetrofitInstance.api.getProducts(categoryId)
        call.enqueue(object : Callback<List<ProductPojoItem>?> {
            override fun onResponse(
                call: Call<List<ProductPojoItem>?>,
                response: Response<List<ProductPojoItem>?>,
            ) {
                if (response.isSuccessful) {
                    val products = response.body()
                    if (products != null) {
                        val limitedProducts = if (products.size > 6) products.take(6) else products
                        val productAdapter = ProductAdapter(limitedProducts) {
                            navigateToProductDetail(it, categoryId)
                        }

                        binding.chkkiRV.adapter = productAdapter
                        stopShimmerEffect()

                    } else {
                        Toast.makeText(requireContext(), "No Chikkis Found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<ProductPojoItem>?>, t: Throwable) {

//                Toast.makeText(requireContext(), "Checking Internet Connection....!", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun setUpChikkiRecyclerView() {
        binding.chkkiRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setUpImageSlider() {
        val call= RetrofitInstance.api.getHomeImages()
        call.enqueue(object : Callback<List<HomeImagePojoItem>?> {
            override fun onResponse(
                call: Call<List<HomeImagePojoItem>?>,
                response: Response<List<HomeImagePojoItem>?>,
            ) {
                if (response.isSuccessful) {
                    val images = response.body()
                    if (images != null) {
                        val slideModels = images.map { SlideModel(it.image, ScaleTypes.FIT) }
                        binding.imageSlider.setImageList(slideModels)
                        stopShimmerEffect()

                    } else {
                        Toast.makeText(requireContext(), "No Images found", Toast.LENGTH_SHORT).show()
                    }
                }

            }

            override fun onFailure(call: Call<List<HomeImagePojoItem>?>, t: Throwable) {

//                Toast.makeText(requireContext(), "Checking Internet Connection....!", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun navigateToProductList(categoryId: String) {
        val bundle = Bundle().apply {
            putString("category_id", categoryId)
        }
        findNavController().navigate(R.id.action_homeFragment_to_productListFragment, bundle)
    }

    private fun navigateToProductDetail(it: ProductPojoItem, categoryId: String) {
        val bundle = Bundle().apply {
            putSerializable("product_id", it)
            putString("category_id", categoryId)

        }
        findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, bundle)
    }
}