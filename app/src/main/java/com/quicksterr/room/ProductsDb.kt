package com.quicksterr.room

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [ProductItem::class])
abstract class ProductsDb : RoomDatabase() {
    companion object {
        fun get(application: Application): ProductsDb {
            return Room.databaseBuilder(application, ProductsDb::class.java, "products")
                .build()
        }
    }

    abstract fun getProductDao(): ProductsDao
}