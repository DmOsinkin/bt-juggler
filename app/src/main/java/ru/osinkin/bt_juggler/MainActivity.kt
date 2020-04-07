package ru.osinkin.bt_juggler

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.osinkin.bt_juggler.adapters.LeDeviceListAdapter
import ru.osinkin.bt_juggler.adapters.LeDeviceListAdapter.OnItemCLickListener
import ru.osinkin.bt_juggler.presenter.BluetoothScanningPresenter
import ru.osinkin.bt_juggler.view.BluetoothScanView


class MainActivity : AppCompatActivity(), OnItemCLickListener, BluetoothScanView {
    private val bluetoothScanningPresenter = BluetoothScanningPresenter()
    private lateinit var deviceListAdapter: LeDeviceListAdapter

    init {
        bluetoothScanningPresenter.attachView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        checkPermissions()
    }

    private fun initComponents() {
        devicesRecycleView.layoutManager = LinearLayoutManager(this)
        devicesRecycleView.setHasFixedSize(true)


        bluetoothScanningPresenter.enableBluetoothIfNot()
        bluetoothScanningSwitch.isEnabled = bluetoothScanningPresenter.checkIfBluetoothSupported()

        // Initializes list view adapter.
        deviceListAdapter = LeDeviceListAdapter(mutableListOf(), this)
        devicesRecycleView.adapter = deviceListAdapter

        bluetoothScanningSwitch.setOnCheckedChangeListener { _, isChecked ->
            bluetoothScanningPresenter.startOrEndScanning(isChecked)
        }
    }

    private fun checkPermissions() {
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show()
            finish()
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

    override fun onDestroy() {
        super.onDestroy()
        bluetoothScanningPresenter.detachView()
    }

    override fun getBluetoothManager() =
        getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    override fun onItemClick(position: Int) {
        val device: BluetoothDevice = deviceListAdapter.getDevice(position)

        val intent = Intent(this@MainActivity, DeviceControlActivity::class.java)
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.name)
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.address)
        Log.d("MainActivity", device.name + ": " + device.address)
//        if (mScanning) {
//            mLeScanner.stopScan(mScanCallback)
//            mScanning = false
//        }
        bluetoothScanningPresenter.startOrEndScanning(false)
        startActivity(intent)
    }

    override fun initBluetoothEnablingIntent() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, BluetoothScanningPresenter.REQUEST_ENABLE_BT)
    }

    override fun addDevice(bluetoothDevice: BluetoothDevice) {
        deviceListAdapter.addDevice(bluetoothDevice)
        deviceListAdapter.notifyDataSetChanged()
    }

    override fun showBluetoothIsNotSupported() {
        Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show()
    }
}
