package com.pingwinek.jens.cookandbake

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData

class ConfigurationViewModel(application: Application) : AndroidViewModel(application) {

    val uid = MutableLiveData<String>()
}