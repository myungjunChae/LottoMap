package com.ono.lotto_map.presentation

import android.content.Context
import android.content.Intent
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
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.MobileAds
import com.ono.lotto_map.R
import com.ono.lotto_map.data.model.StoreInfo
import com.ono.lotto_map.databinding.ViewInfoWindowBinding
import com.ono.lotto_map.showToast
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.bottom_sheet.*


class MapsActivity : BaseActivity<ActivityMapsBinding>(), OnMapReadyCallback {
    override val resourceId: Int = R.layout.activity_maps
    private lateinit var mMap: GoogleMap
    private val vm: MapsViewModel by viewModel()

    private val goldList = mutableListOf<Marker>()
    private val silverList = mutableListOf<Marker>()
    private val bronzeList = mutableListOf<Marker>()

    private val goldIcon by lazy { loadBitmap(R.drawable.ic_gold) }
    private val silverIcon by lazy { loadBitmap(R.drawable.ic_silver) }
    private val bronzeIcon by lazy { loadBitmap(R.drawable.ic_bronze) }

    private val SCAN_RANGE = 2000

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

        binding.goldSwitch.isChecked = true
        binding.silverSwitch.isChecked = true
        binding.bronzeSwitch.isChecked = true

        binding.goldSwitch.setOnCheckedChangeListener { button, isChecked ->
            when (isChecked) {
                true -> goldList.map { it.isVisible = true }
                false -> goldList.map { it.isVisible = false }
            }
        }

        binding.silverSwitch.setOnCheckedChangeListener { button, isChecked ->
            when (isChecked) {
                true -> silverList.map { it.isVisible = true }
                false -> silverList.map { it.isVisible = false }
            }
        }

        binding.bronzeSwitch.setOnCheckedChangeListener { button, isChecked ->
            when (isChecked) {
                true -> bronzeList.map { it.isVisible = true }
                false -> bronzeList.map { it.isVisible = false }
            }
        }

        binding.btnCall.setOnClickListener {
            val filteredPhone = vm.currentStore.value!!.phone.filter { i -> i != '-' }
            val intent = Intent().apply {
                action = Intent.ACTION_DIAL
                data = Uri.parse("tel:$filteredPhone")
            }
            startActivity(intent)
        }

        binding.btnGoogleMap.setOnClickListener {
            val address = vm.currentStore.value!!.location
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse("google.navigation:q=$address")
            }
            startActivity(intent)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        vm.currentStore.observe(this, Observer {
            when (it) {
                null -> {
                    showButtonView(false)
                }
                else -> {
                    showButtonView(true)
                }
            }
        })

        //구글 애드몹
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    override fun onBackPressed() {

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
            setInfoWindowAdapter(InfoWindowAdapter(this@MapsActivity))
        }

        vm.currentLatLng.observe(this, Observer() {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 12.0f))
            updateNearStore()
        })
    }

    private fun loadBitmap(resourceId: Int): Bitmap {
        return BitmapFactory.decodeResource(resources, resourceId)
    }

    private fun resizeBitmap(resourceId: Int, width: Int, height: Int): Bitmap {
        val b = loadBitmap(resourceId)
        return Bitmap.createScaledBitmap(b, width, height, false)
    }

    private fun updateNearStore() {
        val currentLocation: LatLng = vm.currentLatLng.value ?: LatLng(37.498186, 127.027481)
        val application = applicationContext as MyApplication

        // 맵 초기화
        mMap.clear()

        //검색된 위치 주변의 판매점 검색 및 마킹
        var searchNum = 0
        for ((index, store) in application.storeInfos.withIndex()) {
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
                searchNum++
                val score = store.first_winning * 8 + store.second_winning
                val marker = mMap.addMarker(
                    MarkerOptions().apply {
                        position(LatLng(store.lat, store.lng))
                        when {
                            score >= 30 -> {
                                if (!gold_switch.isChecked)
                                    visible(false)

                                icon(BitmapDescriptorFactory.fromBitmap(goldIcon)).zIndex(100.0F)
                            }
                            score in 10 until 30 -> {
                                if (!silver_switch.isChecked)
                                    visible(false)

                                icon(BitmapDescriptorFactory.fromBitmap(silverIcon)).zIndex(90.0F)
                            }
                            else -> {
                                if (!bronze_switch.isChecked)
                                    visible(false)

                                icon(BitmapDescriptorFactory.fromBitmap(bronzeIcon)).zIndex(80.0F)
                            }
                        }
                    }
                )

                when {
                    score >= 30 -> goldList.add(marker)
                    score in 10 until 30 -> silverList.add(marker)
                    else -> bronzeList.add(marker)
                }
                marker.tag = index


                mMap.setOnMarkerClickListener {
                    val index = it.tag.toString().toInt()
                    val store = application.storeInfos[index]
                    vm.currentStore.value = store
                    showButtonView(true)
                    false
                }
                mMap.setOnInfoWindowCloseListener {
                    vm.currentStore.value = null
                }
            }
        }

        showToast("${searchNum}개의 로또판매점이 검색되었습니다.")
        showProgressCircular(false)
    }

    private fun showButtonView(state: Boolean) {
        val targetList = listOf<View>(binding.btnCall, binding.btnGoogleMap)

        when (state) {
            false -> targetList.forEach { it.visibility = View.GONE }
            true -> targetList.forEach { it.visibility = View.VISIBLE }
        }
    }

    private fun showProgressCircular(state: Boolean) {
        when (state) {
            true -> binding.progressCircular.visibility = View.VISIBLE
            false -> binding.progressCircular.visibility = View.INVISIBLE
        }
    }
}

class InfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {
    private val application = context.applicationContext as MyApplication

    override fun getInfoWindow(marker: Marker?): View? {
        val inflater = LayoutInflater.from(context)
        val binding: ViewInfoWindowBinding =
            DataBindingUtil.inflate(inflater, R.layout.view_info_window, null, false)
        val index = marker?.tag.toString().toInt()
        val score = application.storeInfos[index].score

        binding.shopTitle.text = application.storeInfos[index].shop
        when {
            score >= 30 -> binding.rank.setTextColor(context.resources.getColor(R.color.colorGold))
            score in 10 until 30 -> binding.rank.setTextColor(context.resources.getColor(R.color.colorSilver))
            else -> binding.rank.setTextColor(context.resources.getColor(R.color.colorBronze))

        }
        binding.rank.text = "${(index + 1)}등"
        binding.firstWinning.text = "1등 : ${application.storeInfos[index].first_winning}회"
        binding.secondWinning.text = "2등 : ${application.storeInfos[index].second_winning}회"
        return binding.root
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }
}