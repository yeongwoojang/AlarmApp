package com.example.couroutinstudy.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.couroutinstudy.R
import com.example.couroutinstudy.databinding.ItemDayOfWeekBinding
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.model.vo.DayOfWeek
import com.example.couroutinstudy.viewmodel.BaseViewModel


class DayOfWeekAdapter(private val viewModel: BaseViewModel, val alarm: Alarm) :
    RecyclerView.Adapter<DayOfWeekAdapter.DayOfWeekViewHolder>() {
    private var thisObj = this
    private var items: List<DayOfWeek> = mutableListOf()

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

    fun updateItem(items: List<DayOfWeek>) {
        this.items = items
        notifyDataSetChanged()
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

    //요일을 체크 했을 때 실행되는 메소드
    fun itemClick(position: Int) {
        items[position].isCheck = !items[position].isCheck // DayOfWeek객체의 "isCheck" 를 변경
        alarm.dayOfWeek[position].isCheck = items[position].isCheck // 넘어온 알람객체에 변경사항 할당

        if (alarm.dayOfWeek[position].isCheck) { // 변경된 알람객체의 "isCheck" 값이 true일 경우
            val pId = (Math.random() * 100000000).toInt() //요일을 클릭하면 랜덤으로 requestCode를 생성
            alarm.dayOfWeek[position].requestCode = pId //해당 알람객체의 requestCode 값을 지정해준다.
        }else{
            alarm.dayOfWeek[position].requestCode = -1
        }
        viewModel.setAlarm(alarm) //프래그먼트간 공유할 alarm 객체를 업데이트
        notifyItemChanged(position) //리사이클러뷰에 변경사항 적용
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