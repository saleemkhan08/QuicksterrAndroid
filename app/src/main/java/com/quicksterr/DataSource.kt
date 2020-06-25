package com.quicksterr

import java.util.ArrayList

class DataSource {
    data class ProductItem(val id: String?, val name: String?, val price: String?)

    companion object {
        fun createDataSet(): ArrayList<ProductItem> {
            val productList = ArrayList<ProductItem>()
            var total = 0
            for (i in 1..30) {
                val productPrice = (200..1000).random()
                productList.add(
                    ProductItem(
                        "$i",
                        "Product$i",
                        "$productPrice"
                    )
                )
                total += productPrice
            }
            // To show Total
            productList.add(
                ProductItem(
                    null,
                    null,
                    "$total"
                )
            )
            // To show send receipt button
            productList.add(
                ProductItem(
                    null,
                    null,
                    null
                )
            )
            return productList
        }
    }
}
