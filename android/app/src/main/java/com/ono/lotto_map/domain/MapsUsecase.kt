package com.ono.lotto_map.domain

import com.ono.lotto_map.data.MapsRepository
import com.ono.lotto_map.data.response.Geometry
import io.reactivex.Single

class MapsUsecase(private val mapsRepository: MapsRepository) {
    fun searchLocation(key: String, address: String): Single<Geometry> {
        return mapsRepository.searchAddress(key, address)
    }
}