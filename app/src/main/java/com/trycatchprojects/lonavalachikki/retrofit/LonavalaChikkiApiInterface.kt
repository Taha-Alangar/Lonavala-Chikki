package com.trycatchprojects.lonavalachikki.retrofit

import com.trycatchprojects.lonavalachikki.models.AboutPojoItem
import com.trycatchprojects.lonavalachikki.models.CategoryPojoItem
import com.trycatchprojects.lonavalachikki.models.HomeBannerImagePojoItem
import com.trycatchprojects.lonavalachikki.models.HomeImagePojoItem
import com.trycatchprojects.lonavalachikki.models.ProductPojoItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LonavalaChikkiApiInterface {
    @GET("v3/maganlalchikki/category_list")
    fun getCategories(): Call<List<CategoryPojoItem>>

    @GET("v3/maganlalchikki/product_list")
    fun getProducts(
        @Query("category_id") categoryId: String
    ): Call<List<ProductPojoItem>>

    @GET("v3/maganlalchikki/about")
    fun getAbout(): Call<List<AboutPojoItem>>

    @GET("v3/maganlalchikki/home_image_gallery")
    fun getHomeImages(): Call<List<HomeImagePojoItem>>

    @GET("v3/maganlalchikki/banner_image")
    fun getBannerImages(): Call<List<HomeBannerImagePojoItem>>
}