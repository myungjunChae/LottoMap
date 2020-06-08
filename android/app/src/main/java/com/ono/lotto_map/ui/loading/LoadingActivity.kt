package com.ono.lotto_map.ui.loading

import android.os.Bundle
import androidx.lifecycle.Observer
import com.ono.lotto_map.R
import com.ono.lotto_map.databinding.ActivityLoadingBinding
import com.ono.lotto_map.startActivityWithFinish
import com.ono.lotto_map.ui.base.BaseActivity
import com.ono.lotto_map.ui.maps.MapsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoadingActivity : BaseActivity<ActivityLoadingBinding>() {
    override val resourceId: Int = R.layout.activity_loading
    private val vm: LoadingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm.isFirst.observe(this, Observer { isFirst ->
            if (!isFirst) {
                startActivityWithFinish<MapsActivity>()
            }
        })

        isFirst()
    }

    private fun isFirst() {
        if (vm.isFirst.value!!) {
            vm.getStoreDataFromLocalStorage()
            vm.isFirstComplete()
        }
    }
}