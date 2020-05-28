package com.ono.lotto_map.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ono.lotto_map.data.local.StoreInfoEntity

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

fun StoreInfo.toEntity(): StoreInfoEntity {
    return StoreInfoEntity(1, first_winning, second_winning, lat, lng, location, phone, shop, score)
}

fun List<StoreInfo>.toEntity(): List<StoreInfoEntity> =
    map { it.toEntity() }
