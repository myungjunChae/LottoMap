package com.ono.lotto_map.presentation

import android.app.Application
import android.content.Context
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
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import com.ono.lotto_map.R
import com.ono.lotto_map.databinding.ViewInfoWindowBinding
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.view_info_window.view.*


class MapsActivity : BaseActivity<ActivityMapsBinding>(), OnMapReadyCallback {
    override val resourceId: Int = R.layout.activity_maps
    private lateinit var mMap: GoogleMap
    private val vm: MapsViewModel by viewModel()
    private val SCAN_RANGE = 2000

    private val bottomSheetBehavior by lazy {
        BottomSheetBehavior.from(binding.bottomSheet.root)
    }

    private var bottomSheetState = BottomSheetBehavior.STATE_COLLAPSED

    private val goldIcon by lazy { resizeBitmap(R.drawable.ic_gold, 180, 180) }
    private val silverIcon by lazy { resizeBitmap(R.drawable.ic_silver, 150, 150) }
    private val bronzeIcon by lazy { resizeBitmap(R.drawable.ic_bronze, 140, 140) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.editText.setOnEditorActionListener { view, i, keyEvent ->
            when (i) {
                EditorInfo.IME_ACTION_DONE -> {
                    vm.searchAddress(view.text.toString())
                    showProgressCircular(true)
                    false
                }
                else -> {
                    false
                }
            }
        }

        binding.clearButton.setOnClickListener {
            binding.editText.text.clear()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onBackPressed() {
        when (bottomSheetState) {
            BottomSheetBehavior.STATE_EXPANDED -> showBottomSheet(false)
            BottomSheetBehavior.STATE_COLLAPSED -> super.onBackPressed()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.apply {
            setMinZoomPreference(6.0f) // 최대한 멀리
            setMaxZoomPreference(20.0f) // 최대한 가까이

            // 맵 범위 제한
            setLatLngBoundsForCameraTarget(
                LatLngBounds(
                    LatLng(33.0, 125.0),
                    LatLng(38.0, 131.0)
                )
            )
            uiSettings.isRotateGesturesEnabled = false // 회전 금지
            moveCamera(CameraUpdateFactory.newLatLngZoom(vm.currentLatLng.value, 10.0f)) //기본 좌표
            setInfoWindowAdapter(temp(this@MapsActivity))
        }

        vm.currentLatLng.observe(this, Observer() {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it,12.0f))
            updateNearStore()
        })
    }

    private fun showBottomSheet(state: Boolean) {
        bottomSheetState = when (state) {
            true -> BottomSheetBehavior.STATE_EXPANDED
            false -> BottomSheetBehavior.STATE_COLLAPSED
        }
        bottomSheetBehavior.state = bottomSheetState
    }

    private fun resizeBitmap(resourceId: Int, width: Int, height: Int): Bitmap {
        val b = BitmapFactory.decodeResource(resources, resourceId)
        return Bitmap.createScaledBitmap(b, width, height, false)
    }

    private fun updateNearStore() {
        val currentLocation: LatLng = vm.currentLatLng.value ?: LatLng(37.498186, 127.027481)
        val application = applicationContext as MyApplication

        // 맵 초기화
        mMap.clear()

        //검색된 위치 주변의 판매점 검색 및 마킹
        for((index,store) in application.storeInfos.withIndex()){
            var results = FloatArray(1)
            Location.distanceBetween(
                currentLocation.latitude,
                currentLocation.longitude,
                store.lat,
                store.lng,
                results
            )

            var distanceInMeters = results[0]

            if(distanceInMeters < SCAN_RANGE){
                val marker = mMap.addMarker(
                    MarkerOptions().apply {
                        position(LatLng(store.lat, store.lng))
                        val score = store.first_winning * 8 + store.second_winning
                        when {
                            score >= 30 -> icon(BitmapDescriptorFactory.fromBitmap(goldIcon)).zIndex(
                                100.0F
                            )
                            score in 10 until 30 -> icon(BitmapDescriptorFactory.fromBitmap(silverIcon)).zIndex(
                                90.0F
                            )
                            else -> icon(BitmapDescriptorFactory.fromBitmap(bronzeIcon)).zIndex(80.0F)
                        }
                   }
                )
                marker.tag = index

                mMap.setOnMarkerClickListener {
                    bottomSheetBehavior.apply {
                        val index = it.tag.toString().toInt()
                        val store = application.storeInfos[index]
                        store_title.text = store.shop
                        store_location.text = store.location
                        store_phone.text = store.phone
                    }
                    showBottomSheet(true)
                    false
                }
            }
        }

        showProgressCircular(false)
    }

    fun showProgressCircular(state : Boolean){
        when(state){
            true -> binding.progressCircular.visibility = View.VISIBLE
            false -> binding.progressCircular.visibility = View.INVISIBLE
        }
    }
}

class temp(private val context : Context) : GoogleMap.InfoWindowAdapter{
    val application = context.applicationContext as MyApplication

    override fun getInfoWindow(marker: Marker?): View? {
        val inflater = LayoutInflater.from(context)
        val binding : ViewInfoWindowBinding = DataBindingUtil.inflate(inflater,R.layout.view_info_window,null,false)
        val index = marker?.tag.toString().toInt()

        binding.shopTitle.text = application.storeInfos[index].shop
        binding.rank.text = "${(index+1)}등"
        binding.firstWinning.text = "1등 : ${application.storeInfos[index].first_winning}회"
        binding.secondWinning.text = "2등 : ${application.storeInfos[index].second_winning}회"
        return binding.root
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }
}