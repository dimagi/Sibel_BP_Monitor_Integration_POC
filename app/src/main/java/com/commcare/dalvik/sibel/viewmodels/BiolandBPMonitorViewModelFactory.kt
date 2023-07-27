@file:Suppress("KDocMissingDocumentation")

package com.commcare.dalvik.sibel.viewmodels

import com.sibelhealth.bluetooth.sensor.bloodpressuremonitor.biolandbloodpressuremonitor.BiolandBloodPressureMonitor
import com.sibelhealth.bluetooth.sensor.glucosemeter.g427bglucosemeter.G427BGlucoseMeter
import com.sibelhealth.bluetooth.sensor.sibel.SibelSensor



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sibelhealth.core.sensor.Sensor


class BiolandBPMonitorViewModelFactory(private val sensor: Sensor) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when (sensor) {
            is BiolandBloodPressureMonitor -> if (modelClass.isAssignableFrom(BiolandBPMonitoringViewModel::class.java)) return BiolandBPMonitoringViewModel(sensor) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}