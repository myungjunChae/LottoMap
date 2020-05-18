package com.ono.lotto_map.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ono.lotto_map.data.local.StoreInfoEntity

@Dao
interface StoreInfoDao{
    @Insert
    fun insert(store: StoreInfoEntity)

    @Update
    fun update(night: StoreInfoEntity)

    @Query("SELECT * FROM store_info_table WHERE store_id = :key")
    fun get(key: Long): StoreInfoEntity?

    @Query("DELETE from store_info_table")
    fun clear()

    @Query("SELECT * from store_info_table ORDER BY nightId DESC LIMIT 1")
    fun getTonight() : StoreInfoEntity?

    @Query("SELECT * from store_info_table ORDER BY nightId DESC")
    fun getAllNight() : List<StoreInfoEntity>
}