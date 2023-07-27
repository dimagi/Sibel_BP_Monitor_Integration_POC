package com.commcare.dalvik.sibel.viewmodels

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.commcare.dalvik.sibel.utils.sensorService
import com.sibelhealth.bluetooth.sensorservice.SensorService
import com.sibelhealth.bluetooth.sensorservice.connection.ConnectionEventObserver
import com.sibelhealth.bluetooth.sensorservice.scan.ScanErrorCode
import com.sibelhealth.bluetooth.sensorservice.scan.ScanEventObserver
import com.sibelhealth.core.sensor.Sensor
import com.sibelhealth.core.sensor.errorcode.ConnectionErrorCode
import com.sibelhealth.core.sensor.info.ConnectionInfo
import com.sibelhealth.core.sensor.status.ConnectionStatus

private val TAG = ConnectionViewModel::class.java.simpleName

/**
 * Connection view for selected sensor
 */
class ConnectionViewModel(
    /**
     * Selected Sensor
     */
    val sensor: Sensor
) : ViewModel() {
    private var sensorObserver: Sensor.Observer? = null
    private var connectionEventObserver: SensorService.Observer? = null
    private var shouldScan = false
    private val scanEventObserver = object : ScanEventObserver() {
        override fun onSensorScanStarted() {
        }

        override fun onSensorScanStopped() = handleScan()

        override fun onSensorScanFailed(scanErrorCode: ScanErrorCode) = handleScan()

        private fun handleScan() {
            if (shouldScan) {
                _isScanning.postValue(true)
                sensorService.startScan(SCAN_TIMEOUT_IN_MILLISECONDS)
            } else {
                _isScanning.postValue(false)
                sensorService.stopScan()
            }
        }
    }

    private val _connectButtonText = MutableLiveData("Connect")
    private val _isConnectedText = MutableLiveData("Click Connect")
    private val _isProceedEnabled = MutableLiveData(false)

    val isProceedEnabled: LiveData<Boolean>
        get() = _isProceedEnabled

    /**
     * LiveData string that will set as the text of connect Button
     */
    val connectButtonText: LiveData<String>
        get() = _connectButtonText

    val isConnectedText: LiveData<String>
        get() = _isConnectedText

    /**
     * Observable variable that will observe if the autoConnect Checkbox is checked or not
     */
    val isAutoConnectChecked = ObservableBoolean(true)

    val isSkipInitChecked = ObservableBoolean(false)

    val timeoutRequest = MutableLiveData<String?>("30000")

    private val _isScanning = MutableLiveData(sensorService.isScanning)
    val isScanning: LiveData<Boolean> get() = _isScanning

    fun onScanButtonTapped() {
        if (isScanning.value!!) {
            shouldScan = false
            _isScanning.postValue(false)
            _isProceedEnabled.postValue(true)
            sensorService.stopScan()
        } else {
            shouldScan = true
            _isScanning.postValue(true)
            _isProceedEnabled.postValue(false)
            sensorService.startScan(SCAN_TIMEOUT_IN_MILLISECONDS)
        }
    }

    /**
     * calls when connect button is clicked
     */
    fun onConnectButtonTapped() {
        when (sensor.connectionStatus) {
            ConnectionStatus.CONNECTED,
            ConnectionStatus.CONNECTING,
            ConnectionStatus.CONNECTION_LOST -> {
                disconnectSensor()
            }
            ConnectionStatus.DISCONNECTED -> {
                sensor.connect(
                    ConnectionInfo.Builder()
                        .autoConnect(isAutoConnectChecked.get())
                        .timeout(timeoutRequest.value?.toLong() ?: 30000.toLong())
                        .skipInit(isSkipInitChecked.get())
                        .build()
                )
            }
        }
    }

    /**
     * Disconnect Sensor method
     */
    private fun disconnectSensor() {
        sensor.disconnect()
    }

    init {
        if (sensor.isConnected) {
            _connectButtonText.postValue("Disconnect")
            _isConnectedText.postValue("Connected")
            _isProceedEnabled.postValue(true)
        }
        initScanEventObserver()
        initConnectionEventObserver()
        initSensorObserver()
    }

    private fun initSensorObserver() {
        sensorObserver = object : Sensor.Observer() {
            override fun onConnectionStatusUpdated(sensor: Sensor, connectionStatus: ConnectionStatus, isConnected: Boolean) {
                when (connectionStatus) {
                    ConnectionStatus.CONNECTING -> {
                        _connectButtonText.postValue("Connecting")
                        _isConnectedText.postValue("Connecting")
                        _isProceedEnabled.postValue(false)
                    }
                    ConnectionStatus.CONNECTED -> {
                        _connectButtonText.postValue("Disconnect")
                        _isConnectedText.postValue("Disconnected")
                        _isProceedEnabled.postValue(true)
                    }
                    else -> {

                    }
                }
            }

            override fun onConnectionFailed(sensor: Sensor, errorCode: ConnectionErrorCode) {
                _connectButtonText.postValue("Connect")
                _isConnectedText.postValue("Connection Failed")
                _isProceedEnabled.postValue(false)
                Log.i(TAG, "Failed to connect ${sensor.name} due to $errorCode")
            }
        }
        sensor.addSensorObserver(sensorObserver!!)
    }

    private fun initScanEventObserver() {
        sensorService.addSensorServiceObserver(scanEventObserver)
    }

    private fun initConnectionEventObserver() {
        connectionEventObserver = object : ConnectionEventObserver() {
            override fun onSensorConnectionFailed(
                sensor: Sensor,
                connectionErrorCode: ConnectionErrorCode
            ) {
                Log.d(TAG, "[${sensor.name}] connection has failed.")
            }

            override fun onSensorConnected(sensor: Sensor) {
                _connectButtonText.postValue("Disconnect")
                _isConnectedText.postValue("Connected")
                _isProceedEnabled.postValue(true)
            }

            override fun onSensorConnectionLost(sensor: Sensor) {
                Log.d(TAG, "[${sensor.name}] connection has lost.")
                _connectButtonText.postValue("Disconnect")
                _isConnectedText.postValue("Connection Lost")
                _isProceedEnabled.postValue(false)
            }

            override fun onSensorDisconnected(sensor: Sensor) {
                Log.d(TAG, "[${sensor.name}] is disconnected.")
                _connectButtonText.postValue("Connect")
                _isConnectedText.postValue("Disconnected")
                _isProceedEnabled.postValue(false)
            }
        }
        sensorService.addSensorServiceObserver(connectionEventObserver!!)
    }

    /**
     * Default function
     */
    override fun onCleared() {
        if (sensorObserver != null) sensor.deleteSensorObserver(sensorObserver!!)
        if (connectionEventObserver != null) sensorService.deleteSensorServiceObserver(connectionEventObserver!!)
        sensorService.deleteSensorServiceObserver(scanEventObserver)
        if (sensorService.isScanning) sensorService.stopScan()
    }

    companion object {
        /**
         * Scan duration of an hour
         */
        private const val SCAN_TIMEOUT_IN_MILLISECONDS = (60 * 60 * 1000).toLong()
    }
}