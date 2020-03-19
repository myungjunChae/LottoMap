package com.ono.lotto_map

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

inline fun <reified T : Activity> Activity.startActivity(bundle: Bundle? = null) {
    startActivity(Intent(this, T::class.java), bundle)
}


inline fun <reified T : Activity> Activity.startActivityWithFinish(bundle: Bundle? = null) {
    startActivity<T>(bundle)
    finish()
}

fun Context.showToast(text:String){
    Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
}

fun Context.readJsonFromAsset(fileName: String) =
    assets.open(fileName).bufferedReader().use { it.readText() }