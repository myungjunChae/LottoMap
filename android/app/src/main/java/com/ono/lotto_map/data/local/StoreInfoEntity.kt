package com.ono.lotto_map.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "store_info_table")
data class StoreInfoEntity(
    @PrimaryKey var store_id: Int,
    @ColumnInfo(name = "1st") val first_winning: Int,
    @ColumnInfo(name = "2nd") val second_winning: Int,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lng") val lng: Double,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "shop") val shop: String,
    @ColumnInfo(name = "score") var score: Int
)