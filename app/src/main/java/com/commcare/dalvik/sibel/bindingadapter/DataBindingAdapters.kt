package com.commcare.dalvik.sibel.bindingadapter

import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.commcare.dalvik.sibel.R


/**
 * Binds the text of scan sensors button
 */
@BindingAdapter("android:buttonText")
fun setButtonText(view: Button, text: String) {
    view.text = text
}

/**
 * Binds the text of the scanning button
 */
@BindingAdapter("android:scanningButtonText")
fun setScanningButtonText(view: Button, isScanning: Boolean) {
    if (isScanning) {
        view.text = view.context.resources.getString(R.string.stop_scan)
    } else {
        view.text = view.context.resources.getString(R.string.start_scan)
    }
}

/**
 * Binds the view that if it is enabled or not
 */
@BindingAdapter("android:isEnabled")
fun isEnabled(view: View, isEnabled: Boolean) {
    view.isEnabled = isEnabled
}

/**
 * Updates a View's visibility
 *
 * @param view      View to bind to
 * @param isVisible True if ImageView should be visible, false otherwise
 */
@BindingAdapter("android:isVisible")
fun setIsVisible(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

/**
 * Updates a View's visibility
 *
 * @param view      View to bind to
 * @param isVisible True if ImageView should be visible, false otherwise
 */
@BindingAdapter("android:isVisibleOrGone")
fun setIsVisibleOrGone(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

/**
 * Binds the height of view if it's match parent or wrap content
 */
@BindingAdapter("android:layout_height")
fun setViewHeight(view: View, isMatchParent: Boolean) {
    val layoutParams = view.layoutParams
    layoutParams.height = if (isMatchParent) {
        MATCH_PARENT
    } else {
        WRAP_CONTENT
    }
    view.layoutParams = layoutParams
}

/**
 * Binds the width of view if it's match parent or wrap content
 */
@BindingAdapter("android:layout_width")
fun setViewWidth(view: View, isMatchParent: Boolean) {
    val layoutParams = view.layoutParams
    layoutParams.width = if (isMatchParent) {
        MATCH_PARENT
    } else {
        WRAP_CONTENT
    }
    view.layoutParams = layoutParams
}


/**
 * Binds the text off of the toggle button
 */
@BindingAdapter("android:textOff")
fun setToggleButtonTextOff(btn: ToggleButton, text: String) {
    btn.textOff = text
}

/**
 * Binds the text on of the toggle button
 */
@BindingAdapter("android:textOn")
fun setToggleButtonTextOn(btn: ToggleButton, text: String) {
    btn.textOn = text
}


/**
 * Binds that if the charging button is checked or not
 */
@BindingAdapter("android:isButtonChecked")
fun setChargingButtonChecked(btn: ToggleButton, isChecked: Boolean) {
    btn.isChecked = isChecked
}


