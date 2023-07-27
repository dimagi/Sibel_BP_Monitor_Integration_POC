package com.commcare.dalvik.sibel.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.commcare.dalvik.sibel.utils.sensorService
import com.sibelhealth.bluetooth.sensor.bloodpressuremonitor.biolandbloodpressuremonitor.Bioland20062bReadingError
import com.sibelhealth.bluetooth.sensor.bloodpressuremonitor.biolandbloodpressuremonitor.BiolandBloodPressureMonitor
import com.sibelhealth.bluetooth.sensorservice.datastream.BP2BloodPressureDataPackage
import com.sibelhealth.bluetooth.sensorservice.datastream.Bioland20062bProgressDataPackage
import com.sibelhealth.bluetooth.sensorservice.datastream.BloodPressureDataPackage
import com.sibelhealth.bluetooth.sensorservice.datastream.DataStreamObserver
import com.sibelhealth.bluetooth.sensorservice.datastream.StreamDataType
import com.sibelhealth.core.sensor.Sensor
import com.sibelhealth.core.sensor.SensorType

private val TAG = BiolandBPMonitoringViewModel::class.java.simpleName

class BiolandBPMonitoringViewModel (

    private val biolandBloodPressureMonitor: BiolandBloodPressureMonitor
) : ViewModel() {
    private var observerList = mutableListOf<Sensor.Observer>()
    private var sensorObserver: Sensor.Observer? = null
    private var dataStreamObserver: DataStreamObserver? = null
    private val _systolicBP = MutableLiveData("0")
    private val _diastolicBP = MutableLiveData("0")
    private val _pulserate = MutableLiveData("0")
    private val _startEnabled = MutableLiveData(true)
    private val _stopEnabled = MutableLiveData(false)
    private val _returnEnabled = MutableLiveData(false)

    val systolicBP: LiveData<String>
        get() = _systolicBP

    val diastolicBP: LiveData<String>
        get() = _diastolicBP

    val pulserate: LiveData<String>
        get() = _pulserate

    val startEnabled: LiveData<Boolean>
        get() = _startEnabled

    val stopEnabled: LiveData<Boolean>
        get() = _stopEnabled

    val returnEnabled: LiveData<Boolean>
        get() = _returnEnabled


    init {
        //initStreamView()
        _startEnabled.postValue(true)
        _stopEnabled.postValue(false)
        _returnEnabled.postValue(false)
        initDataStreamObserver()
        initSensorObserver()
    }

     fun onClickStartBPMeasurement(){
         _diastolicBP.postValue("0")
         _systolicBP.postValue("0")
         _pulserate.postValue("0")
         _startEnabled.postValue(false)
         _stopEnabled.postValue(true)
         _returnEnabled.postValue(false)
         Log.i(TAG, "Sensor Name : " + biolandBloodPressureMonitor.name + " Sensor is connected? : " + biolandBloodPressureMonitor.isConnected)
         Log.i(TAG, "dataStreamObserver in Start Measurement: " + dataStreamObserver.toString())
         biolandBloodPressureMonitor.sendStartMeasurementCommand()
     }

    fun onClickStopBPMeasurement(){
        _diastolicBP.postValue("0")
        _systolicBP.postValue("0")
        _pulserate.postValue("0")
        _startEnabled.postValue(true)
        _stopEnabled.postValue(false)
        _returnEnabled.postValue(false)
        biolandBloodPressureMonitor.stopMeasurementCommand()
    }

    private fun initSensorObserver() {
        addObserver(object : BiolandBloodPressureMonitor.Observer() {
            override fun onBatteryLevelUpdated(biolandBloodPressureMonitor: BiolandBloodPressureMonitor, batteryLevel: Double) {
                super.onBatteryLevelUpdated(biolandBloodPressureMonitor, batteryLevel)
                Log.i(TAG,"Battery level updated to $batteryLevel")
            }

            override fun onBloodPressureCollectionDone(biolandBloodPressureMonitor: BiolandBloodPressureMonitor, dataPackage: BloodPressureDataPackage) {
                super.onBloodPressureCollectionDone(biolandBloodPressureMonitor, dataPackage)
                dataPackage.data.forEach { (t, u) ->
                    dataPackage.timestamp.forEachIndexed { index, d ->
                        Log.i(TAG,"Type: ${t}, TS: ${dataPackage.timestamp[index]}, d: ${u.joinToString()}")
                    }
                    if(t.toString() == "BP_DIA") _diastolicBP.postValue(u.joinToString())
                    if(t.toString() == "BP_SYS") _systolicBP.postValue(u.joinToString())
                    if(t.toString()=="PR") _pulserate.postValue(u.joinToString())
                }
                Log.i(TAG, "Blood Pressure Collection Done !!")
                _startEnabled.postValue(true)
                _stopEnabled.postValue(false)
                _returnEnabled.postValue(true)
            }

            override fun onBloodPressureCollectionError(biolandBloodPressureMonitor: BiolandBloodPressureMonitor, readingErrorCode: Bioland20062bReadingError) {
                super.onBloodPressureCollectionError(biolandBloodPressureMonitor, readingErrorCode)
                Log.i(TAG,"Error occurred $readingErrorCode")
                _diastolicBP.postValue("Error")
                _systolicBP.postValue("Error")
                _pulserate.postValue("Error")
                _startEnabled.postValue(true)
                _stopEnabled.postValue(false)
                _returnEnabled.postValue(true)
            }

            override fun onBloodPressureCollectionProgressUpdated(biolandBloodPressureMonitor: BiolandBloodPressureMonitor, dataPackage: Bioland20062bProgressDataPackage) {
                super.onBloodPressureCollectionProgressUpdated(biolandBloodPressureMonitor, dataPackage)
                Log.i(TAG,"BP Measurement Completed")
                dataPackage.data.forEach { (t, u) ->
                    dataPackage.timestamp.forEachIndexed { index, d ->
                        Log.i(TAG,"Type: ${t}, TS: ${dataPackage.timestamp[index]}, d: ${u.joinToString()}")
                    }
                    _diastolicBP.postValue(u.joinToString())
                    _systolicBP.postValue(u.joinToString())
                    _pulserate.postValue("Monitoring...")

                }
                Log.i(TAG,"Heart Rate Detected: ${dataPackage.heartRateDetected}")
                _startEnabled.postValue(false)
                _stopEnabled.postValue(true)
                _returnEnabled.postValue(false)
            }

            override fun onBloodPressureHistoryReadDone(biolandBloodPressureMonitor: BiolandBloodPressureMonitor, vararg bpHistory: BloodPressureDataPackage) {
                super.onBloodPressureHistoryReadDone(biolandBloodPressureMonitor, *bpHistory)
                Log.i(TAG,"BP History Read Completed")
                biolandBloodPressureMonitor.bpHistory.forEach { dataPackage ->
                    dataPackage.data.forEach { (t, u) ->
                        dataPackage.timestamp.forEachIndexed { index, d ->
                            Log.i(TAG,"Type: ${t}, TS: ${dataPackage.timestamp[index]}, d: ${u.joinToString()}")
                        }
                    }
                }
            }

            override fun onInfoUpdated(biolandBloodPressureMonitor: BiolandBloodPressureMonitor, communicationProtocolVersion: String, serialNumber: String) {
                super.onInfoUpdated(biolandBloodPressureMonitor, communicationProtocolVersion, serialNumber)
                Log.i(TAG,"Info updated Vers: ${communicationProtocolVersion}, S/N: ${serialNumber}")
            }

            override fun onSerialNumberUpdated(biolandBloodPressureMonitor: BiolandBloodPressureMonitor, serialNumber: String) {
                super.onSerialNumberUpdated(biolandBloodPressureMonitor, serialNumber)
                Log.i(TAG,"Serial number updated: $serialNumber")
            }

            override fun onCommunicationProtocolVersionUpdated(biolandBloodPressureMonitor: BiolandBloodPressureMonitor, communicationProtocolVersion: String) {
                super.onCommunicationProtocolVersionUpdated(biolandBloodPressureMonitor, communicationProtocolVersion)
                Log.i(TAG,"Communication protocol ver updated: $communicationProtocolVersion")
            }
        }, SensorType.BIOLAND_BLOOD_PRESSURE_MONITOR)
    }

    override fun onCleared() {
        _startEnabled.postValue(true)
        _stopEnabled.postValue(false)
        _returnEnabled.postValue(false)
        if (sensorObserver != null) biolandBloodPressureMonitor.deleteSensorObserver(sensorObserver!!)
        if (dataStreamObserver != null) sensorService.deleteSensorServiceObserver(dataStreamObserver!!)
    }


    private fun initDataStreamObserver() {

        if (dataStreamObserver != null) sensorService.deleteSensorServiceObserver(dataStreamObserver!!)

        dataStreamObserver = object : DataStreamObserver() {

         override fun onBloodPressureDataUpdated(sensor: Sensor, dataPackage: BloodPressureDataPackage){
             Log.i(TAG, "BP Data Package's data is = : " + dataPackage.data )
                if (dataPackage is BP2BloodPressureDataPackage) {
                    if (dataPackage.isMeasurementFinished) {
                        val bpSys = dataPackage.data.get(StreamDataType.BP_SYS)
                        val bpDia = dataPackage.data.get(StreamDataType.BP_DIA)
                        val heartRate = dataPackage.data.get(StreamDataType.HR)
                        Log.i(TAG, "Systolic pressure value received: ${bpSys}. Diastolic pressure value received: ${bpDia}. Hear Rate Observed : ${heartRate}")
                    } else {
                        val bp = dataPackage.data.get(StreamDataType.BP)
                        Log.i(TAG, "Blood pressure value received: ${bp}.")
                    }
                }
            }
        }
        sensorService.addSensorServiceObserver(dataStreamObserver!!)
    }

    private fun addObserver(observer: Sensor.Observer, sensorType: SensorType) {
        if (sensorType == biolandBloodPressureMonitor.sensorType) {
            biolandBloodPressureMonitor.addSensorObserver(observer)
            observerList.add(observer)
        }
    }

}