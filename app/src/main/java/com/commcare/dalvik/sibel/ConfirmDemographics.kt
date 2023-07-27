package com.commcare.dalvik.sibel

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.commcare.dalvik.sibel.databinding.FragmentConfirmDemographicsBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



/**
 * A simple [Fragment] subclass.
 * Use the [ConfirmDemographics.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConfirmDemographics : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentConfirmDemographicsBinding?=null
    private val binding get() = _binding!!
    private val TAG = ConfirmDemographics::class.simpleName
    private var bluetooth_enabled = "false"
    private var bluetoothStatus = ""

    private fun populateIntentData() {
        arguments?.getString("beneficiary_name")?.apply {
            val person_name = getString(R.string.person_name, this)
            binding.personName.text = person_name
        }
        arguments?.getString("phone_number")?.apply {
            val phone_number = getString(R.string.person_phone_number, this)
            binding.personPhoneNumber.text = phone_number
        }
        bluetooth_enabled = arguments?.getString("bluetooth_enabled").toString()
        Log.i(TAG, "Bluetooth Status inside fragment  : " + bluetooth_enabled)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentConfirmDemographicsBinding.inflate(inflater, container, false)
        val view = binding.root
        populateIntentData()

        if(bluetooth_enabled.equals("true")){
            bluetoothStatus = getString(R.string.connected)
            binding.confirmDemographics.isEnabled=true
            binding.confirmDemographics.isClickable=true
            binding.bluetoothStatusHelp.visibility = View.INVISIBLE
        }
        else {
            bluetoothStatus = getString(R.string.not_connected)
            binding.confirmDemographics.isEnabled=false
            binding.confirmDemographics.isClickable=false
            binding.bluetoothStatusHelp.visibility = View.VISIBLE
        }

        val bluetooth_status_display = getString(R.string.bluetooth_status, bluetoothStatus)
        binding.bluetoothStatus.text = bluetooth_status_display

        binding.confirmDemographics.setOnClickListener {
            Log.i(TAG, "Proceed Button Clicked ")
            findNavController().navigate(R.id.action_confirmDemographics_to_BPMonitoringDeviceConnected ,arguments)
        }
        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ConfirmDemographics.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfirmDemographics().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}