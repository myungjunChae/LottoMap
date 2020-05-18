package com.ono.lotto_map.data

import com.ono.lotto_map.data.model.Geometry
import io.reactivex.Single

interface MapsRepository {
    fun searchAddress(key: String, address: String): Single<Geometry>
}