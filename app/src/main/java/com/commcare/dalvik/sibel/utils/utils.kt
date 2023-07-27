package com.commcare.dalvik.sibel.utils

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.sibelhealth.bluetooth.sensorservice.SensorService
import com.sibelhealth.core.log.Logger
import com.sibelhealth.core.log.LoggerFactory
import java.text.SimpleDateFormat
import java.util.*

private const val FILE_SIZE_FACTOR = 1024

/**
 * Log factory instance to inject into [SensorService]
 */
val loggerFactory = object : LoggerFactory() {
    override fun createLog(logTag: String): Logger {
        return AndroidLogger(logTag)
    }
}

/**
 * String when data package returns no value
 */
const val NOT_APPLICABLE: String = "N/A"

/**
 * Green Color
 */
val COLOR_GREEN = Color.argb(0xff, 0x77, 0xdd, 0x77)

/**
 * Transparent Color
 */
const val COLOR_TRANSPARENT = Color.TRANSPARENT

/**
 * TEXT_COLOR_WHITE used in graph
 */
const val TEXT_COLOR_WHITE = Color.WHITE

/**
 * Key variable to for Encrypted files encryption key
 */
const val DOWNLOAD_FILE_ENCRYPTION_KEY = "ENCRYPTION_KEY"

/**
 * Blink Animation
 */
fun animateBlinkView(view: View) {
    Handler(Looper.getMainLooper()).post {
        val anim: ObjectAnimator = ObjectAnimator.ofInt(
            view,
            "backgroundColor",
            Color.RED,
            Color.TRANSPARENT
        )
        anim.duration = 10000
        anim.setEvaluator(ArgbEvaluator())
        anim.repeatMode = ValueAnimator.RESTART
        anim.repeatCount = 0
        anim.start()
    }
}

/**
 * Set the size of TextView
 */
fun TextView.setTextSizeSp(size: Float) {
    this.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
}

/**
 * Ellipsize start the TextView
 */
fun TextView.ellipsizeStart() {
    this.ellipsize = TextUtils.TruncateAt.START
}

/**
 * Ellipsize end the TextView
 */
fun TextView.ellipsizeEnd() {
    this.ellipsize = TextUtils.TruncateAt.END
}

/**
 * set max lines to one for TextView
 */
fun TextView.singleLine() {
    this.maxLines = 1
}

/**
 * Set layout params of streaming data view
 */
fun TextView.setStreamingViewParams() {
    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    this.layoutParams = layoutParams
}

/**
 * Set layout params of streaming data view with bottom margin
 */
fun TextView.setStreamingViewMarginParams() {
    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    layoutParams.setMargins(0, 0, 0, 5)
    this.layoutParams = layoutParams
}

/**
 * Set text color of text view to black
 */
fun TextView.textColorBlack() {
    this.setTextColor(Color.BLACK)
}

/**
 * Function to convert seconds into Milli Seconds
 */
fun secondsToMilliSec(seconds: Long): Long {
    return seconds * 1000
}

/**
 * Covert Seconds to Date String
 */
fun formatSecondsToDate(seconds: Long): String {
    val milliSeconds = secondsToMilliSec(seconds)
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(milliSeconds))
}

/**
 * Converts file size from bytes to Kilobytes or MegaBytes
 */
fun parseFileSize(sizeInBytes: Long): String {
    var size = sizeInBytes / FILE_SIZE_FACTOR
    return if (size >= FILE_SIZE_FACTOR) {
        size /= FILE_SIZE_FACTOR
        "$size mb"
    } else {
        "$size kb"
    }
}

/**
 * Add a new string element at the end of the string type array
 */
fun Array<String>.append(element: String): Array<String> {
    val list: MutableList<String> = this.toMutableList()
    list.add(element)
    return list.toTypedArray()
}

fun Long?.secondsToTimeSpanString(): String {
    if (this == null) return "-"
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val date = Date(this * 1000)
    return sdf.format(date) + "($this)"
}
