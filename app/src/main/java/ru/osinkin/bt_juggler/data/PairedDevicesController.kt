package ru.osinkin.bt_juggler.data

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context

/**
 * this class is my attempt to create a singleton that uses a context from any activity or service
 */
class PairedDevicesController(var context: Context) {

        fun getPairedDevices(): MutableList<String> {
            val bluetoothAdapter: BluetoothAdapter
            // Initializes Bluetooth adapter.
            val bluetoothManager : BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager.adapter
            val pairedDevices = bluetoothAdapter.bondedDevices
            val pairedDeviceItemList = mutableListOf<PairedDeviceItem>()
            pairedDevices.forEach { device ->
                pairedDeviceItemList.add(PairedDeviceItem(device))
            }
            val pairedDevicesList = mutableListOf<String>()
            for (bluetoothDevice in pairedDeviceItemList) pairedDevicesList.add(bluetoothDevice.name)
            return pairedDevicesList
        }

    companion object {
        private var pairedDevicesController: PairedDevicesController? = null
        fun getPairedDevicesController(context: Context): PairedDevicesController {
            if (pairedDevicesController == null) {
                pairedDevicesController = PairedDevicesController(context)
            }
            return PairedDevicesController(context)
        }
    }
}