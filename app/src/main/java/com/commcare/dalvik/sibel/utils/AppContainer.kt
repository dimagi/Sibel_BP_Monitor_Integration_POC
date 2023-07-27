package com.commcare.dalvik.sibel.utils

import com.sibelhealth.bluetooth.sensorservice.DefaultSensorService
import com.sibelhealth.bluetooth.sensorservice.SensorService
import com.sibelhealth.core.sensor.Sensor

/**
 * [SensorService] instance used across the application to communicate with [Sensor] objects over bluetooth layer.
 */
val sensorService : SensorService by lazy { DefaultSensorService() }