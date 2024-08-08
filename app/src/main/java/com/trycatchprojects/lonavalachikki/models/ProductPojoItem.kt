package com.trycatchprojects.lonavalachikki.models

import java.io.Serializable

data class ProductPojoItem(
    val full_description: String,
    val id: String,
    val images: List<String>,
    val price: String,
    val small_description: String,
    val title: String
):Serializable