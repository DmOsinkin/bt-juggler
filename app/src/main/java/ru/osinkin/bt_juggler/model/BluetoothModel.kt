package ru.osinkin.bt_juggler.model

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.os.Handler
import androidx.core.content.ContextCompat
import ru.osinkin.bt_juggler.adapters.LeDeviceListAdapter

class BluetoothModel {

    /**
     * private properties
     */
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mLeScanner: BluetoothLeScanner
    private var mScanning = false
    private var mHandler = Handler()

    fun scanLeDevice(enable: Boolean, mScanCallback: ScanCallback) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed({
                mScanning = false
                mLeScanner.stopScan(mScanCallback)
            }, SCAN_PERIOD)
            mScanning = true
            mLeScanner.startScan(mScanCallback)
        } else {
            mScanning = false
            mLeScanner.stopScan(mScanCallback)
        }
    }

    fun isBluetoothEnabled(bluetoothManager: BluetoothManager): Boolean {
        mBluetoothAdapter = bluetoothManager.adapter
        mLeScanner = mBluetoothAdapter.bluetoothLeScanner
        return mBluetoothAdapter.isEnabled
    }

    fun isBluetoothSupported(): Boolean {
        return this.mBluetoothAdapter != null
    }

    companion object {
        private const val SCAN_PERIOD: Long = 100000
    }

}