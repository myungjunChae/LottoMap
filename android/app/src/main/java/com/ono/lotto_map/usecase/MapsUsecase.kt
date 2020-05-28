package com.ono.lotto_map.usecase

import com.ono.lotto_map.data.model.Geometry
import com.ono.lotto_map.repository.MapsRepository
import io.reactivex.Single

class MapsUsecase(private val mapsRepository: MapsRepository) {
    fun searchLocation(address: String): Single<Geometry> {
        return mapsRepository.searchAddress(address)
    }
}