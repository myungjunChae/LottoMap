package com.ono.lotto_map.repository

import com.ono.lotto_map.data.local.StoreInfoEntity
import com.ono.lotto_map.datasource.local.StoreLocalDataSource
import com.ono.lotto_map.datasource.remote.StoreRemoteDataSource
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

class StoreInfoRepository(
    private val storeInfoLocalDataSource: StoreLocalDataSource,
    private val storeInfoRemoteDataSource: StoreRemoteDataSource
) {
    fun insertStore(store: StoreInfoEntity): Completable {
        return storeInfoLocalDataSource.insertStore(store)
    }

    fun updateStore(store: StoreInfoEntity): Completable {
        return storeInfoLocalDataSource.updateStore(store)
    }

    fun clear(): Completable {
        return storeInfoLocalDataSource.clear()
    }

    fun getStoreByLocation(location: String): Maybe<StoreInfoEntity> {
        return storeInfoLocalDataSource.getStoreByLocation(location)
    }

    fun getAllStore(): Single<List<StoreInfoEntity>> {
        return storeInfoLocalDataSource.getAllStore()
    }

    fun getStoreDataFromLocalStorage(): Single<List<StoreInfoEntity>> {
        return storeInfoLocalDataSource.getStoreDataFromLocalStorage()
    }

    fun downloadStoreDataFromS3(): Completable {
        return storeInfoRemoteDataSource.downloadStoreDataFromS3()
    }
}