package com.commcare.dalvik.sibel


import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.commcare.dalvik.sibel.databinding.FragmentConnectionBinding
import com.commcare.dalvik.sibel.utils.BindingFragmentBase
import com.commcare.dalvik.sibel.viewmodels.ConnectionViewModel
import com.commcare.dalvik.sibel.viewmodels.SensorConnectionViewModelFactory
import com.sibelhealth.bluetooth.sensor.deserializeSensor
import com.sibelhealth.core.sensor.Sensor

class ConnectionFragment : BindingFragmentBase<FragmentConnectionBinding, ConnectionViewModel>() {
    private var serializedSensor: String? = null
    private lateinit var sensor: Sensor

    override fun onCreateViewModel(): ConnectionViewModel {
        serializedSensor = arguments?.getString(Sensor::class.qualifiedName)
        sensor = Sensor.deserializeSensor(serializedSensor!!)
        return ViewModelProvider(this, SensorConnectionViewModelFactory(sensor)).get(ConnectionViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = onCreateViewModel()
        _binding = DataBindingUtil.bind(requireView())!!
        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()
        binding.lifecycleOwner = this
        binding.proceed.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_connectionFragment_to_biolandBPMonitoring, arguments)
        )
    }

    override fun getLayoutId(): Int = R.layout.fragment_connection
}