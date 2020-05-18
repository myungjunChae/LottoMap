package com.ono.lotto_map.util

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

object PermissionUtil {
    private val GRANTED = PackageManager.PERMISSION_GRANTED
    private const val VERSION_MASHEMELLO = Build.VERSION_CODES.M

    fun getPermission(activity: Activity, permission: String, checkSum: Int): Boolean {
        if (checkVersionM()) {
            println("마시멜로우")
            if (!checkPermissions(activity, permission)) {
                println("퍼미션 없음")
                if (shouldShowRequestPermissionRationale(
                        activity,
                        permission
                    )
                ) {
                    println("이전에 실행")
                    requestPermissions(
                        activity,
                        arrayOf(permission),
                        checkSum
                    )
                    return false
                } else {
                    println("초기실행")
                    requestPermissions(
                        activity,
                        arrayOf(permission),
                        checkSum
                    )
                    return false
                }
            }
        }
        return true
    }

    fun checkPermissions(activity: Activity, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(activity, permission) == GRANTED
    }

    fun shouldShowRequestPermissionRationale(
        activity: Activity,
        permission: String
    ): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    fun checkVersionM(): Boolean {
        return Build.VERSION.SDK_INT >= VERSION_MASHEMELLO
    }

    fun requestPermissions(activity: Activity, permissions: Array<String>, checkSum: Int) {
        for (permission in permissions) {
            println("request")
            ActivityCompat.requestPermissions(activity, permissions, checkSum)
        }
    }
}
