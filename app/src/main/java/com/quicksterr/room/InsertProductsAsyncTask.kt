package com.quicksterr.room

import android.app.Application
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.opencsv.CSVReader
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.lang.Exception
import java.nio.charset.Charset

class InsertProductsAsyncTask(
    private val uri: Uri?,
    private val application: Application
) :
    AsyncTask<Void, Void, Void>() {
    private val list = ArrayList<ProductItem>()

    override fun doInBackground(vararg params: Void?): Void? {
        try {
            val inputStream = application.contentResolver.openInputStream(uri!!)
            var content = inputStream?.readBytes()?.toString(Charset.defaultCharset())
            var lines = content?.split("\n")
            lines = lines?.slice(1 until lines.size)

            lines?.forEach {
                val items = it.split(",")
                list.add(ProductItem(items[0], items[1], items[2].toDouble(), items[3].toDouble()))
            }
            ProductsDb.get(application).getProductDao().insertProducts(list)
            ProductsDb.get(application).getProductDao().getProducts().forEach {
                Log.v("SalData", "${it.id} , ${it.name}, ${it.price}, ${it.quantity}")
            }

        } catch (err: Exception) {
            Log.v("SalData", "$err")
        }

        return null
    }
}