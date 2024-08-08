package com.trycatchprojects.lonavalachikki.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Picasso
import com.trycatchprojects.lonavalachikki.R
import com.trycatchprojects.lonavalachikki.databinding.FragmentAboutBinding
import com.trycatchprojects.lonavalachikki.models.AboutPojoItem
import com.trycatchprojects.lonavalachikki.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AboutFragment : Fragment() {
    private lateinit var binding: FragmentAboutBinding
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAboutBinding.inflate(layoutInflater)

        fetchAboutData()
        shimmerFrameLayout = binding.shimmerViewContainer
        startShimmerEffect()
        return binding.root
    }
    private fun startShimmerEffect() {
        shimmerFrameLayout.visibility = View.VISIBLE
        shimmerFrameLayout.startShimmer()
        binding.scrollView.visibility = View.GONE
    }

    private fun stopShimmerEffect() {
        shimmerFrameLayout.stopShimmer()
        shimmerFrameLayout.visibility = View.GONE
        binding.scrollView.visibility = View.VISIBLE
    }

    private fun fetchAboutData() {
        val call = RetrofitInstance.api.getAbout()
        call.enqueue(object : Callback<List<AboutPojoItem>?> {
            override fun onResponse(
                call: Call<List<AboutPojoItem>?>,
                response: Response<List<AboutPojoItem>?>,
            ) {
                stopShimmerEffect()
                if (response.isSuccessful) {
                    val aboutList = response.body()
                    aboutList?.let {
                        binding.aboutImg.visibility = View.VISIBLE
                        binding.aboutTitle.visibility = View.VISIBLE
                        binding.aboutDescription.visibility = View.VISIBLE
                        binding.aboutClientSatisfaction.visibility = View.VISIBLE
                        binding.aboutOutTeam.visibility = View.VISIBLE
                        binding.aboutHistory.visibility = View.VISIBLE

                        Picasso.get().load(it[0].image).into(binding.aboutImg)
                        binding.aboutTitle.text = it[0].title
                        binding.aboutDescription.text = it[0].description
                        binding.aboutClientSatisfaction.text = it[0].client_satisfaction
                        binding.aboutOutTeam.text = it[0].our_team
                        binding.aboutHistory.text = it[0].history
                    }
                } else {
                    Toast.makeText(requireContext(), "Checking Internet Connection....!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<AboutPojoItem>?>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to fetch About Data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}