package ru.osinkin.bt_juggler

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.LeScanCallback
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
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.osinkin.bt_juggler.adapters.LeDeviceListAdapter
import ru.osinkin.bt_juggler.adapters.LeDeviceListAdapter.OnItemCLickListener


class MainActivity : AppCompatActivity(), OnItemCLickListener {

    /**
     * private properties
     */
    private val deviceList = mutableListOf<BluetoothDevice>()
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mLeDeviceListAdapter: LeDeviceListAdapter
    private lateinit var mLeScanner: BluetoothLeScanner
    private var mScanning = false
    private var mHandler: Handler? = null

    private var mScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            mLeDeviceListAdapter.addDevice(result.device)
            mLeDeviceListAdapter.notifyDataSetChanged()
            invalidateOptionsMenu()
        }
    }
    // Device scan callback.
    private val mLeScanCallback = LeScanCallback { device, rssi, scanRecord ->
        runOnUiThread {
            mLeDeviceListAdapter.addDevice(device)
            mLeDeviceListAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mHandler = Handler()

        val layoutManager = LinearLayoutManager(this)
        pairedDevicesRecyclerView.layoutManager = layoutManager
        pairedDevicesRecyclerView.setHasFixedSize(true)
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show()
            finish()
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        mLeScanner = mBluetoothAdapter.bluetoothLeScanner

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
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

    }

    override fun onResume() {
        super.onResume()
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        // Initializes list view adapter.
        mLeDeviceListAdapter = LeDeviceListAdapter(deviceList, this)
        pairedDevicesRecyclerView.adapter = mLeDeviceListAdapter
        scanLeDevice(true)
    }

    private fun scanLeDevice(enable: Boolean) {
        if (enable) { // Stops scanning after a pre-defined scan period.
            mHandler?.postDelayed({
                mScanning = false
//                if (Build.VERSION.SDK_INT < 23) {
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback)
//                } else {
//                }
                mLeScanner.stopScan(mScanCallback)
                invalidateOptionsMenu()
            }, SCAN_PERIOD)
            mScanning = true
//            if (Build.VERSION.SDK_INT < 23) {
//                mBluetoothAdapter.startLeScan(mLeScanCallback)
//            } else {
//
//            }
            mLeScanner.startScan(mScanCallback)
        } else {
            mScanning = false
//            if (Build.VERSION.SDK_INT < 23) {
//                mBluetoothAdapter.stopLeScan(mLeScanCallback)
//            } else {
//            }
            mLeScanner.stopScan(mScanCallback)
        }
        invalidateOptionsMenu()
    }

    companion object {
        private const val SCAN_PERIOD: Long = 10000
        private const val REQUEST_ENABLE_BT = 1
    }

    override fun onItemClick(position: Int) {
        val device: BluetoothDevice = mLeDeviceListAdapter.getDevice(position)
        val intent = Intent(this@MainActivity, DeviceControlActivity::class.java)
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.name)
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.address)
        Log.d("MainActivity", device.name + ": " + device.address)
        if (mScanning) {
            mLeScanner.stopScan(mScanCallback)
            mScanning = false
        }
        startActivity(intent)
    }
}
