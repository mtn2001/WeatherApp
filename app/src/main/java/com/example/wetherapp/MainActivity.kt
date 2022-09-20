package com.example.wetherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.wetherapp.databinding.ActivityMainBinding
import com.example.wetherapp.fragments.MainFragment
import org.json.JSONObject

const val API_KEY = "6ee269bbc6f84189a7691212221909"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.plaseHolder, MainFragment.newInstance())
            .commit()
//        binding.button.setOnClickListener {
//        getResult("London")
//        }
    }
//    private fun getResult(name: String){
//        val url = "https://api.weatherapi.com/v1/current.json" +
//                "?key=$API_KEY&q=$name&aqi=no"
//        val queue = Volley.newRequestQueue(this)
//        val stringRequest = StringRequest(Request.Method.GET,
//            url,
//            {
//                response->
//                val obj = JSONObject(response)
//                val temp = obj.getJSONObject("current")
//
//                Log.d("MyLog","Temp: ${temp.getString("temp_c")}")
//
//            },
//            {
//                Log.d("MyLog","Volley error: $it")
//            }
//        )
//        queue.add(stringRequest)
//    }
}