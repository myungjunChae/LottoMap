package com.ono.lotto_map.application

import android.app.Application
import com.google.gson.Gson
import com.ono.lotto_map.data.remote.StoreInfoResponse
import com.ono.lotto_map.data.remote.toModel
import com.ono.lotto_map.readJsonFromAsset
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    val storeInfos by lazy {
        Gson().fromJson(readJsonFromAsset("data.json"), Array<StoreInfoResponse>::class.java)
            .toList().toModel()
            .sortedByDescending { it.score }
    }

    val storeDatabase by lazy {
        StoreDatabase.getInstance(this)
    }

    val storeDao by lazy {
        StoreDatabase.getInstance(this).storeInfoDao
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            injectionFeature()
        }

        Logger.addLogAdapter(AndroidLogAdapter())
    }
}