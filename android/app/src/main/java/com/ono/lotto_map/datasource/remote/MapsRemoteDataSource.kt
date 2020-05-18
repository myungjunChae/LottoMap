package com.ono.lotto_map.datasource.remote

import android.content.res.Resources
import com.ono.lotto_map.R
import com.ono.lotto_map.data.response.Geometry
import io.reactivex.Single

class MapsRemoteDataSource(private val mapsApi: MapsApi) {
    fun searchAddress(key : String, address: String): Single<Geometry> {
        return mapsApi.searchApi(address, key)
            .map { it.results[0].geometry }
    }
}