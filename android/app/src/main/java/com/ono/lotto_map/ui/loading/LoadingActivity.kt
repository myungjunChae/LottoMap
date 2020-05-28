package com.ono.lotto_map.ui.loading

import android.os.Bundle
import com.ono.lotto_map.R
import com.ono.lotto_map.application.MyApplication
import com.ono.lotto_map.databinding.ActivityLoadingBinding
import com.ono.lotto_map.ui.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoadingActivity : BaseActivity<ActivityLoadingBinding>() {
    override val resourceId: Int = R.layout.activity_loading
    private val vm: LoadingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when(vm.isFirst.value!!){
            true->{
                vm.downloadStoreDataFromS3()
            }
            false->{}
        }
    }

    private fun onDownloadSuccess() {
        val myApplication = application as MyApplication
    }
}