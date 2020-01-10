package ru.osinkin.bt_juggler

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.osinkin.bt_juggler.adapters.PairedDevicesRecycleViewAdapter


class MainActivity : AppCompatActivity() {

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

        val layoutManager = LinearLayoutManager(this)
        pairedDevicesRecyclerView.layoutManager = layoutManager
        pairedDevicesRecyclerView.setHasFixedSize(true)

        prepareBLE()
    }

    override fun onResume() {
        super.onResume()
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (!mBluetoothAdapter.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
        } else {
            startScan()
            mAdapter =
                PairedDevicesRecycleViewAdapter(deviceList)
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
                deviceList.add(result.device)
            }
        }

    }

    /**
     * Initiates the scan for BLE devices according to the API level.
     */
    private fun startScan() {
        mLeScanner = mBluetoothAdapter.bluetoothLeScanner
        // start scan in low latency mode
        mLeScanner.startScan(
            ArrayList<ScanFilter>(),
            ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(),
            mScanCallback
        )
    }

    companion object {
        const val REQUEST_ENABLE_BT = 1
    }
}
