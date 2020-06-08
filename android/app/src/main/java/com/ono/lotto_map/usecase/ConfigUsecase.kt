package com.ono.lotto_map.usecase

import com.ono.lotto_map.repository.ConfigRepository

class ConfigUsecase(private val configRepository: ConfigRepository) {
    fun getIsFirst() : Boolean {
        return configRepository.getIsFirst()
    }

    fun isFirstComplete(){
        configRepository.isFirstComplete()
    }
}