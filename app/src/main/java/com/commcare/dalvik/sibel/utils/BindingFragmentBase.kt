package com.commcare.dalvik.sibel.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.commcare.dalvik.sibel.BR


/**
 * Base to bind fragment with view model
 */
abstract class BindingFragmentBase<TBinding : ViewDataBinding, TViewModel : ViewModel> : Fragment() {

    /**
     * Fragment's view model variable
     */
    protected lateinit var viewModel: TViewModel

    var _binding: TBinding? = null
    protected val binding
        get() = _binding!!

    /**
     * Fragment's lifecycle onViewCreated() method
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = onCreateViewModel()
        _binding = DataBindingUtil.bind(requireView())!!
        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()
        binding.lifecycleOwner = this
    }

    /**
     * Fragment's lifecycle onCreateView() method
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    /**
     * Fragment's lifecycle onDestroy() method
     */
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /**
     * Abstract function to get viewmodel from fragment
     */
    protected abstract fun onCreateViewModel(): TViewModel

    /**
     * Abstract method to get layout of fragment
     */
    protected abstract fun getLayoutId(): Int
}