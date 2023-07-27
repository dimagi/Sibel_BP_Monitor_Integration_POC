package com.commcare.dalvik.sibel

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.commcare.dalvik.sibel.bindingadapter.SensorListAdapter
import com.commcare.dalvik.sibel.databinding.FragmentBpMonitoringDeviceConnectedBinding
import com.commcare.dalvik.sibel.utils.BindingFragmentBase
import com.commcare.dalvik.sibel.viewmodels.SensorListViewModel
import com.sibelhealth.core.sensor.SensorType


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private val TAG = BPMonitoringDeviceConnected::class.simpleName

/**
 * A simple [Fragment] subclass.
 * Use the [BPMonitoringDeviceConnected.newInstance] factory method to
 * create an instance of this fragment.
 */
class BPMonitoringDeviceConnected : BindingFragmentBase<FragmentBpMonitoringDeviceConnectedBinding, SensorListViewModel>(){
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private val sensorTypes = arrayOf(NO_SENSOR_TYPE_FILTER) + SensorType.values().map { it.sensorTypeName() }
    /**
     * fragment lifecycle onActivityCreated Method
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prepareSensorList()

        viewModel.discoveredSensorsLiveData.observe(viewLifecycleOwner) {
            //filterSensorData()
            prepareSensorList()
        }

        viewModel.scanDurationInputError.observe(viewLifecycleOwner) { scanningStatus ->
            scanningStatus.getContentIfNotHandled().let {
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.scanningStatus.observe(viewLifecycleOwner) { scanningStatus ->
            scanningStatus.getContentIfNotHandled().let {
                if (!it.isNullOrEmpty()) {
                    Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.refreshSensorsList.setOnRefreshListener {
            viewModel.isFirstScanUponRequest = true
            viewModel.startScan()
            binding.refreshSensorsList.isRefreshing = false
        }

    }

    private fun prepareSensorList() {
        Log.i(TAG, "viewModel.discoveredSensorsLiveData.value : " + viewModel.discoveredSensorsLiveData.value.toString())
        viewAdapter = SensorListAdapter(viewModel.discoveredSensorsLiveData.value!!, viewModel)
        Log.i(TAG, "I was in prepareSensorList() ")
        binding.sensorList.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(this.context)
            adapter = viewAdapter
        }
    }

    /**
     * Method use to hide keyboard
     */
    private fun View.hideKeyBoard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    /**
     * override method to return the viewmodel of fragment
     */
    override fun onCreateViewModel(): SensorListViewModel {
        return ViewModelProvider(this).get(SensorListViewModel::class.java)
    }

    /**
     * override method that will return the layout file of fragment
     */
    override fun getLayoutId(): Int = R.layout.fragment_bp_monitoring_device_connected

    companion object {
        private const val NO_SENSOR_TYPE_FILTER = "Filter by Sensor Type"
        private const val NO_FILTER = 0

        /**
         * Name for ANNE Chest sensor
         */
        private const val ANNE_CHEST_NAME = "ANNE Chest"

        /**
         * Name for ANNE Chest Neonate sensor
         */
        private const val ANNE_CHEST_NEONATE_NAME = "ANNE Chest Neonate"

        /**
         * Name for ANNE Limb sensor
         */
        private const val ANNE_LIMB_NAME = "ANNE Limb"

        /**
         * Name for ANNE Limb Neonate sensor
         */
        private const val ANNE_LIMB_NEONATE_NAME = "ANNE Limb Neonate"

        /**
         * Name for OxySmart pulse oximeter
         */
        private const val OXY_SMART_PULSE_OXIMETER_NAME = "OxySmart Pulse Ox"

        /**
         * Name for Berry pulse oximeter
         */
        private const val BERRY_PULSE_OXIMETER_NAME = "Berry Pulse Ox"

        /**
         * Name for Nonin 3150 pulse oximeter
         */
        private const val NONIN_3150_PULSE_OXIMETER_NAME = "Nonin 3150 Pulse Ox"

        /**
         * Name for BP2 BP Monitor
         */
        private const val BP2_BLOOD_PRESSURE_MONITOR_NAME = "BP2 Blood Pressure Monitor"

        /**
         * Name for ADAM sensor
         */
        private const val ADAM_NAME = "ADAM"

        /**
         * Name for Unique weight scale
         */
        private const val UNIQUE_WEIGHT_SCALE_NAME = "Unique Weight Scale"

        /**
         * Name for JoyTech Thermometer
         */
        private const val DMT_4735B_THERMOMETER_NAME = "JoyTech Thermometer"

        /**
         * Name for G427B Glucose Meter
         */
        private const val G427B_GLUCOSE_METER = "G427B Glucose Meter"

        /**
         * Name for Bioland Blood Pressure Monitor
         */
        private const val BIOLAND_BP_MONITOR = "Bioland BP Monitor"

        /**
         * Name for Tiansheng weight scale
         */
        private const val TIANSHENG_WEIGHT_SCALE_NAME = "Tiansheng Weight Scale"

        /**
         * Returns proper name for the sensor type
         */
        private fun SensorType.sensorTypeName(): String {
            return when (this) {
                SensorType.CHEST -> ANNE_CHEST_NAME
                SensorType.CHEST_NEONATE -> ANNE_CHEST_NEONATE_NAME
                SensorType.LIMB -> ANNE_LIMB_NAME
                SensorType.LIMB_NEONATE -> ANNE_LIMB_NEONATE_NAME
                SensorType.OXY_SMART_PULSE_OXIMETER -> OXY_SMART_PULSE_OXIMETER_NAME
                SensorType.BERRY_PULSE_OXIMETER -> BERRY_PULSE_OXIMETER_NAME
                SensorType.NONIN_3150_PULSE_OXIMETER -> NONIN_3150_PULSE_OXIMETER_NAME
                SensorType.BP2_BLOOD_PRESSURE_MONITOR -> BP2_BLOOD_PRESSURE_MONITOR_NAME
                SensorType.ADAM -> ADAM_NAME
                SensorType.UNIQUE_WEIGHT_SCALE -> UNIQUE_WEIGHT_SCALE_NAME
                SensorType.DMT_4735B_THERMOMETER -> DMT_4735B_THERMOMETER_NAME
                SensorType.G427B_GLUCOSE_METER -> G427B_GLUCOSE_METER
                SensorType.BIOLAND_BLOOD_PRESSURE_MONITOR -> BIOLAND_BP_MONITOR
                SensorType.TIANSHENG_WEIGHT_SCALE -> TIANSHENG_WEIGHT_SCALE_NAME
            }
        }
    }
}