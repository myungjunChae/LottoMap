package com.ono.lotto_map

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar

inline fun <reified T : Activity> Activity.startActivity(bundle: Bundle? = null) {
    startActivity(Intent(this, T::class.java), bundle)
}


inline fun <reified T : Activity> Activity.startActivityWithFinish(bundle: Bundle? = null) {
    startActivity<T>(bundle)
    finish()
}

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.readJsonFromAsset(fileName: String) =
    assets.open(fileName).bufferedReader().use { it.readText() }

fun GoogleMap.drawRadius(position: LatLng) {
    val circle = CircleOptions().apply {
        center(position)
        radius(20.0)
        strokeColor(Color.BLACK)
        fillColor(0x30ff0000)
        strokeWidth(2f)
    }

    this.addCircle(circle)
}