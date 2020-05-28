package com.ono.lotto_map.ui.maps

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ono.lotto_map.R
import com.ono.lotto_map.data.local.StoreInfoEntity
import com.ono.lotto_map.data.model.StoreInfo
import com.ono.lotto_map.usecase.MapsUsecase
import com.ono.lotto_map.usecase.StoreInfoUsecase
import com.ono.lotto_map.util.ResourceProvider
import com.orhanobut.logger.Logger
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

private const val SCAN_RANGE = 2000

class MarkerOptionIndex(markerOptions: MarkerOptions, index: Int) {
    val mMarkerOptions = markerOptions
    val mIndex = index
}

class MapsViewModel(
    private val mapsUsecase: MapsUsecase,
    private val storeInfoUsecase: StoreInfoUsecase,
    resourceProvider: ResourceProvider
) : ViewModel() {
    private val goldIcon by lazy { resourceProvider.loadBitmap(R.drawable.ic_gold) }
    private val silverIcon by lazy { resourceProvider.loadBitmap(R.drawable.ic_silver) }
    private val bronzeIcon by lazy { resourceProvider.loadBitmap(R.drawable.ic_bronze) }

    private var _storeInfoList = mutableListOf<StoreInfoEntity>()
    val storeInfoList = MutableLiveData<List<StoreInfoEntity>>()

    private val _currentLatLng = MutableLiveData(LatLng(37.498186, 127.027481))
    val currentLatLng: LiveData<LatLng>
        get() = _currentLatLng

    private val _currentStore = MutableLiveData<StoreInfoEntity>()
    val currentStore: LiveData<StoreInfoEntity>
        get() = _currentStore

    val currentStorePhoneWithoutDash = Transformations.map(_currentStore) { store ->
        store?.phone?.filter { i -> i != '-' }
    }

    private val _isProgress = MutableLiveData<Boolean>()
    val isProgress: LiveData<Boolean>
        get() = _isProgress

    private val _isClear = MutableLiveData<Boolean>()
    val isClear: LiveData<Boolean>
        get() = _isClear

    private val _isGoldChecked = MutableLiveData<Boolean>()
    val isGoldChecked: LiveData<Boolean>
        get() = _isGoldChecked

    private val _isSilverChecked = MutableLiveData<Boolean>()
    val isSilverChecked: LiveData<Boolean>
        get() = _isSilverChecked

    private val _isBronzeChecked = MutableLiveData<Boolean>()
    val isBronzeChecked: LiveData<Boolean>
        get() = _isBronzeChecked

    private var _goldList = mutableListOf<StoreInfoEntity>()
    val goldList = MutableLiveData<List<StoreInfoEntity>>()

    val goldMarkerOptionList = Transformations.map(goldList) {
        val t = mutableListOf<MarkerOptionIndex>()
        for (store in it) {
            t.add(
                MarkerOptionIndex(
                    MarkerOptions().apply {
                        position(LatLng(store.lat, store.lng))
                        icon(BitmapDescriptorFactory.fromBitmap(goldIcon)).zIndex(100.0F)
                    }
                    , store.store_id
                )
            )
        }
        MutableLiveData<List<MarkerOptionIndex>>(t)
    }

    private var _silverList = mutableListOf<StoreInfoEntity>()
    val silverList = MutableLiveData<List<StoreInfoEntity>>()

    val silverMarkerOptionList = Transformations.map(silverList) {
        val t = mutableListOf<MarkerOptionIndex>()
        for (store in it) {
            t.add(
                MarkerOptionIndex(
                    MarkerOptions().apply {
                        position(LatLng(store.lat, store.lng))
                        icon(BitmapDescriptorFactory.fromBitmap(silverIcon)).zIndex(90.0F)
                    }
                    , store.store_id
                )
            )
        }
        MutableLiveData<List<MarkerOptionIndex>>(t)
    }

    private var _bronzeList = mutableListOf<StoreInfoEntity>()
    val bronzeList = MutableLiveData<List<StoreInfoEntity>>()

    val bronzeMarkerOptionList = Transformations.map(bronzeList) {
        val t = mutableListOf<MarkerOptionIndex>()
        for (store in it) {
            t.add(
                MarkerOptionIndex(
                    MarkerOptions().apply {
                        position(LatLng(store.lat, store.lng))
                        icon(BitmapDescriptorFactory.fromBitmap(bronzeIcon)).zIndex(80.0F)
                    }
                    , store.store_id
                )
            )
        }
        MutableLiveData<List<MarkerOptionIndex>>(t)
    }

    //Gold Store
    fun addGoldStore(store: StoreInfoEntity) {
        _goldList.add(store)
        setGoldList()
    }

    fun addGoldStore(store: List<StoreInfoEntity>) {
        _goldList = store.toMutableList()
        setGoldList()
    }

    fun removeGoldStore(store: StoreInfoEntity) {
        _goldList.remove(store)
        setGoldList()
    }

    fun setGoldList() {
        goldList.postValue(_goldList)
    }

    //Silver Store
    fun addSilverStore(store: StoreInfoEntity) {
        _silverList.add(store)
        setSilverList()
    }

    fun addSilverStore(store: List<StoreInfoEntity>) {
        _silverList = store.toMutableList()
        setSilverList()
    }

    fun removeSilverStore(store: StoreInfoEntity) {
        _silverList.remove(store)
        setSilverList()
    }

    fun setSilverList() {
        silverList.postValue(_silverList)
    }

    //Bronzen Store
    fun addBronzeStore(store: StoreInfoEntity) {
        _bronzeList.add(store)
        setBronzeList()
    }

    fun addBronzeStore(store: List<StoreInfoEntity>) {
        _bronzeList = store.toMutableList()
        setBronzeList()
    }

    fun removeBronzeStore(store: StoreInfoEntity) {
        _bronzeList.remove(store)
        setBronzeList()
    }

    fun setBronzeList() {
        bronzeList.postValue(_bronzeList)
    }

    //Store Info List
    fun addStoreInfo(store: List<StoreInfoEntity>) {
        _storeInfoList = store.toMutableList()
        setStoreInfo()
    }

    fun setStoreInfo() {
        storeInfoList.postValue(_storeInfoList)
    }

    //Progress
    fun onProgress() {
        _isProgress.postValue(true)
    }

    fun onProgressComplete() {
        _isProgress.postValue(false)
    }

    fun onClear() {
        _isClear.postValue(true)
    }

    fun onClearComplete() {
        _isClear.postValue(false)
    }

    fun setGoldState(state: Boolean) {
        _isGoldChecked.postValue(state)
    }

    fun setSilverState(state: Boolean) {
        _isSilverChecked.postValue(state)
    }

    fun setBronzeState(state: Boolean) {
        _isBronzeChecked.postValue(state)
    }

    fun setNewLocation(lat: Double, lng: Double) {
        _currentLatLng.postValue(LatLng(lat, lng))
    }

    fun setCurrentStore(store: StoreInfoEntity?) {
        _currentStore.postValue(store)
    }

    fun searchAddress(address: String) {
        mapsUsecase.searchLocation(address)
            .subscribeOn(Schedulers.io())
            .subscribe({
                setNewLocation(it.location.lat, it.location.lng)
            }, {
                Logger.d(it)
            })
    }

    @SuppressLint("CheckResult")
    fun updateNearStore() {
        storeInfoUsecase.getStoreDataFromLocalStorage()
            .subscribeOn(Schedulers.io())
            .flatMap {
                addStoreInfo(it)

                val currentLocation: LatLng = currentLatLng.value ?: LatLng(37.498186, 127.027481)
                var searchStoreList = mutableListOf<StoreInfoEntity>()

                for ((index, store) in it.withIndex()) {
                    var results = FloatArray(1)
                    Location.distanceBetween(
                        currentLocation.latitude,
                        currentLocation.longitude,
                        store.lat,
                        store.lng,
                        results
                    )
                    var distanceInMeters = results[0]

                    if (distanceInMeters < SCAN_RANGE) {
                        store.score = store.first_winning * 8 + store.second_winning
                        searchStoreList.add(store)
                    }

                    store.store_id = index
                }
                Single.just(searchStoreList)
            }
            .subscribe({
                val tempGoldList = mutableListOf<StoreInfoEntity>()
                val tempSilverList = mutableListOf<StoreInfoEntity>()
                val tempBronzeList = mutableListOf<StoreInfoEntity>()
                for (store in it) {
                    when {
                        store.score >= 30 -> {
                            tempGoldList.add(store)
                        }
                        store.score in 10 until 30 -> {
                            tempSilverList.add(store)
                        }
                        else -> {
                            tempBronzeList.add(store)
                        }
                    }
                }
                addGoldStore(tempGoldList)
                addSilverStore(tempSilverList)
                addBronzeStore(tempBronzeList)
                onProgressComplete()

            }, {
                Logger.d(it)
                onProgressComplete()
            })
    }
}
