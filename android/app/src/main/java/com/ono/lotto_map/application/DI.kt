package com.ono.lotto_map.application

import com.ono.lotto_map.datasource.local.ConfigLocalDataSource
import com.ono.lotto_map.datasource.local.StoreLocalDataSource
import com.ono.lotto_map.datasource.remote.MapsApi
import com.ono.lotto_map.datasource.remote.MapsRemoteDataSource
import com.ono.lotto_map.datasource.remote.StoreRemoteDataSource
import com.ono.lotto_map.repository.*
import com.ono.lotto_map.ui.loading.LoadingViewModel
import com.ono.lotto_map.ui.maps.MapsViewModel
import com.ono.lotto_map.usecase.ConfigUsecase
import com.ono.lotto_map.usecase.MapsUsecase
import com.ono.lotto_map.usecase.StoreInfoUsecase
import com.ono.lotto_map.util.ResourceProviderImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
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
            providerModule,
            usecaseModule,
            repositoryModule,
            localDataSourceModule,
            remoteDataSourceModule,
            apiModule
        )
    )
}

internal val viewModelModule: Module = module {
    viewModel { MapsViewModel(get(), get(), get(named("provider"))) }
    viewModel { LoadingViewModel(get(), get()) }
}

internal val providerModule: Module = module {
    single(named("provider")) { ResourceProviderImpl(androidContext()) }
}

internal val usecaseModule: Module = module {
    factory { MapsUsecase(get(named("mapRepository"))) }
    factory { StoreInfoUsecase(get(named("storeRepository"))) }
    factory { ConfigUsecase(get(named("configRepository"))) }
}

internal val repositoryModule: Module = module {
    single(named("mapRepository")) { MapsRepository(get(named("mapRemote"))) }
    single(named("storeRepository")) {
        StoreInfoRepository(
            get(named("storeLocal")),
            get(named("storeRemote"))
        )
    }
    single(named("configRepository")) { ConfigRepository(get(named("configLocal"))) }
}

internal val localDataSourceModule: Module = module {
    single(named("storeLocal")) { StoreLocalDataSource(androidContext()) }
    single(named("configLocal")) { ConfigLocalDataSource(androidContext()) }
}

internal val remoteDataSourceModule: Module = module {
    single(named("mapRemote")) { MapsRemoteDataSource(androidContext(), get()) }
    single(named("storeRemote")) { StoreRemoteDataSource(androidContext()) }
}

internal val apiModule: Module = module {
    single { mapsApi }
}

internal const val MAPS_API_URL = "https://maps.googleapis.com/maps/api/geocode/"

internal val LogInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

internal val client = OkHttpClient.Builder().apply {
    addNetworkInterceptor(LogInterceptor)
    connectTimeout(30, TimeUnit.SECONDS)
    readTimeout(30, TimeUnit.SECONDS)
}.build()

internal val mapsRetrofit: Retrofit =
    Retrofit.Builder()
        .client(client)
        .baseUrl(MAPS_API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

internal val mapsApi: MapsApi = mapsRetrofit.create(MapsApi::class.java)




