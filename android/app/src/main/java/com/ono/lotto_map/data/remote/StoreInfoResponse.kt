package com.ono.lotto_map.data.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ono.lotto_map.data.model.StoreInfo

data class StoreInfoResponse(
    @SerializedName("1st") @Expose val first_winning: Int,
    @SerializedName("2nd") @Expose val second_winning: Int,
    @SerializedName("lat") @Expose val lat: Double,
    @SerializedName("lng") @Expose val lng: Double,
    @SerializedName("location") @Expose val location: String,
    @SerializedName("phone") @Expose val phone: String,
    @SerializedName("shop") @Expose val shop: String
)

fun StoreInfoResponse.toModel(): StoreInfo {
    val score = first_winning * 8 + second_winning
    return StoreInfo(first_winning, second_winning, lat, lng, location, phone, shop, score)
}

fun List<StoreInfoResponse>.toModel(): List<StoreInfo> = map {
    it.toModel()
}