package com.ono.lotto_map.ui.loading

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ono.lotto_map.usecase.ConfigUsecase
import com.ono.lotto_map.usecase.StoreInfoUsecase
import com.orhanobut.logger.Logger
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LoadingViewModel(
    private val configUsecase: ConfigUsecase,
    private val storeInfoUsecase: StoreInfoUsecase
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    val isFirst: LiveData<Boolean>
        get() = object : LiveData<Boolean>(configUsecase.getIsFirst()) {}

    fun isFirstComplete() {
        configUsecase.isFirstComplete()
    }

    /* Store */
    fun getStoreDataFromLocalStorage() {
        compositeDisposable.add(
            storeInfoUsecase.getStoreDataFromLocal(false)
                .observeOn(Schedulers.io())
                .flattenAsObservable { it }
                .flatMapCompletable { storeInfoUsecase.insertStore(it) }
                .subscribe(
                    { Logger.d("Insert StoreInfo") },
                    { Logger.e("Fail to insert StoreInfo") }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}