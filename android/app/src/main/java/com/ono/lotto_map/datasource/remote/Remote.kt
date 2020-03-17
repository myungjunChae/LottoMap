package com.ono.lotto_map.datasource.remote

import com.ono.lotto_map.data.response.Geocoder
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MapsApi {
    @GET("json")
    fun searchApi(@Query("address") address: String,  @Query("key") key: String) : Single<Geocoder>
}