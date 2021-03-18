package com.example.couroutinstudy.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.couroutinstudy.R
import com.example.couroutinstudy.databinding.FragmentDayOfWeekBinding
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.view.activity.MainActivity
import com.example.couroutinstudy.view.adapter.DayOfWeekAdapter
import com.example.couroutinstudy.viewmodel.BaseViewModel

class DayOfWeekFrag : Fragment() {

    private var _binding : FragmentDayOfWeekBinding? = null
    private val binding get() =_binding!!
    private lateinit var viewModel: BaseViewModel
    private lateinit var mActivity : MainActivity
    private lateinit var alarm : Alarm

    override fun onAttach(context: Context) { //onAttach
        super.onAttach(context)
        mActivity = activity as MainActivity
    }

    override fun onCreateView(  //onCreateView
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDayOfWeekBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {  //onViewCreated
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[BaseViewModel::class.java]

        val bundle = mActivity.bundle
        alarm = bundle?.getSerializable("alarmInfo") as Alarm

        Log.d("TEST", "onViewCreated: ${alarm.time}")
        val adapter = DayOfWeekAdapter(viewModel,alarm)


        binding.dayOfweekRv.let{ //recyclerView 설정
            it.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            it.adapter = adapter
            adapter.updateItem(alarm.dayOfWeek)
        }

        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

    }


}