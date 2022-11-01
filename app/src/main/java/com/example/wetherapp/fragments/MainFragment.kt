package com.example.wetherapp.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings

import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.wetherapp.MainViewModel
import com.example.wetherapp.adapters.VpAdapter
import com.example.wetherapp.adapters.WetherModel
import com.example.wetherapp.databinding.FragmentMainBinding
import com.example.wetherapp.dialogManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject


const val API_KEY = "5e1690d3e78a4ac4b5404453221610"

class MainFragment : Fragment() {
    private lateinit var fLocationClient: FusedLocationProviderClient
    private val flist = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance()
    )
    private val tlist = listOf(
        "Hours",
        "Days",
        ""
    )
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val model: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        init()
        updateCurrentCard()
    }

    private fun init() = with(binding) {
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val adapter = VpAdapter(activity as FragmentActivity, flist)
        vp.adapter = adapter
        TabLayoutMediator(tabLayout, vp) { tab, p ->
            tab.text = tlist[p]
        }.attach()
        imageButton.setOnClickListener {
            tabLayout.selectTab(tabLayout.getTabAt(0))
            checkLocation()
        }
        imageButton7.setOnClickListener{
            dialogManager.serchByName(requireContext(),object : dialogManager.Listener{
                override fun onClick(name: String?) {
                    Log.d("MyLog", "$name")
                    name?.let { it1 -> reqvestWeatherData(it1) }
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }
    private fun isLocationEnabled(): Boolean{
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation() {
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener {
                reqvestWeatherData("${it.result.latitude},${it.result.longitude}")
            }
    }

    private fun checkLocation(){
        if (isLocationEnabled()){
            getLocation()
        }else{
            dialogManager.locationSettingsDialog(requireContext(),object : dialogManager.Listener{
                override fun onClick(name: String?) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    private fun updateCurrentCard() = with(binding) {
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            val maxMinTemp = "${it.maxTemp}°/${it.minTemp}"
            tvDateQ.text = it.time
            textView7.text = it.city
            textView6.text = it.currentTemp.ifEmpty { maxMinTemp }+"°"
            textView8.text = it.condition
            textView9.text = if (it.currentTemp.isEmpty()) "" else maxMinTemp
            Picasso.get().load("https:" + it.imageUrl).into(imageView6)
            Log.d("MyLog","${it.city} ")
        }
    }

    private fun permissionListener() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun reqvestWeatherData(city: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +
                "&q=" +
                city +
                "&days=" +
                "10" +
                "&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { result ->
                parseWeatherData(result)

            },
            {
                Log.d("MyLog", "Volley error: $it")
            }
        )
        queue.add(stringRequest)
    }

    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])

    }

    private fun parseDays(mainObject: JSONObject): List<WetherModel> {
        val list = ArrayList<WetherModel>()
        val daysArray = mainObject.getJSONObject("forecast")
            .getJSONArray("forecastday")
        val name = mainObject.getJSONObject("location").getString("name")
        for (i in 0 until daysArray.length()) {
            val day = daysArray[i] as JSONObject
            val item = WetherModel(
                name,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition").getString("text"),
                "",
                day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                day.getJSONArray("hour").toString()
            )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem: WetherModel) {

        val item = WetherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current")
                .getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getString("temp_c").toFloat().toInt().toString(),
            weatherItem.maxTemp.toFloat().toInt().toString(),
            weatherItem.minTemp.toFloat().toInt().toString(),
            mainObject.getJSONObject("current")
                .getJSONObject("condition").getString("icon"),
            weatherItem.hours,
        )
        model.liveDataCurrent.value = item
    }


    companion object {
        fun newInstance() = MainFragment().apply {

        }
    }
}