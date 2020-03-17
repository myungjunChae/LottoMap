package com.ono.lotto_map

import com.ono.lotto_map.data.response.Geometry
import com.ono.lotto_map.datasource.remote.MapsApi
import com.ono.lotto_map.datasource.remote.MapsRemoteDataSource
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    val baseUrl = "https://maps.googleapis.com/maps/api/geocode/json/"

    val LogInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder().apply {
        addNetworkInterceptor(LogInterceptor)
        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(30, TimeUnit.SECONDS)
    }.build()

    val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    val remoteDatasource = MapsRemoteDataSource(retrofit.create(MapsApi::class.java))

    @Test
    fun `same latlng`() {
        var actual : Geometry
        remoteDatasource.searchAddress("서울")
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    val lat=37.566535
                    val lng=126.9779692
                    assertEquals(lat, it.location.lat)
                    assertEquals(lng, it.location.lat)
                },{}
            )
    }
}
