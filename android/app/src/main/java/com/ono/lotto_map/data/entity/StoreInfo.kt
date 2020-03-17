package com.ono.lotto_map.data.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StoreInfo(
    @SerializedName("1st") @Expose val first_winning: Int,
    @SerializedName("2nd") @Expose val second_winning: Int,
    @SerializedName("lat") @Expose val lat: Double,
    @SerializedName("lng") @Expose val lng: Double,
    @SerializedName("location") @Expose val location: String,
    @SerializedName("phone") @Expose val phone: String,
    @SerializedName("shop") @Expose val shop: String
)