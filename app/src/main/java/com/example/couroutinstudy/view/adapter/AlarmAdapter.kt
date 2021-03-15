package com.example.couroutinstudy.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.couroutinstudy.databinding.ItemAlarmBinding
import com.example.couroutinstudy.model.vo.Alarm

class AlarmViewHolder(private val binding: ItemAlarmBinding) :
    RecyclerView.ViewHolder(binding.root) {


    fun bind(alarm : Alarm, position : Int){
        binding.alarm = alarm
        binding.position = position
    }
}

class AlarmAdapter : RecyclerView.Adapter<AlarmViewHolder>() {
    private var items : List<Alarm> = ArrayList<Alarm>()

    fun updateItems(items : List<Alarm>){
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAlarmBinding.inflate(inflater,parent,false)
        return AlarmViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item,position)
    }
}