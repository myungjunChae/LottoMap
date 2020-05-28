package com.ono.lotto_map.application

import android.content.Context
import android.content.SharedPreferences

const val PREF_NAME = "lotto"

class SharedPref private constructor(private val instance: SharedPreferences) {
    companion object {
        private var INSTANCE: SharedPref? = null

        fun getInstance(context: Context): SharedPref {
            if (INSTANCE == null) {
                val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                INSTANCE = SharedPref(pref)
            }
            return INSTANCE!!
        }
    }
    fun getBoolean(key: String, default: Boolean = false) =
        instance.getBoolean(key, default)

    fun getInt(key: String, default: Int = -1) =
        instance.getInt(key, default)

    fun getFloat(key: String, default: Float = -1.0f) =
        instance.getFloat(key, default)

    fun getLong(key: String, default: Long = -1L) =
        instance.getLong(key, default)

    fun getString(key: String, default: String? = null) =
        instance.getString(key, default)

    fun getStringSet(key: String, default: MutableSet<String>) =
        instance.getStringSet(key, default)

    fun setBoolean(key: String, default: Boolean = false) =
        instance.edit().putBoolean(key, default)?.apply()

    fun setInt(key: String, default: Int = -1) =
        instance.edit().putInt(key, default)?.apply()

    fun setFloat(key: String, default: Float = -1.0f) =
        instance.edit().putFloat(key, default)?.apply()

    fun setLong(key: String, default: Long = -1L) =
        instance.edit().putLong(key, default)?.apply()

    fun setString(key: String, default: String? = null) =
        instance.edit().putString(key, default)?.apply()

    fun setStringSet(key: String, default: MutableSet<String>) =
        instance.edit().putStringSet(key, default)?.apply()
}
