package com.ono.lotto_map.application

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ono.lotto_map.data.local.StoreInfoEntity
import com.ono.lotto_map.datasource.local.StoreInfoDao

@Database(entities = [StoreInfoEntity::class], version = 1, exportSchema = false)
abstract class StoreInfoDatabase : RoomDatabase() {
    abstract val storeInfoDao: StoreInfoDao
    companion object {
        @Volatile
        private var INSTANCE: StoreInfoDatabase? = null

        fun getInstance(context: Context): StoreInfoDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        StoreInfoDatabase::class.java,
                        "store_info_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}