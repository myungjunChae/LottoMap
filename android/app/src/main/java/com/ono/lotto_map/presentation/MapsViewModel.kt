package com.ono.lotto_map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.ono.lotto_map.data.entity.StoreInfo
import com.ono.lotto_map.domain.MapsUsecase
import com.ono.lotto_map.readJsonFromAsset
import io.reactivex.schedulers.Schedulers

class MapsViewModel(private val mapsUsecase: MapsUsecase) : ViewModel() {
    val BOTTOM_SHEET_COLLAPSED = BottomSheetBehavior.STATE_COLLAPSED
    val BOTTOM_SHEET_EXPANDED = BottomSheetBehavior.STATE_EXPANDED
    private var bottomSheetState = BOTTOM_SHEET_COLLAPSED

    var currentLatLng =  MutableLiveData<LatLng>().apply {
        value = LatLng(37.498186, 127.027481)
    }

    fun changeBottomSheetCollapsed() {
        bottomSheetState = BOTTOM_SHEET_COLLAPSED
    }

    fun changeBottomSheetExpanded() {
        bottomSheetState = BOTTOM_SHEET_EXPANDED
    }

    fun getBottomSheetState(): Int {
        return bottomSheetState
    }

    fun searchAddress(address: String) {
        mapsUsecase.searchLocation(address)
            .subscribeOn(Schedulers.io())
            .subscribe({
                print(it)
                currentLatLng.postValue(LatLng(it.location.lat, it.location.lng))
            }, {
                print(it.message)
            })
    }

//    private val users: MutableLiveData<List<User>> by lazy {
//        MutableLiveData().also {
//            loadUsers()
//        }
//    }
//
//    fun getUsers(): LiveData<List<User>> {
//        return users
//    }
//
//    private fun loadUsers() {
//        // Do an asynchronous operation to fetch users.
//    }
}
