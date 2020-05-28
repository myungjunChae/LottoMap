package com.ono.lotto_map.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ono.lotto_map.data.local.StoreInfoEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface StoreInfoDao {
    @Insert
    fun insert(store: StoreInfoEntity): Completable

    @Update
    fun update(store: StoreInfoEntity): Completable

    @Query("DELETE from store_info_table")
    fun clear(): Completable

    @Query("SELECT * FROM store_info_table WHERE location = :location")
    fun get(location: String): Maybe<StoreInfoEntity>

    @Query("SELECT * from store_info_table ORDER BY store_id DESC")
    fun getAllStore(): Single<List<StoreInfoEntity>>
}