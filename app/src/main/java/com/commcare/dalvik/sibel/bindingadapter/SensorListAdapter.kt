package com.commcare.dalvik.sibel.bindingadapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.commcare.dalvik.sibel.R
import com.commcare.dalvik.sibel.databinding.SensorListItemBinding
import com.commcare.dalvik.sibel.utils.COLOR_GREEN
import com.commcare.dalvik.sibel.utils.COLOR_TRANSPARENT
import com.commcare.dalvik.sibel.utils.animateBlinkView
import com.commcare.dalvik.sibel.utils.sensorService
import com.commcare.dalvik.sibel.viewmodels.SensorListModel
import com.commcare.dalvik.sibel.viewmodels.SensorListViewModel
import com.sibelhealth.bluetooth.sensor.serializeSensor
import com.sibelhealth.bluetooth.sensor.sibel.SibelSensor
import com.sibelhealth.core.sensor.Sensor
import com.sibelhealth.core.sensor.SensorType


private val TAG = SensorListAdapter::class.simpleName

/**
 * Sensors List Adapter
 */
class SensorListAdapter(private var sensorList: MutableList<SensorListModel>, private val viewModel: SensorListViewModel) : RecyclerView.Adapter<SensorListAdapter.SensorListItemViewHolder>() {

    /**
     * Create new views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SensorListItemViewHolder {
        val binding = SensorListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.sensor_list_item, parent, false)
        return SensorListItemViewHolder(binding)
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: SensorListItemViewHolder, position: Int) {
        Log.i(TAG, "I was in onBindViewHolder () ")
        holder.bindSensor(sensorList[position])
    }

    /**
     * Return the size of your data set (invoked by the layout manager)
     */
    override fun getItemCount() = sensorList.size

    /**
     * ViewHolder class to bind data set with list views
     */
    inner class SensorListItemViewHolder(
        private val sensorItemView: SensorListItemBinding
    ) : RecyclerView.ViewHolder(sensorItemView.root) {
        /**
         * Update sensor item view with values of discovered sensor
         */
        fun bindSensor(sensorListModel: SensorListModel) {
            Log.i(TAG, "I was in bindSensor() ")
            val sensor = sensorListModel.sensor
            val bundle = Bundle()
            bundle.putString(Sensor::class.java.canonicalName, Sensor.serializeSensor(sensor))
            if (sensor.sensorType == SensorType.CHEST || sensor.sensorType == SensorType.CHEST_NEONATE || sensor.sensorType == SensorType.LIMB || sensor.sensorType == SensorType.LIMB_NEONATE || sensor.sensorType == SensorType.ADAM ) {
                    sensorItemView.propertyTextView.text = "AdvertisementVersion: ${(sensor as SibelSensor).advertisementPacketVersionNumber ?: "-"}"

            }
            val targetResource = R.id.connectionFragment
            //val targetResource = when (sensor.sensorType) {
              //  SensorType.BIOLAND_BLOOD_PRESSURE_MONITOR -> ""
               // SensorType.BIOLAND_BLOOD_PRESSURE_MONITOR -> R.id.action_sensorListFragment_to_biolandBpMonitorSensorInfoFragment
           // }
            val onClickListener = Navigation.createNavigateOnClickListener(targetResource, bundle)

            val connectedSensorsList = sensorService.connectedSensors
//            if (!connectedSensorsList.isNullOrEmpty()) {
//                for (s in connectedSensorsList) {
//                    sensorItemView.apply {
//                        if (s.address == sensor.address) {
//                            setBackgroundColor(COLOR_GREEN)
//                        } else {
//                            setBackgroundColor(COLOR_TRANSPARENT)
//                        }
//                    }
//
//                }
//            }

            sensorItemView.sensorName.text = sensor.name
          //  sensorItemView.sensorType.text = sensor.sensorType.toString()
            sensorItemView.rssi.text = sensor.rssi.toString()
//            sensorItemView.serializeSensorButton.text = if (sensorListModel.isSerialized) {
//                "Remove"
//            } else {
//                "Save"
//            }

            Log.i(TAG, "Sensor Name Found: " + sensorItemView.sensorName.toString())

            sensorItemView.moreInfoButton.setOnClickListener(onClickListener)
            val color = if (sensor.isConnected) COLOR_GREEN else COLOR_TRANSPARENT
          //  sensorItemView.setBackgroundColor(color)
//            sensorItemView.serializeSensorButton.setOnClickListener {
//                val serializedSensor = Sensor.serializeSensor(sensor)
//                if (sensorListModel.isSerialized) {
//                    viewModel.serializeSensorToggle(serializedSensor, false)
//                    sensorListModel.isSerialized = false
//                } else {
//                    viewModel.serializeSensorToggle(serializedSensor, true)
//                    sensorListModel.isSerialized = true
//                }
//                notifyItemChanged(adapterPosition)
//            }

            val sensorObs = object : Sensor.Observer() {
                override fun onRssiUpdated(sensor: Sensor, rssi: Int) {
                    sensorItemView.rssi.text = sensor.rssi.toString()
                    animateBlinkView(sensorItemView.rssi)
                }
            }
            sensor.addSensorObserver(sensorObs)
        }
    }

    /**
     * Method for update the list in case of filter and search
     */
    fun updateFilteredList(list: MutableList<SensorListModel>) {
        sensorList = list
        notifyDataSetChanged()
    }
}