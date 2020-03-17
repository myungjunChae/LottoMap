package com.ono.lotto_map.data

import com.ono.lotto_map.data.response.Geometry
import io.reactivex.Single

interface MapsRepository {
    fun searchAddress(address: String) : Single<Geometry>
}