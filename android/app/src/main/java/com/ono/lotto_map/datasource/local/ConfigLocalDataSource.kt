package com.ono.lotto_map.datasource.local

import android.content.Context
import com.ono.lotto_map.application.SharedPref

const val IS_FIRST = "isFirst"

class ConfigLocalDataSource(context: Context) {
    val pref by lazy { SharedPref.getInstance(context) }

    fun getIsFirst(): Boolean {
        return pref.getBoolean(IS_FIRST, true)
    }

    fun isFirstComplete() {
        pref.setBoolean(IS_FIRST, false)
    }
}
