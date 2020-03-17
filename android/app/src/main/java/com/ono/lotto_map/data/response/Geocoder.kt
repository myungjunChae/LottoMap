package com.ono.lotto_map.data.response

data class Geocoder(
    val results: List<Result>,
    val status: String
)