package com.ono.lotto_map.datasource.remote

import android.content.Context
import com.ono.lotto_map.R
import com.ono.lotto_map.data.model.Geometry
import io.reactivex.Single

class MapsRemoteDataSource(private val context: Context, private val mapsApi: MapsApi) {
    fun searchAddress(address: String): Single<Geometry> {
        val key = context.getString(R.string.geocoding_api_key)

        return mapsApi.searchApi(address, key)
            .map { it.results[0].geometry }
    }
}