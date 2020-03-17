package com.ono.lotto_map.datasource.remote

import com.ono.lotto_map.data.response.Geometry
import io.reactivex.Single

class MapsRemoteDataSource(private val mapsApi: MapsApi) {
    fun searchAddress(address: String): Single<Geometry> {
        //val key = Resources.getSystem().getString(R.string.geocoder_api_key)
        val key = "AIzaSyASFKaOV3J-9XXKgmL9LeSkffmVfpQ4pHE"
        return mapsApi.searchApi(address, key)
            .map { it.results[0].geometry }
    }
}