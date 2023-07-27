package com.commcare.dalvik.sibel.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.commcare.dalvik.sibel.utils.Event
import com.commcare.dalvik.sibel.utils.secondsToMilliSec
import com.commcare.dalvik.sibel.utils.sensorService
import com.sibelhealth.bluetooth.sensor.deserializeSensor
import com.sibelhealth.bluetooth.sensor.serializeSensor
import com.sibelhealth.bluetooth.sensorservice.scan.ScanErrorCode
import com.sibelhealth.bluetooth.sensorservice.scan.ScanEventObserver
import com.sibelhealth.core.sensor.Sensor
import io.paperdb.Paper


private val TAG = SensorListViewModel::class.simpleName
/**
 * View Model class of sensor list fragment
 */
class SensorListViewModel : ViewModel() {

    private var scanningTimeout = 0L

    companion object {
        /**
         *  Constant Key Value to store and retrieve sensors
         */
        const val SERIALIZED_SENSORS = "SERIALIZED_SENSORS"
    }

    /**
     * Serialized Sensor String
     */
    private var serializedSensors = Paper.book().read(SERIALIZED_SENSORS, mutableListOf<String>())

    private val _discoveredSensorsLiveData = MutableLiveData<MutableList<SensorListModel>>(ArrayList())

    /**
     * LiveData list of all discovered sensors
     */
    val discoveredSensorsLiveData: LiveData<MutableList<SensorListModel>>
        get() = _discoveredSensorsLiveData


    private val _isScanButtonEnabled = MutableLiveData(true)

    /**
     * LiveData Boolean that will set as scan Button will be enabled or not
     */
    val isScanButtonEnabled: LiveData<Boolean>
        get() = _isScanButtonEnabled


    private val _scanButtonText = MutableLiveData("")

    /**
     * LiveData string that will set as the text of scan Button
     */
    val scanButtonText: LiveData<String>
        get() = _scanButtonText


    private val _scanDurationInputError = MutableLiveData<Event<String>>()

    /**
     * LiveData string that will be displayed as a toast if scan duration time is not correct
     */
    val scanDurationInputError: LiveData<Event<String>>
        get() = _scanDurationInputError


    /**
     * LiveData string that will bind as the text of scan duration edittext
     */
    val scanDurationText = MutableLiveData<String>("10")


    private val _isScanning = MutableLiveData(false)

    /**
     * LiveData boolean that will set as the sensor is scanning or not
     */
    val isScanning: LiveData<Boolean>
        get() = _isScanning


    /**
     * Live Data boolean that tells if the scan shall loop once started
     */
    val shouldLoopScan = MutableLiveData(false)

    private val _scanningStatus = MutableLiveData<Event<String>>()

    /**
     *Live data value that will visualize the scanning status
     */
    val scanningStatus: LiveData<Event<String>>
        get() = _scanningStatus

    /**
     * Indicates if the scan to be instantiated is requested by the user or the program
     */
    var isFirstScanUponRequest = false

    private var scanCanceledByUser = false

