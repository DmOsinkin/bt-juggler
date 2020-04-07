package ru.osinkin.bt_juggler.presenter

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import ru.osinkin.bt_juggler.MainActivity
import ru.osinkin.bt_juggler.model.BluetoothModel
import ru.osinkin.bt_juggler.view.BluetoothScanView


class BluetoothScanningPresenter {
    private var view: BluetoothScanView? = null
    private val model = BluetoothModel()

    fun attachView(activity: MainActivity) {
        view = activity
    }

    fun detachView() {
        view = null
    }

    fun enableBluetoothIfNot() {
        if (!model.isBluetoothEnabled(view!!.getBluetoothManager())) {
            view!!.initBluetoothEnablingIntent()
        }
    }

    fun startOrEndScanning(isStart: Boolean) {
        model.scanLeDevice(isStart, mScanCallback)
    }

    private var mScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            view!!.addDevice(result.device)
        }
    }


    fun checkIfBluetoothSupported(): Boolean {
        // Checks if Bluetooth is supported on the device.
        return if (!model.isBluetoothSupported()) {
            view!!.showBluetoothIsNotSupported()
            false
        } else true
    }

    companion object {
        const val REQUEST_ENABLE_BT = 1
    }
}