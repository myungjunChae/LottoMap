package com.ono.lotto_map.presentation

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.ono.lotto_map.R
import com.ono.lotto_map.application.MyApplication
import com.ono.lotto_map.databinding.ActivityMapsBinding
import com.ono.lotto_map.databinding.ViewInfoWindowBinding
import com.ono.lotto_map.showToast
import com.ono.lotto_map.util.PermissionUtil
import kotlinx.android.synthetic.main.activity_maps.*
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val LOCATION_INTERVAL: Long = 10000
private const val LOCATION_FASTEST_INTERVAL: Long = 5000
private const val LOCATION_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY
private const val PERMISSION_CHECKSUM: Int = 2001
private const val SCAN_RANGE = 2000

class MapsActivity : BaseActivity<ActivityMapsBinding>(), OnMapReadyCallback {
    override val resourceId: Int = R.layout.activity_maps
    private val vm: MapsViewModel by viewModel()

    private lateinit var mMap: GoogleMap

    private val googleKey by lazy { getString(R.string.geocoding_api_key) }

    private val goldList = mutableListOf<Marker>()
    private val silverList = mutableListOf<Marker>()
    private val bronzeList = mutableListOf<Marker>()

    private val goldIcon by lazy { loadBitmap(R.drawable.ic_gold) }
    private val silverIcon by lazy { loadBitmap(R.drawable.ic_silver) }
    private val bronzeIcon by lazy { loadBitmap(R.drawable.ic_bronze) }

    private val locationManager by lazy { getSystemService(LOCATION_SERVICE) as LocationManager }

    private val mFusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val locationRequest =
        LocationRequest()
            .setInterval(LOCATION_INTERVAL)
            .setFastestInterval(LOCATION_FASTEST_INTERVAL)
            .setPriority(LOCATION_PRIORITY)

    private val builder = LocationSettingsRequest.Builder().apply {
        addLocationRequest(locationRequest)
    }

    private lateinit var locationCallback: LocationCallback

    private var requestingLocationUpdates = false

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        var check = true
        if (requestCode == PERMISSION_CHECKSUM) {
            if (!PermissionUtil.checkPermissions(this, ACCESS_FINE_LOCATION))
                check = false
        }

        if (check) {
            getCurrentLocation()
        } else {
            when (PermissionUtil.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                true -> {
                    Snackbar.make(
                        binding.layout,
                        "퍼미션이 거부되었습니다.",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction("확인", {}).show()
                }
                false -> {
                    Snackbar.make(
                        binding.layout,
                        "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다.",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction("확인", {}).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.editText.setOnEditorActionListener { view, i, keyEvent ->
            when (i) {
                EditorInfo.IME_ACTION_DONE -> {
                    when (view.text.toString().isBlank()) {
                        true -> {
                            showToast("검색어를 입력해주세요.")
                        }
                        false -> {
                            vm.searchAddress(googleKey, view.text.toString())
                            showProgressCircular(true)
                        }
                    }
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

        binding.btnGps.setOnClickListener {
            if (PermissionUtil.getPermission(this, ACCESS_FINE_LOCATION, PERMISSION_CHECKSUM)) {
                getCurrentLocation()
            }
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

    private fun checkGPSEnable(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


    private fun enableGPS() {
        var intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        startActivity(intent)
    }

    private fun getCurrentLocation(): Boolean {
        if (!checkGPSEnable()) {
            Snackbar.make(
                binding.layout,
                "GPS가 꺼져있습니다. GPS를 켜주세요.",
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction("확인", { enableGPS() }).show()
            return false
        }

        // 구글 맵 설정 - 내 위치 표시
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        requestingLocationUpdates = true

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    location.latitude
                    location.longitude

                    vm.setNewLocation(location.latitude, location.longitude)
                    showProgressCircular(true)
                }
                stopLocationUpdates()
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            }
        }
        startLocationUpdates()

        return true
    }

    private fun startLocationUpdates() {
        Log.e("","startLocationUpdate")
        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        try {
            synchronized(this){
                val voidTask = mFusedLocationClient.removeLocationUpdates(locationCallback)
            }
        } catch (exp: SecurityException) {
            Log.d("", " Security exception while removeLocationUpdates");
        }
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

        vm.currentLatLng.observe(this, Observer {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 12.0f))
            updateNearStore()
        })
    }

    private fun loadBitmap(resourceId: Int): Bitmap {
        return BitmapFactory.decodeResource(resources, resourceId)
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