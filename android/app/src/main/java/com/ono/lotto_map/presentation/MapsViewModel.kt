package com.ono.lotto_map.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.ono.lotto_map.domain.MapsUsecase
import io.reactivex.schedulers.Schedulers

class MapsViewModel(private val mapsUsecase: MapsUsecase) : ViewModel() {
    var currentLatLng =  MutableLiveData<LatLng>().apply {
        value = LatLng(37.498186, 127.027481)
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
