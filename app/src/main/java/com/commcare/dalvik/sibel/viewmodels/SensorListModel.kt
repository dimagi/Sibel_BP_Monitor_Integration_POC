package com.commcare.dalvik.sibel.viewmodels

import com.sibelhealth.core.sensor.Sensor

/**
 * Sensor List Model with two variables
 */
data class SensorListModel(
    /**
     * [sensor] represents the item that need to b displayed
     */
    val sensor: Sensor,
    /**
     * @[isSerialized] will show if the sensor is serialized or not.
     */
    var isSerialized: Boolean
)
