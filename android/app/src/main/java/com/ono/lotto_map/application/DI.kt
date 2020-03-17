package com.ono.lotto_map.application

import com.ono.lotto_map.datasource.local.MapsLocalDataSource
import com.ono.lotto_map.datasource.remote.MapsApi
import com.ono.lotto_map.datasource.remote.MapsRemoteDataSource
import com.ono.lotto_map.domain.MapsRepositoryImpl
import com.ono.lotto_map.domain.MapsUsecase
import com.ono.lotto_map.presentation.MapsViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

fun injectionFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(
        listOf(
            viewModelModule,
            usecaseModule,
            repositoryModule,
            localDataSourceModule,
            remoteDataSourceModule,
            apiModule
        )
    )
}

internal val viewModelModule: Module = module {
    viewModel { MapsViewModel(get()) }
}

internal val usecaseModule: Module = module {
    factory { MapsUsecase(get(named("t"))) }
}

internal val repositoryModule: Module = module {
    single(named("t")) { MapsRepositoryImpl(get(named("t1")), get(named("t2"))) }
}

internal val localDataSourceModule: Module = module {
    single(named("t1")) { MapsLocalDataSource() }
}

internal val remoteDataSourceModule: Module = module {
    single(named("t2")) { MapsRemoteDataSource(get()) }
}

internal val apiModule: Module = module {
    single { mapsApi }
}

internal const val BASE_URL = "https://maps.googleapis.com/maps/api/geocode/"

internal val LogInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

internal val client = OkHttpClient.Builder().apply {
    addNetworkInterceptor(LogInterceptor)
    connectTimeout(30, TimeUnit.SECONDS)
    readTimeout(30, TimeUnit.SECONDS)
}.build()

internal val retrofit: Retrofit =
    Retrofit.Builder()
        .client(client)
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

internal val mapsApi: MapsApi = retrofit.create(MapsApi::class.java)