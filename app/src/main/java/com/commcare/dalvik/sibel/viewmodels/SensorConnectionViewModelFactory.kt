@file:Suppress("KDocMissingDocumentation")

package com.commcare.dalvik.sibel.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sibelhealth.core.sensor.Sensor

/**
 * [SensorConnectionViewModelFactory] to pass sensor value to View Model
 */
class SensorConnectionViewModelFactory(private val sensor: Sensor) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConnectionViewModel::class.java)) {
            return ConnectionViewModel(sensor) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}