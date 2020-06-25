package com.quicksterr.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(list: List<ProductItem>)

    @Query("SELECT * FROM ProductItem")
    fun getProducts(): List<ProductItem>
}