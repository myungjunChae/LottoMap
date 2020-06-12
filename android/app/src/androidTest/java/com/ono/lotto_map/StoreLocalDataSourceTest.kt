package com.ono.lotto_map

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ono.lotto_map.application.StoreInfoDatabase
import com.ono.lotto_map.data.local.StoreInfoEntity
import com.ono.lotto_map.datasource.local.StoreInfoDao
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import io.reactivex.schedulers.Schedulers
import org.hamcrest.core.IsEqual.equalTo
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.internal.runners.statements.Fail
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class StoreLocalDataSourceTest {
    private lateinit var userDao: StoreInfoDao
    private lateinit var db: StoreInfoDatabase
    private lateinit var storeList: List<StoreInfoEntity>

    @Before()
    fun setLogger(){
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, StoreInfoDatabase::class.java).build()
        userDao = db.storeInfoDao

        val store1: StoreInfoEntity = StoreInfoEntity(0, 0, 0, 0.0, 0.0, "", "", "store1", 0)
        val store2: StoreInfoEntity = StoreInfoEntity(1, 0, 0, 0.0, 0.0, "", "", "store2", 0)
        val store3: StoreInfoEntity = StoreInfoEntity(2, 0, 0, 0.0, 0.0, "", "", "store3", 0)

        userDao.insert(store1)
            .test()
            .assertNoErrors()
            .assertSubscribed()
            .assertComplete()
            .dispose()

        userDao.insert(store2)
            .test()
            .assertNoErrors()
            .assertSubscribed()
            .assertComplete()
            .dispose()

        userDao.insert(store3)
            .test()
            .assertNoErrors()
            .assertSubscribed()
            .assertComplete()
            .dispose()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun `insert_is_amount_same`() {
        val actual = 3

        userDao.getAllStore()
            .test()
            .awaitDone(1L, TimeUnit.MINUTES)
            .assertValue { it.size == actual }
            .dispose()
    }

    @Test
    @Throws(Exception::class)
    fun `insert_is_store_name_correct`() {
        val actual = hashMapOf<Int, String>(0 to "store1", 1 to "store2", 2 to "store3")

        userDao.getAllStore()
            .test()
            .awaitDone(1L, TimeUnit.MINUTES)
            .assertValue{
                it[0].shop == actual.get(it[0].store_id)
            }
            .dispose()
    }
}
