package com.example.wetherapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wetherapp.adapters.WetherModel

class MainViewModel : ViewModel() {
    val liveDataCurrent = MutableLiveData<WetherModel>()
    val liveDataList = MutableLiveData<List<WetherModel>>()

}