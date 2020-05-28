package com.ono.lotto_map.repository

import com.ono.lotto_map.datasource.local.ConfigLocalDataSource

class ConfigRepository(private val configLocalDataSource : ConfigLocalDataSource) {
    fun getIsFirst() : Boolean {
        return configLocalDataSource.getIsFirst()
    }

    fun setIsNotFirst(){
        configLocalDataSource.setIsNotFirst()
    }
}