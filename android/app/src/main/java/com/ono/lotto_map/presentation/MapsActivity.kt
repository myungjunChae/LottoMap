package com.ono.lotto_map.presentation

import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ono.lotto_map.databinding.ActivityMapsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.ono.lotto_map.application.MyApplication
import android.graphics.Bitmap
import com.ono.lotto_map.R


class MapsActivity : BaseActivity<ActivityMapsBinding>(), OnMapReadyCallback {
    override val resourceId: Int = R.layout.activity_maps
    private lateinit var mMap: GoogleMap
    private val vm: MapsViewModel by viewModel()

    private val bottomSheetBehavior by lazy {
        BottomSheetBehavior.from(binding.bottomSheet.root)
    }

    private val goldIcon by lazy { resizeBitmap(R.drawable.ic_gold,180,180) }
    private val silverIcon by lazy { resizeBitmap(R.drawable.ic_silver,150,150) }
    private val bronzeIcon by lazy { resizeBitmap(R.drawable.ic_bronze,140,140) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.editText.setOnEditorActionListener { view, i, keyEvent ->
            when (i) {
                EditorInfo.IME_ACTION_DONE -> {
                    vm.searchAddress(view.text.toString())
                    true
                }
                else -> {
                    false
                }
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onBackPressed() {
        when (vm.getBottomSheetState()) {
            vm.BOTTOM_SHEET_EXPANDED -> {
                vm.changeBottomSheetCollapsed()
                setBottomSheetState()
            }
            vm.BOTTOM_SHEET_COLLAPSED -> super.onBackPressed()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.apply {
            setMinZoomPreference(6.0f) // 최대한 멀리
            setMaxZoomPreference(20.0f) // 최대한 가까이
            setLatLngBoundsForCameraTarget(
                LatLngBounds(
                    LatLng(33.0, 125.0),
                    LatLng(38.0, 131.0)
                )
            ) // 맵 범위 제한
            uiSettings.isRotateGesturesEnabled = false

        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vm.currentLatLng.value, 10.0f))

        vm.currentLatLng.observe(this, Observer() {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(it))
            updateNearStore()
        })
    }

    private fun setBottomSheetState() {
        bottomSheetBehavior.state = vm.getBottomSheetState()
    }

    private fun resizeBitmap(resourceId: Int, width: Int, height: Int): Bitmap {
        val b = BitmapFactory.decodeResource(resources, resourceId)
        return Bitmap.createScaledBitmap(b, width, height, false)
    }

    private fun updateNearStore() {
        val currentLocation: LatLng = vm.currentLatLng.value ?: LatLng(37.498186, 127.027481)
        val application = applicationContext as MyApplication

        val nearStoreList = application.storeInfos.filter {
            var results = FloatArray(1)
            Location.distanceBetween(
                currentLocation.latitude,
                currentLocation.longitude,
                it.lat,
                it.lng,
                results
            )

            var distanceInMeters = results[0]
            println("${distanceInMeters}미터, ${distanceInMeters < 1000}")
            distanceInMeters < 5000
        }

        for (store in nearStoreList) {
            mMap.addMarker(
                MarkerOptions().apply {
                    position(LatLng(store.lat, store.lng))
                    val score = store.first_winning * 8 + store.second_winning
                    when {
                        score >= 30 -> icon(BitmapDescriptorFactory.fromBitmap(goldIcon)).zIndex(100.0F)
                        score in 10 until 30 -> icon(BitmapDescriptorFactory.fromBitmap(silverIcon)).zIndex(90.0F)
                        else -> icon(BitmapDescriptorFactory.fromBitmap(bronzeIcon)).zIndex(80.0F)
                    }
                }
            )
        }
    }


}