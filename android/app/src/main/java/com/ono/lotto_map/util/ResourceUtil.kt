package com.ono.lotto_map.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

object ResourceUtil{
    fun loadBitmap(context : Context, resourceId: Int): Bitmap {
        return BitmapFactory.decodeResource(context.resources, resourceId)
    }
}