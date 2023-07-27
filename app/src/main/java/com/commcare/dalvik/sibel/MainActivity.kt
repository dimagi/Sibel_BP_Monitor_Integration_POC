package com.commcare.dalvik.sibel
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.commcare.dalvik.sibel.databinding.ActivityMainBinding
import com.commcare.dalvik.sibel.utils.sensorService
import com.sibelhealth.core.BuildConfig
import io.paperdb.Paper
import kotlin.random.Random

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private lateinit var navHostFragment: NavHostFragment
    private val TAG = MainActivity::class.simpleName
    private var bluetooth_enabled : String = "false"

    /**
     * Location Permission Request Code
     */
    private val STORAGE_REQUEST_CODE = Random(10).nextBits(16)
    private val LOCATION_REQUEST_CODE = Random(20).nextBits(16)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Paper.init(this)
        sensorService.initialize(applicationContext)

        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        check_bluetooth_status()

        intent.putExtra("bluetooth_enabled" , bluetooth_enabled)
        val bundle = intent.extras ?: bundleOf()
        //Log.i(TAG, "intent has bluetooth_enabled ? : " + intent.hasExtra("bluetooth_enabled"))
        navController
            .setGraph(R.navigation.commcare_sibel_fragment_navigation_map, bundle)

        //inflateNavGraphWithSensor()

        setupActionBarWithNavController(navController)
        supportActionBar?.title = getString(R.string.top_bar_confirm_person)

        var title = getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "+" + BuildConfig.VERSION_CODE
        if (BuildConfig.DEBUG) {
            title += " (${BuildConfig.BUILD_TYPE})"
        }

        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        Log.i(TAG, "Start application")

    }

    private fun inflateNavGraphWithSensor() {
        val bundle = intent.extras ?: bundleOf()  //not null
        intent.putExtras(bundle)
        val inflater = navController.navInflater
        val navGraph: Int = R.navigation.commcare_sibel_fragment_navigation_map //not null
        val graph = inflater.inflate(navGraph) //not null
        //Log.i(TAG, "intent.extras = " + intent.extras)  //intent.extras is not null
        //Log.i(TAG, "bundle = " + bundle)
        graph.addInDefaultArgs(intent.extras)
        Log.i(TAG, "Argument Passed from Main Activity: " + graph.arguments.keys + " And other graph objects are: " )

    }

    private fun check_bluetooth_status(){
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val adapter: BluetoothAdapter? = bluetoothManager.adapter
        if (adapter != null) {
            bluetooth_enabled = adapter.isEnabled.toString()
            Log.i(TAG, "Bluetooth status : " + bluetooth_enabled)
        }
    }

    override fun onStart() {
        super.onStart()
        requestLocationPermission()
        requestStoragePermission()
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED) {
                val requestedPermissions = arrayOf(
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                )
                ActivityCompat.requestPermissions(this, requestedPermissions, LOCATION_REQUEST_CODE)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val requestedPermissions = arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                ActivityCompat.requestPermissions(this, requestedPermissions, LOCATION_REQUEST_CODE)
            }
        }
    }

    // Request permission
    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val requestedPermissions =
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ActivityCompat.requestPermissions(this, requestedPermissions, STORAGE_REQUEST_CODE)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun getNavHostId(): Int {
        return R.id.nav_host_fragment
    }

     fun dispatchResult(intent: Intent) {
        setResult(111, intent)
        finish()
    }
}