package com.ono.lotto_map.data.model

data class Geocoder(
    val results: List<Result>,
    val status: String
)