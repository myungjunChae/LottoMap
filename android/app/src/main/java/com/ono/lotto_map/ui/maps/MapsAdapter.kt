package com.ono.lotto_map.ui.maps

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.ono.lotto_map.R
import com.ono.lotto_map.application.MyApplication
import com.ono.lotto_map.databinding.ViewInfoWindowBinding

class InfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {
    private val application = context.applicationContext as MyApplication

    override fun getInfoWindow(marker: Marker?): View? {
        val inflater = LayoutInflater.from(context)
        val binding: ViewInfoWindowBinding =
            DataBindingUtil.inflate(inflater, R.layout.view_info_window, null, false)
        val index = marker?.tag.toString().toInt()
        val score = application.storeInfos[index].score

        binding.shopTitle.text = application.storeInfos[index].shop

        when {
            score >= 30 -> binding.rank.setTextColor(context.resources.getColor(R.color.colorGold))
            score in 10 until 30 -> binding.rank.setTextColor(context.resources.getColor(R.color.colorSilver))
            else -> binding.rank.setTextColor(context.resources.getColor(R.color.colorBronze))

        }
        binding.rank.text = "${(index + 1)}등"
        binding.firstWinning.text = "1등 : ${application.storeInfos[index].first_winning}회"
        binding.secondWinning.text = "2등 : ${application.storeInfos[index].second_winning}회"
        return binding.root
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }
}