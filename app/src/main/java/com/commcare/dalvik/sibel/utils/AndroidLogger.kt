package com.commcare.dalvik.sibel.utils
import android.util.Log
import com.sibelhealth.core.log.Logger
import com.sibelhealth.core.log.LoggerFactory

/**
 * [AndroidLogger] printing logs via the `android.util.Log`.
 */
class AndroidLogger
/**
 * @param tag The tag that is present in
 * android.util.Log.d(TAG, "Hello world");
 */(
    /** Tag for the log message  */
    private val tag: String,
) : Logger() {
    /** If set, only this level and above logs will be output by this logger  */
    private var _level: LoggerFactory.Level? = null

    /**
     * Checks whether a log level has been set either at the local level or, if not, the global one
     * @return Appropriate log level if one has been set by the user
     */
    private val level: LoggerFactory.Level get() = _level ?: LoggerFactory.level

    override val isDebugEnabled: Boolean
        get() = (level <= LoggerFactory.Level.DEBUG)

    override val isErrorEnabled: Boolean
        get() {
            return (level <= LoggerFactory.Level.ERROR)
        }
    override val isInfoEnabled: Boolean
        get() {
            return (level <= LoggerFactory.Level.INFO)
        }
    override val isVerboseEnabled: Boolean
        get() {
            return (level <= LoggerFactory.Level.VERBOSE)
        }
    override val isWarnEnabled: Boolean
        get() {
            return (level <= LoggerFactory.Level.WARN)
        }
    override val isFatalEnabled: Boolean
        get() {
            return (level <= LoggerFactory.Level.FATAL)
        }

    override fun verboseImpl(message: Any) {
        if (level <= LoggerFactory.Level.VERBOSE) {
            Log.v(tag, reformatMessage(message.toString()))
        }
    }

    override fun verboseImpl(message: Any, t: Throwable) {
        if (level <= LoggerFactory.Level.VERBOSE) {
            Log.v(tag, reformatMessage(message.toString()), t)
        }
    }

    override fun debugImpl(message: Any) {
        if (level <= LoggerFactory.Level.DEBUG) {
            Log.d(tag, reformatMessage(message.toString()))
        }
    }

    override fun debugImpl(message: Any, t: Throwable) {
        if (level <= LoggerFactory.Level.DEBUG) {
            Log.d(tag, reformatMessage(message.toString()), t)
        }
    }

    override fun infoImpl(message: Any) {
        if (level <= LoggerFactory.Level.INFO) {
            Log.i(tag, reformatMessage(message.toString()))
        }
    }

    override fun infoImpl(message: Any, t: Throwable) {
        if (level <= LoggerFactory.Level.INFO) {
            Log.i(tag, reformatMessage(message.toString()), t)
        }
    }

    override fun warnImpl(message: Any) {
        if (level <= LoggerFactory.Level.WARN) {
            Log.w(tag, reformatMessage(message.toString()))
        }
    }

    override fun warnImpl(message: Any, t: Throwable) {
        if (level <= LoggerFactory.Level.WARN) {
            Log.w(tag, reformatMessage(message.toString()), t)
        }
    }

    override fun errorImpl(message: Any) {
        if (level <= LoggerFactory.Level.ERROR) {
            Log.e(tag, reformatMessage(message.toString()))
        }
    }

    override fun errorImpl(message: Any, t: Throwable) {
        if (level <= LoggerFactory.Level.ERROR) {
            Log.e(tag, reformatMessage(message.toString()), t)
        }
    }

    override fun fatalImpl(message: Any) {
        // Unable to implement fatal log
    }

    override fun fatalImpl(message: Any, t: Throwable) {
        // Unable to implement fatal log
    }

    private fun reformatMessage(message: String) : String{
        return "[Sibel Playground] $message"
    }
}