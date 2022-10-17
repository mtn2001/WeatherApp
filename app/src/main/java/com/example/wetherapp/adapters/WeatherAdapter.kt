package com.example.wetherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wetherapp.R
import com.example.wetherapp.databinding.ListItemBinding
import com.squareup.picasso.Picasso

class WeatherAdapter(val litener: Listener?) : ListAdapter<WetherModel,WeatherAdapter.Holder>(Comparator()){
    class Holder(view: View, val litener: Listener?) : RecyclerView.ViewHolder(view){
        val binding = ListItemBinding.bind(view)
        var itemTemp: WetherModel? = null
        init {
            itemView.setOnClickListener{
                itemTemp?.let { it1 -> litener?.onClick(it1) }
            }
        }
        fun bind(item: WetherModel) = with(binding) {
            itemTemp = item
            tvDate.text = item.time
            tvCondition.text = item.condition
            tvTemp.text = item.currentTemp.ifEmpty { "${item.maxTemp}° / ${item.minTemp}°" }
            Picasso.get().load("https:"+item.imageUrl).into(imageView)
        }
    }
    class Comparator: DiffUtil.ItemCallback<WetherModel>() {
        override fun areItemsTheSame(oldItem: WetherModel, newItem: WetherModel): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(oldItem: WetherModel, newItem: WetherModel): Boolean {
            return newItem == oldItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent,false)
        return Holder(view,litener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
    interface Listener{
        fun onClick(item: WetherModel)
    }
}