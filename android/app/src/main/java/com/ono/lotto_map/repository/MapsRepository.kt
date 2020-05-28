package com.ono.lotto_map.repository

import com.ono.lotto_map.data.model.Geometry
import com.ono.lotto_map.datasource.remote.MapsRemoteDataSource
import io.reactivex.Single

class MapsRepository(
    private val mapsRemoteDataSource: MapsRemoteDataSource
) {
    fun searchAddress(address: String): Single<Geometry> {
        return mapsRemoteDataSource.searchAddress(address)
    }
}