package ru.osinkin.bt_juggler.data

import android.bluetooth.BluetoothAdapter

class PairedDevicesController {
    companion object {
        fun getPairedDevices(): MutableList<String> {
            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val pairedDevices = mBluetoothAdapter.bondedDevices
            val pairedDevicesList = mutableListOf<String>()
            for (bluetoothDevice in pairedDevices) pairedDevicesList.add(bluetoothDevice.name)
            return pairedDevicesList
        }
    }
}