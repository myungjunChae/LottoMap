package com.ono.lotto_map.ui.maps

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
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
import com.ono.lotto_map.R
import com.ono.lotto_map.application.MyApplication
import com.ono.lotto_map.databinding.ActivityMapsBinding
import com.ono.lotto_map.databinding.ViewInfoWindowBinding
import com.ono.lotto_map.showInfiniteSnackbar
import com.ono.lotto_map.showToast
import com.ono.lotto_map.startAction
import com.ono.lotto_map.ui.base.BaseActivity
import com.ono.lotto_map.util.PermissionUtil
import com.orhanobut.logger.Logger
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val LOCATION_INTERVAL: Long = 10000
private const val LOCATION_FASTEST_INTERVAL: Long = 5000
private const val LOCATION_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY
private const val PERMISSION_CHECKSUM: Int = 2001

class MapsActivity : BaseActivity<ActivityMapsBinding>(), OnMapReadyCallback {

    /** field **/
    override val resourceId: Int = R.layout.activity_maps
    private val viewModel: MapsViewModel by viewModel()

    //Google Map
    private lateinit var mMap: GoogleMap
    private var goldMarkerList = listOf<Marker>()
    private var silverMarkerList = listOf<Marker>()
    private var bronzeMarkerList = listOf<Marker>()

    //GPS
    private val locationRequest =
        LocationRequest()
            .setInterval(LOCATION_INTERVAL)
            .setFastestInterval(LOCATION_FASTEST_INTERVAL)
            .setPriority(LOCATION_PRIORITY)

    private val locationManager by lazy { getSystemService(LOCATION_SERVICE) as LocationManager }
    private val mFusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private val builder = LocationSettingsRequest.Builder().apply {
        addLocationRequest(locationRequest)
    }
    private var requestingLocationUpdates = false
    private lateinit var locationCallback: LocationCallback


    /** method **/
    //onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //DataBinding
        binding.mapsViewModel = viewModel
        binding.lifecycleOwner = this

        binding.goldSwitch.isChecked = true
        binding.silverSwitch.isChecked = true
        binding.bronzeSwitch.isChecked = true

        //Check EditText Empty
        binding.editText.setOnEditorActionListener { view, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                when (view.text.toString().isBlank()) {
                    true -> {
                        showToast("검색어를 입력해주세요.")
                    }
                    false -> {
                        viewModel.searchAddress(view.text.toString())
                        viewModel.onProgress()
                    }
                }
            }
            false
        }

        //Clear Button
        viewModel.isClear.observe(this, Observer { isClear ->
            if (isClear) {
                binding.editText.text.clear()
                viewModel.onClearComplete()
            }
        })

        //GPS Button
        binding.btnGps.setOnClickListener {
            if (PermissionUtil.getPermission(this, ACCESS_FINE_LOCATION, PERMISSION_CHECKSUM)) {
                getCurrentLocation()
            }
        }

        //Toggle Medal Visibility
        viewModel.isGoldChecked.observe(this, Observer { isChecked ->
            goldMarkerList.map { it.isVisible = isChecked }
        })

        viewModel.isSilverChecked.observe(this, Observer { isChecked ->
            silverMarkerList.map { it.isVisible = isChecked }
        })

        viewModel.isBronzeChecked.observe(this, Observer { isChecked ->
            bronzeMarkerList.map { it.isVisible = isChecked }
        })

        binding.goldSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setGoldState(isChecked)
        }

        binding.silverSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setSilverState(isChecked)
        }

        binding.bronzeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setBronzeState(isChecked)
        }

        //Store
        viewModel.goldMarkerOptionList.observe(this, Observer {
            val t = mutableListOf<Marker>()
            it.value?.map {
                t.add(mMap.addMarker(it.mMarkerOptions).apply {
                    tag = it.mIndex
                    isVisible = viewModel.isGoldChecked.value?:true
                })
            }
            goldMarkerList = t
        })

        viewModel.silverMarkerOptionList.observe(this, Observer {
            val t = mutableListOf<Marker>()
            it.value?.map {
                t.add(mMap.addMarker(it.mMarkerOptions).apply {
                    tag = it.mIndex
                    isVisible = viewModel.isSilverChecked.value?:true
                })
            }
            silverMarkerList = t
        })

        viewModel.bronzeMarkerOptionList.observe(this, Observer {
            val t = mutableListOf<Marker>()
            it.value?.map {
                t.add(mMap.addMarker(it.mMarkerOptions).apply {
                    tag = it.mIndex
                    isVisible = viewModel.isBronzeChecked.value?:true
                })
            }
            bronzeMarkerList = t
        })

        //Calling Intent
        viewModel.currentStorePhoneWithoutDash.observe(this, Observer {})

        binding.btnCall.setOnClickListener {
            startAction(
                Intent.ACTION_DIAL,
                "tel:${viewModel.currentStorePhoneWithoutDash.value}"
            )
        }

        //Map Intent
        binding.btnGoogleMap.setOnClickListener {
            startAction(
                Intent.ACTION_VIEW,
                "google.navigation:q=${viewModel.currentStore.value!!.location}"
            )
        }

        //Google Map
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Google Admob
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

    }

    //Map
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
            moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    viewModel.currentLatLng.value,
                    10.0f
                )
            ) //기본 좌표
            setInfoWindowAdapter(InfoWindowAdapter(this@MapsActivity))
        }

        viewModel.currentLatLng.observe(this, Observer {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 12.0f))
            mMap.clear()

            mMap.setOnMarkerClickListener {
                val index = it.tag.toString().toInt()
                val store = viewModel.storeInfoList.value?.get(index)
                viewModel.setCurrentStore(store)
                false
            }

            mMap.setOnInfoWindowCloseListener {
                viewModel.setCurrentStore(null)
            }

            viewModel.updateNearStore()

            //marker.tag = index

        })
    }

    //Runtime Permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CHECKSUM) {
            if (PermissionUtil.checkPermissions(this, ACCESS_FINE_LOCATION)) {
                getCurrentLocation()
            } else {
                if (PermissionUtil.shouldShowRequestPermissionRationale(
                        this,
                        ACCESS_FINE_LOCATION
                    )
                ) {
                    showInfiniteSnackbar(binding.layout, "퍼미션이 거부되었습니다.")
                } else {
                    showInfiniteSnackbar(
                        binding.layout,
                        "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다."
                    )
                }
            }
        }
    }

    //GPS
    private fun checkGpsEnable(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun startGpsPage() {
        startActivity(
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
            }
        )
    }

    private fun startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        try {
            synchronized(this) {
                val voidTask = mFusedLocationClient.removeLocationUpdates(locationCallback)
            }
        } catch (exp: SecurityException) {
            Logger.e("Security exception while removeLocationUpdates");
        }
    }

    private fun getCurrentLocation(): Boolean {
        if (!checkGpsEnable()) {
            showInfiniteSnackbar(binding.layout, "GPS가 꺼져있습니다. GPS를 켜주세요.") {
                startGpsPage()
            }
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

                    viewModel.setNewLocation(location.latitude, location.longitude)
                    viewModel.onProgress()
                }
                stopLocationUpdates()
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            }
        }
        startLocationUpdates()

        return true
    }
}

