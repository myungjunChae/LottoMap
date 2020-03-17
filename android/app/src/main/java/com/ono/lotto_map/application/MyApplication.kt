package com.ono.lotto_map.application

import android.app.Application
import com.google.gson.Gson
import com.ono.lotto_map.data.entity.StoreInfo
import com.ono.lotto_map.readJsonFromAsset
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application(){
    val storeInfos by lazy {
        Gson().fromJson(readJsonFromAsset("data.json"), Array<StoreInfo>::class.java).toList()
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            injectionFeature()
        }
    }
}