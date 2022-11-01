package com.example.wetherapp.fragments

import android.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.example.wetherapp.MainViewModel
import com.example.wetherapp.R
import com.example.wetherapp.adapters.WeatherAdapter
import com.example.wetherapp.adapters.WetherModel
import com.example.wetherapp.databinding.FragmentHoursBinding
import org.json.JSONArray
import org.json.JSONObject

class HoursFragment : Fragment() {
        private lateinit var binding: FragmentHoursBinding
        private lateinit var adapter: WeatherAdapter
        private val model: MainViewModel by activityViewModels()
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHoursBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        model.liveDataCurrent.observe(viewLifecycleOwner){
        adapter.submitList(getHoursList(it))
        }
    }
    private fun initRcView() = with(binding){
        rcview.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        adapter = WeatherAdapter(null)
        rcview.adapter = adapter

    }

    private fun getHoursList(WItem: WetherModel): List<WetherModel>{
        val hoursArray = JSONArray(WItem.hours)
        val list = ArrayList<WetherModel>()
        for (i in 0 until hoursArray.length()){
            val item = WetherModel(
                WItem.city,
                (hoursArray[i] as JSONObject).getString("time"),
                (hoursArray[i] as JSONObject).getJSONObject("condition")
                    .getString("text"),
                (hoursArray[i] as JSONObject).getString("temp_c").toFloat().toInt().toString(),
                "",
                "",
                (hoursArray[i] as JSONObject).getJSONObject("condition")
                    .getString("icon"),
                ""
            )
            list.add(item)
        }
        return list
    }

    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}