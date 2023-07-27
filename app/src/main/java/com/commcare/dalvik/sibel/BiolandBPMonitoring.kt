package com.commcare.dalvik.sibel
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.commcare.dalvik.sibel.databinding.FragmentBiolandBpMonitoringBinding
import com.commcare.dalvik.sibel.utils.BindingFragmentBase
import com.commcare.dalvik.sibel.viewmodels.BiolandBPMonitorViewModelFactory
import com.commcare.dalvik.sibel.viewmodels.BiolandBPMonitoringViewModel
import com.sibelhealth.bluetooth.sensor.deserializeSensor
import com.sibelhealth.core.sensor.Sensor

private val TAG = BiolandBPMonitoring::class.java.simpleName

class BiolandBPMonitoring : BindingFragmentBase<FragmentBiolandBpMonitoringBinding, BiolandBPMonitoringViewModel>() {

    private var serializedSensor: String? = null
    private lateinit var sensor: Sensor


    override fun onCreateViewModel(): BiolandBPMonitoringViewModel {

        Log.i(TAG, "Arguments : " + arguments.toString())
        serializedSensor = arguments?.getString(Sensor::class.qualifiedName)
        sensor = Sensor.deserializeSensor(serializedSensor!!)
        return ViewModelProvider(this, BiolandBPMonitorViewModelFactory(sensor)).get(
            BiolandBPMonitoringViewModel::class.java
        )
    }
    override fun getLayoutId(): Int = R.layout.fragment_bioland_bp_monitoring

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = onCreateViewModel()
        _binding = DataBindingUtil.bind(requireView())!!
        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        binding.returnBp.setOnClickListener(){
            val intent = Intent().apply {
                putExtra("bp_diastolic", binding.viewModel?.diastolicBP?.value.toString())
                putExtra("bp_systolic", binding.viewModel?.systolicBP?.value.toString())
                putExtra("pulse_rate", binding.viewModel?.pulserate?.value.toString())
            }

            (activity as MainActivity).dispatchResult(intent)
        }
    }

}