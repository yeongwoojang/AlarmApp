package com.example.couroutinstudy.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.couroutinstudy.R
import com.example.couroutinstudy.databinding.ItemDayOfWeekBinding
import com.example.couroutinstudy.model.vo.DayOfWeek


class DayOfWeekAdapter() : RecyclerView.Adapter<DayOfWeekAdapter.DayOfWeekViewHolder>() {
    private var thisObj = this

    inner class DayOfWeekViewHolder(private val binding: ItemDayOfWeekBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.adapter = thisObj
        }

        fun bind(item: DayOfWeek, position: Int) {
            binding.dayOfWeek = item
            binding.position = position
        }
    }

    private var items = ArrayList<DayOfWeek>()

    init {
        initData()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayOfWeekViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDayOfWeekBinding.inflate(inflater, parent, false)
        return DayOfWeekViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: DayOfWeekViewHolder, position: Int) {
        val dayOfWeek = items[position]
        holder.bind(dayOfWeek, position)


    }

    fun itemClick(position: Int) {
        items[position].isCheck = !items[position].isCheck
        notifyItemChanged(position)
    }

    private fun initData() {
        items.add(DayOfWeek("월요일마다", false))
        items.add(DayOfWeek("화요일마다", false))
        items.add(DayOfWeek("수요일마다", false))
        items.add(DayOfWeek("목요일마다", false))
        items.add(DayOfWeek("금요일마다", false))
        items.add(DayOfWeek("토요일마다", false))
        items.add(DayOfWeek("일요일마다", false))
    }
}

@BindingAdapter("img") //리스트의 버튼 색을 바꿔주기 위한 바인딩어댑터
fun setImage(imageView: ImageView, dayOfWeek: DayOfWeek) {
    var resId = R.drawable.icon_uncheck
    when (dayOfWeek.isCheck) {
        true -> resId = R.drawable.icon_check
        false -> resId = R.drawable.icon_uncheck
    }
    imageView.setImageResource(resId)
}