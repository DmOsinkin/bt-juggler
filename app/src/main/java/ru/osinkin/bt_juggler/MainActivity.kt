package ru.osinkin.bt_juggler

import android.Manifest
import android.app.ListActivity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.osinkin.bt_juggler.adapters.PairedDevicesRecycleViewAdapter


class MainActivity : ListActivity() {

    /**
     * private properties
     */
    private val deviceList = mutableListOf<BluetoothDevice>()
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mScanCallback: ScanCallback
    private lateinit var mAdapter: PairedDevicesRecycleViewAdapter
    private lateinit var mLeScanner: BluetoothLeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show()
            finish()
        }

        val layoutManager = LinearLayoutManager(this)
        pairedDevicesRecyclerView.layoutManager = layoutManager
        pairedDevicesRecyclerView.setHasFixedSize(true)

        prepareBLE()
    }

    override fun onResume() {
        super.onResume()

        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Toast.makeText(
                    this,
                    "The permission to get BLE location data is required",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), 1
                )
            }
        } else {
            Toast.makeText(this, "Location permissions already granted", Toast.LENGTH_SHORT).show()
        }

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (!mBluetoothAdapter.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
        } else {
            startScan()
            mAdapter = PairedDevicesRecycleViewAdapter(deviceList)
            pairedDevicesRecyclerView.adapter = mAdapter
        }
    }

    private fun prepareBLE() {
        // Initializes Bluetooth adapter.
        val bluetoothManager: BluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        initScanCallback()
    }

    private fun initScanCallback() {
        mScanCallback = object : ScanCallback() {

            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                println("=========================================onScanResult(): $result \n$callbackType")
                deviceList.add(result.device)
            }
        }

    }

    /**
     * Initiates the scan for BLE devices according to the API level.
     */
    private fun startScan() {
        if (Build.VERSION.SDK_INT >= 23) {
            println("===============startScan()")
            mLeScanner = mBluetoothAdapter.bluetoothLeScanner
            // start scan in low latency mode
            mLeScanner.startScan(mScanCallback)
        }
    }

    companion object {
        const val REQUEST_ENABLE_BT = 1
    }
}