    private val scanEventObserver = object : ScanEventObserver() {
        init {
            _scanButtonText.postValue("Start Scan")
        }

        override fun onSensorScanStarted() {
            Log.i(TAG, "Started scan")
            _scanButtonText.postValue("Stop Scan")
            _scanningStatus.postValue(Event("Started scan"))
            if (shouldLoopScan.value != true || isFirstScanUponRequest) {
                isFirstScanUponRequest = false
                discoveredSensorsLiveData.value?.clear()
                sensorService.connectedSensors.forEach { sensor ->
                    if (serializedSensors.contains(Sensor.serializeSensor(sensor))) {
                        discoveredSensorsLiveData.value?.add(SensorListModel(sensor, true))
                    } else {
                        discoveredSensorsLiveData.value?.add(SensorListModel(sensor, false))
                    }
                }
            }

            _discoveredSensorsLiveData.postValue(discoveredSensorsLiveData.value)
            _isScanning.postValue(true)
        }

        override fun onSensorDiscovered(sensor: Sensor) {
            if (discoveredSensorsLiveData.value!!.any { it.sensor.address == sensor.address }) return
            Log.i(TAG, "SensorDiscovered: ${sensor.name}")
            if (serializedSensors.contains(Sensor.serializeSensor(sensor))) {
                discoveredSensorsLiveData.value?.add(SensorListModel(sensor, true))
            } else {
                discoveredSensorsLiveData.value?.add(SensorListModel(sensor, false))
            }
            _discoveredSensorsLiveData.postValue(discoveredSensorsLiveData.value)
        }

        override fun onSensorScanFailed(scanErrorCode: ScanErrorCode) {
            Log.i(TAG, "Failed to start scan.")
            _scanButtonText.postValue("SCAN FAILED. TAP to restart ($scanErrorCode)")
            _isScanButtonEnabled.postValue(true)
            _isScanning.postValue(false)
        }

        override fun onSensorScanStopped() {
            Log.i(TAG, "Stopped scan")
            if (shouldLoopScan.value == true && !scanCanceledByUser) {
                startScan()
            } else {
                _scanningStatus.postValue(Event("Stopped scan"))
                _scanButtonText.postValue("Start Scan")
                scanDurationText.value = ""
                _isScanButtonEnabled.postValue(true)
                _isScanning.postValue(false)
            }
        }
    }

    /**
     * Default function of View Model
     */
    override fun onCleared() {
        sensorService.deleteSensorServiceObserver(scanEventObserver)
    }

    init {
        sensorService.addSensorServiceObserver(scanEventObserver)
        if (serializedSensors.isNotEmpty()) {
            try {
                discoveredSensorsLiveData.value?.clear()
                for (s in serializedSensors) {
                    val deSerializedSensor = Sensor.deserializeSensor(s)
                    discoveredSensorsLiveData.value?.add(SensorListModel(deSerializedSensor, true))
                    _discoveredSensorsLiveData.postValue(discoveredSensorsLiveData.value)
                }
            } catch (e: Exception) {
                Log.e(TAG, "${e.printStackTrace()}")
            }
        }
    }

    /**
     * Invoked when scan button is tapped
     */
    fun onScanButtonTapped() {

        Log.i(TAG, "Is Scanning : " + isScanning.value.toString())
        if (isScanning.value!!) {
            scanCanceledByUser = true
            stopScan()
        } else {
            isFirstScanUponRequest = true
            startScan()
        }
    }

    /**
     * Start bluetooth sensors scanning.
     * Before invoking the functionality, the variable [isFirstScanUponRequest] shall be initialized.
     */
    fun startScan() {
        scanCanceledByUser = false
        val time = scanDurationText.value.toString()
        if (time.isNotEmpty() && time != "null") {
            time.toIntOrNull()?.let {
                if (it < 10) {
                    _scanDurationInputError.postValue(Event("Scan time should be at least 10 sec."))
                } else {
                    try {
                        scanningTimeout = secondsToMilliSec(time.trim().toLong())
                        sensorService.startScan(scanningTimeout)
                    } catch (e: Exception) {
                        Log.e(TAG, "startScan: ${e.message}")
                        _scanDurationInputError.postValue(Event("Invalid scan time entered!"))
                    }
                }
            } ?: _scanDurationInputError.postValue(Event("Enter a valid scan time!"))
        } else {
            sensorService.startScan()
        }
    }

    /**
     * stops bluetooth sensors scanning
     */
    private fun stopScan() {
        sensorService.stopScan()
    }

    /**
     * Serialize/De Serialize sensor from adapter
     */
    fun serializeSensorToggle(serializedSensor: String, isSerialized: Boolean) {
        if (isSerialized) {
            serializedSensors.add(serializedSensor)
        } else {
            serializedSensors.remove(serializedSensor)
        }
        Paper.book().write(SERIALIZED_SENSORS, serializedSensors)
    }
}