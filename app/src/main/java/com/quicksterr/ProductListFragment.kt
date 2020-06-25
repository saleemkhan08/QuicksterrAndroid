package com.quicksterr

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

class ProductListFragment : Fragment(), View.OnClickListener {
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.product_list_fragment, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            recyclerView = view
            recyclerView?.layoutManager = LinearLayoutManager(context)
            recyclerView?.adapter = ProductRecyclerAdapter(DataSource.createDataSet(), this)
        }
        return view
    }

    override fun onClick(v: View?) {
        Toast.makeText(context, "Send Receipt", Toast.LENGTH_SHORT).show();
    }
}