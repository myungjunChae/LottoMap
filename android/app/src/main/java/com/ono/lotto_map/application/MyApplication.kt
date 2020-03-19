package com.ono.lotto_map.application

import android.app.Application
import com.google.gson.Gson
import com.ono.lotto_map.data.entity.StoreInfoEntity
import com.ono.lotto_map.data.entity.toModel
import com.ono.lotto_map.readJsonFromAsset
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application(){
    val storeInfos by lazy {
        Gson().fromJson(readJsonFromAsset("data.json"), Array<StoreInfoEntity>::class.java).toList().toModel()
            .sortedByDescending { it.score }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            injectionFeature()
        }
    }
}