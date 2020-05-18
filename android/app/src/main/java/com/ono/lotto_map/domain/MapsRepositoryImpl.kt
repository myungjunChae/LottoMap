package com.ono.lotto_map.domain

import com.ono.lotto_map.data.MapsRepository
import com.ono.lotto_map.data.response.Geometry
import com.ono.lotto_map.datasource.local.MapsLocalDataSource
import com.ono.lotto_map.datasource.remote.MapsRemoteDataSource
import io.reactivex.Single

class MapsRepositoryImpl(
    private val mapsLocalDataSource: MapsLocalDataSource,
    private val mapsRemoteDataSource: MapsRemoteDataSource
) : MapsRepository {
    override fun searchAddress(key: String, address: String): Single<Geometry> {
        return mapsRemoteDataSource.searchAddress(key, address)
    }
}