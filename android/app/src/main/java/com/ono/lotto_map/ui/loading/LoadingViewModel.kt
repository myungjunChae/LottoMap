package com.ono.lotto_map.ui.loading

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ono.lotto_map.data.local.StoreInfoEntity
import com.ono.lotto_map.usecase.ConfigUsecase
import com.ono.lotto_map.usecase.StoreInfoUsecase
import io.reactivex.schedulers.Schedulers

class LoadingViewModel(
    private val configUsecase: ConfigUsecase,
    private val storeInfoUsecase: StoreInfoUsecase
) : ViewModel() {
    private val _isFirst: MutableLiveData<Boolean>
        get() = MutableLiveData(getIsFirst())
    val isFirst: MutableLiveData<Boolean>
        get() = _isFirst

    private fun getIsFirst(): Boolean {
        return configUsecase.getIsFirst()
    }

    fun setIsNotFirst() {
        configUsecase.setIsNotFirst()
    }

    fun insertStore(store: StoreInfoEntity) {
        storeInfoUsecase.insertStore(store)
            .subscribeOn(Schedulers.io())
            .subscribe {
                //Notice
            }
    }

    fun updateStore(store: StoreInfoEntity) {
        storeInfoUsecase.updateStore(store)
            .subscribeOn(Schedulers.io())
            .subscribe {
                //Notice
            }
    }

    fun clear() {
        storeInfoUsecase.clear()
            .subscribeOn(Schedulers.io())
            .subscribe {
                //Notice
            }
    }

    fun getStoreByLocation(location: String) {
        storeInfoUsecase.getStoreByLocation(location)
            .subscribeOn(Schedulers.io())
            .subscribe{

            }
    }

    fun getAllStore() {
        storeInfoUsecase.getAllStore()
            .subscribeOn(Schedulers.io())
            .subscribe(
                {},
                {}
            )
    }

    fun getStoreDataFromLocalStorage() {
        storeInfoUsecase.getStoreDataFromLocalStorage()
            .subscribeOn(Schedulers.io())
            .subscribe(
                {},
                {}
            )
    }

    fun downloadStoreDataFromS3() {
        storeInfoUsecase.downloadStoreDataFromS3()
            .subscribeOn(Schedulers.io())
            .subscribe {
                //Notice
            }
    }
}