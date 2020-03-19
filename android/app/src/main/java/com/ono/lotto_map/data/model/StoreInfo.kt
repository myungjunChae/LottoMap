package com.ono.lotto_map.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StoreInfo(
    val first_winning: Int,
    val second_winning: Int,
    val lat: Double,
    val lng: Double,
    val location: String,
    val phone: String,
    val shop: String,
    val score: Int
)