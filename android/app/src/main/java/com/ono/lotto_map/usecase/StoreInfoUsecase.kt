package com.ono.lotto_map.usecase

import com.ono.lotto_map.data.local.StoreInfoEntity
import com.ono.lotto_map.repository.StoreInfoRepository
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

class StoreInfoUsecase(private val storeInfoRepository: StoreInfoRepository) {
    fun insertStore(store: StoreInfoEntity): Completable {
        return storeInfoRepository.insertStore(store)
    }

    fun updateStore(store: StoreInfoEntity): Completable {
        return storeInfoRepository.updateStore(store)
    }

    fun clear(): Completable {
        return storeInfoRepository.clear()
    }

    fun getStoreByLocation(location: String): Maybe<StoreInfoEntity> {
        return storeInfoRepository.getStoreByLocation(location)
    }

    fun getAllStore(): Single<List<StoreInfoEntity>> {
        return storeInfoRepository.getAllStore()
    }

    fun getStoreDataFromLocal(cached : Boolean): Single<List<StoreInfoEntity>> {
        return storeInfoRepository.getStoreDataFromLocal(cached)
    }

    fun downloadStoreDataFromS3(): Completable {
        return storeInfoRepository.downloadStoreDataFromS3()
    }
}