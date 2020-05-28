package com.ono.lotto_map

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.ono.lotto_map.datasource.remote.StoreRemoteDataSource
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.TimeUnit

class StoreRemoteDataSourceTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun downloadStoreDataFromS3() {
        var success = false
        val dataSource = StoreRemoteDataSource(context)

        dataSource.downloadStoreDataFromS3()
            .test()
            .awaitDone(1L, TimeUnit.MINUTES)
            .assertComplete()
    }
}