package com.ono.lotto_map.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

interface ResourceProvider {
    fun loadBitmap(resourceId: Int): Bitmap
}

class ResourceProviderImpl(private val context : Context) : ResourceProvider {
    override fun loadBitmap(resourceId: Int): Bitmap {
        return BitmapFactory.decodeResource(context.resources, resourceId)
    }
}