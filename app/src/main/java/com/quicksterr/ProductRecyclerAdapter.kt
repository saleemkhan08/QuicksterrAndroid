package com.quicksterr

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.quicksterr.DataSource.ProductItem
import kotlinx.android.synthetic.main.product_list_item.view.*
import kotlinx.android.synthetic.main.product_list_item.view.productName
import kotlinx.android.synthetic.main.product_total.view.*
import kotlinx.android.synthetic.main.send_receipt_button.view.*

class ProductRecyclerAdapter(
    items: ArrayList<ProductItem>,
    sendReceiptClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items = ArrayList<ProductItem>()
    private var sendReceiptClickListener: View.OnClickListener?

    companion object {
        const val TYPE_PRODUCT = 0
        const val TYPE_TOTAL = 1
        const val TYPE_SEND_RECEIPT = 2
    }

    init {
        this.items = items
        this.sendReceiptClickListener = sendReceiptClickListener
    }

    // 1. Option to remove item
    // 2. Option to increase item count
    // 3. Option to change price.

    override fun getItemViewType(position: Int): Int {
        return if (items[position].price == null)
            TYPE_SEND_RECEIPT
        else if (items[position].id == null)
            TYPE_TOTAL
        else
            TYPE_PRODUCT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_PRODUCT -> ProductViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.product_list_item, parent, false)
            )
            TYPE_SEND_RECEIPT -> SendReceiptViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.send_receipt_button, parent, false)
            )
            else -> ProductTotalViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.product_total, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is ProductViewHolder -> {
                holder.bind(item)
            }
            is ProductTotalViewHolder -> {
                holder.bind(item.price)
            }
            is SendReceiptViewHolder -> {
                holder.bind(this.sendReceiptClickListener)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemNumberView: TextView = itemView.itemNumber
        private val productNameView: TextView = itemView.productName
        private val productPriceView: TextView = itemView.productPrice

        fun bind(productItem: ProductItem) {
            itemNumberView.text = productItem.id
            productNameView.text = productItem.name
            productPriceView.text = productItem.price
        }
    }

    inner class ProductTotalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productPriceTotal: TextView = itemView.productPriceTotal

        fun bind(total: String?) {
            productPriceTotal.text = total
        }
    }

    inner class SendReceiptViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sendReceipt: Button = itemView.sendReceipt

        fun bind(clickListener: View.OnClickListener?) {
            sendReceipt.setOnClickListener(clickListener)
        }
    }
}