package com.module.qrmodule.presentation.common.portList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.module.qrmodule.R
import com.module.qrmodule.domain.data.Port
import kotlinx.android.synthetic.main.port_item_list.view.*

class PortAdapter (private val data: ArrayList<Port>) : RecyclerView.Adapter<PortAdapter.ViewHolder<Port>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Port> {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.port_item_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder<Port>, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    fun setData(dataset: ArrayList<Port>) {
        data.clear()
        data.addAll(dataset)
        notifyDataSetChanged()
    }

    class ViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view: View = itemView

        fun bind(port: Port) {
            if (!port.isDisable)
                view.llPortView.background = ContextCompat.getDrawable(view.context, R.drawable.rounded_bg_enable)
            view.tvPortNumber.text = port.id.toString()
        }
    }
}