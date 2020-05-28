package com.ono.lotto_map.application

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ono.lotto_map.data.local.StoreInfoEntity
import com.ono.lotto_map.datasource.local.StoreInfoDao

@Database(entities = [StoreInfoEntity::class], version = 1, exportSchema = false)
abstract class StoreDatabase : RoomDatabase() {
    abstract val storeInfoDao: StoreInfoDao
    companion object {
        @Volatile
        private var INSTANCE: StoreDatabase? = null

        fun getInstance(context: Context): StoreDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        StoreDatabase::class.java,
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