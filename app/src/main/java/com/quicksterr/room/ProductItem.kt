package com.quicksterr.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductItem(
    @PrimaryKey val id: String,
    val name: String?,
    val price: Double?,
    val quantity: Double?
)