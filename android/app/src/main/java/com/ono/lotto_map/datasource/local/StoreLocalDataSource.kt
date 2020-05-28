package com.ono.lotto_map.datasource.local

import android.content.Context
import com.google.gson.Gson
import com.ono.lotto_map.application.StoreDatabase
import com.ono.lotto_map.data.local.StoreInfoEntity
import com.ono.lotto_map.data.model.toEntity
import com.ono.lotto_map.data.remote.StoreInfoResponse
import com.ono.lotto_map.data.remote.toModel
import com.ono.lotto_map.readJsonFromStorage
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.Single.create

class StoreLocalDataSource(private val context: Context) {
    private val storeDao = StoreDatabase.getInstance(context).storeInfoDao
    private val downloadFileName = "data.json"
    private val saveFilePath by lazy { "${context.filesDir.absolutePath}/$downloadFileName" }

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
            val storeInfoEntity : List<StoreInfoEntity>? = Gson().fromJson(context.readJsonFromStorage(saveFilePath), Array<StoreInfoResponse>::class.java)
                .toList().toModel().toEntity()
                .sortedByDescending { it.score }

            if(storeInfoEntity != null){
                it.run { onSuccess(storeInfoEntity) }
            }else{
                it.onError(Throwable("Cannot get store data file"))
            }
        }
    }
}