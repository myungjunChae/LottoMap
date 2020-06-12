package com.ono.lotto_map.datasource.local

import android.content.Context
import com.google.gson.Gson
import com.ono.lotto_map.application.StoreInfoDatabase
import com.ono.lotto_map.data.local.StoreInfoEntity
import com.ono.lotto_map.data.model.toEntity
import com.ono.lotto_map.data.remote.StoreInfoResponse
import com.ono.lotto_map.data.remote.toModel
import com.ono.lotto_map.readJsonFromAsset
import com.ono.lotto_map.readJsonFromStorage
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.Single.create

class StoreLocalDataSource(private val context: Context) {
    private val storeDao = StoreInfoDatabase.getInstance(context).storeInfoDao
    private val dataFileName = "data.json"
    private val localStorageFilePath by lazy { "${context.filesDir.absolutePath}/$dataFileName" }

    fun insertStore(store: StoreInfoEntity): Completable {
        return storeDao.insert(store)
    }

    fun updateStore(store: StoreInfoEntity): Completable {
        return storeDao.update(store)
    }

    fun clear(): Completable {
        return storeDao.clear()
    }

    fun getStoreByLocation(location: String): Maybe<StoreInfoEntity> {
        return storeDao.get(location)
    }

    fun getAllStore(): Single<List<StoreInfoEntity>> {
        return storeDao.getAllStore()
    }

    fun getStoreDataFromLocalStorage() : Single<List<StoreInfoEntity>> {
        return create{
            val storeInfoEntity : List<StoreInfoEntity>? = Gson().fromJson(context.readJsonFromAsset(dataFileName), Array<StoreInfoResponse>::class.java)
                .toList().toModel().toEntity()
                .sortedByDescending { it.score }

            if(storeInfoEntity != null){
                it.run { onSuccess(storeInfoEntity) }
            }else{
                it.onError(Throwable("Cannot get store data file"))
            }
        }
    }

    fun getStoreDataFromRoom(): Single<List<StoreInfoEntity>>{
        return getAllStore()
    }
}